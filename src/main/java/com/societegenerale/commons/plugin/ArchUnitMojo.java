package com.societegenerale.commons.plugin;

import com.societegenerale.commons.plugin.model.ConfigurableRule;
import com.societegenerale.commons.plugin.model.Rules;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.societegenerale.commons.plugin.utils.ArchUtils.getPackageNameOnWhichRulesToApply;
import static com.societegenerale.commons.plugin.utils.ArchUtils.importAllClassesInPackage;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static java.net.URLClassLoader.newInstance;

/**
 * @goal generate
 * @phase process-classes
 * @configurator include-project-dependencies
 * @requiresDependencyResolution compile+runtime
 */
@Mojo(name = "arch-test", requiresDependencyResolution = ResolutionScope.TEST)
public class ArchUnitMojo extends AbstractMojo {

    @Parameter(property = "projectPath")
    private String projectPath;

    @Parameter(property = "rules")
    private Rules rules;

    private static final String EXECUTE_METHOD_NAME = "execute";

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    public Rules getRules() {
        return rules;
    }

    @Override
    public void execute() throws MojoFailureException {

        try {
            ClassLoader contextClassLoader = fetchContextClassLoader();

            List<String> preConfiguredRules = rules.getPreConfiguredRules();

            if (preConfiguredRules == null && rules.getConfigurableRules() == null) {
                throw new MojoFailureException("Arch unit Plugin should have at least one preconfigured/configurable rule");
            }

            if (preConfiguredRules != null) {
                invokePreConfiguredRules(contextClassLoader);
            }
            if (rules.getConfigurableRules() != null) {
                invokeConfigurableRules(contextClassLoader);
            }

        } catch (final Exception e) {
            throw new MojoFailureException(e.toString(), e);
        }
    }

    private ClassLoader fetchContextClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {

        List<URL> urls = new ArrayList<>();
        List<String> elements = mavenProject.getTestClasspathElements();
        for (String element : elements) {
            urls.add(new File(element).toURI().toURL());
        }

        ClassLoader contextClassLoader = newInstance(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(contextClassLoader);
        return contextClassLoader;
    }


    private void invokePreConfiguredRules(ClassLoader contextClassLoader)
            throws ReflectiveOperationException {

        for (String rule : rules.getPreConfiguredRules()) {
            Class<?> testClass = contextClassLoader.loadClass(rule);
            Method method = testClass.getDeclaredMethod(EXECUTE_METHOD_NAME, String.class);
            method.invoke(testClass.newInstance(), projectPath);
        }
    }

    private void invokeConfigurableRules(ClassLoader contextClassLoader)
            throws ReflectiveOperationException {

        for (ConfigurableRule rule : rules.getConfigurableRules()) {
            Class<?> customRuleClass = contextClassLoader.loadClass(rule.getRule());

            Method[] methods = customRuleClass.getDeclaredMethods();

            Map<String, Method> archConditionReturningMethods = new HashMap<>();
            Map<String, Field> archRuleFields = new HashMap<>();

            for (Method method : methods) {
                if (method.getReturnType().equals(ArchCondition.class)) {
                    archConditionReturningMethods.put(method.getName(), method);
                }
            }

            Field[] fields = customRuleClass.getDeclaredFields();

            for (Field field : fields) {
                if (field.getType().equals(ArchRule.class)) {
                    archRuleFields.put(field.getName(), field);
                }
            }

            String packageOnRuleToApply = getPackageNameOnWhichRulesToApply(rule);

            List<String> ruleChecks = rule.getChecks();
            if (ruleChecks != null) {

                applyParticularChecksInRuleClass(ruleChecks, customRuleClass, archConditionReturningMethods,
                        archRuleFields, packageOnRuleToApply);

            } else {
                applyAllChecksInRuleClass(customRuleClass, archConditionReturningMethods, archRuleFields,
                        packageOnRuleToApply);
            }
        }
    }

    private void applyAllChecksInRuleClass(Class<?> testClass,
                                           Map<String, Method> archConditionReturningMethods, Map<String, Field> archRuleFields, String packageOnRuleToApply)
            throws ReflectiveOperationException {

        for (Method method : archConditionReturningMethods.values()) {
            invokeArchUnitCondition(projectPath, method, testClass, packageOnRuleToApply);
        }
        for (Field field : archRuleFields.values()) {
            invokeArchCustomRule(projectPath, field, testClass, packageOnRuleToApply);
        }
    }

    private void applyParticularChecksInRuleClass(List<String> ruleChecks, Class<?> testClass,
                                                  Map<String, Method> archConditionReturningMethods, Map<String, Field> archRuleFields,
                                                  String packageOnRuleToApply)
            throws ReflectiveOperationException {

        for (String check : ruleChecks) {

            if (archConditionReturningMethods.containsKey(check)) {

                invokeArchUnitCondition(projectPath, archConditionReturningMethods.get(check), testClass,
                        packageOnRuleToApply);

            } else if (archRuleFields.containsKey(check)) {

                invokeArchCustomRule(projectPath, archRuleFields.get(check), testClass, packageOnRuleToApply);

            }
        }
    }

    private static void invokeArchCustomRule(String projectPath, Field field, Class<?> testClass, String packageOnRuleToApply)
            throws ReflectiveOperationException {
        field.setAccessible(true);
        ArchRule archRule = (ArchRule) field.get(testClass.newInstance());
        archRule.check(importAllClassesInPackage(projectPath, packageOnRuleToApply));
    }

    @SuppressWarnings(value = "unchecked")
    private static void invokeArchUnitCondition(String projectPath, Method method, Class<?> ruleClass, String packageOnRuleToApply) throws ReflectiveOperationException {
        Object ruleObject = method.invoke(ruleClass.newInstance());
        ArchCondition<JavaClass> archCondition = (ArchCondition<JavaClass>) ruleObject;
        classes().should(archCondition).check(importAllClassesInPackage(projectPath, packageOnRuleToApply));
    }

}
