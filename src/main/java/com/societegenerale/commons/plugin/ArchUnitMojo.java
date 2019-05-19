package com.societegenerale.commons.plugin;

import com.societegenerale.commons.plugin.model.ConfigurableRule;
import com.societegenerale.commons.plugin.model.Rules;
import com.societegenerale.commons.plugin.service.RuleInvokerService;
import com.societegenerale.commons.plugin.utils.ArchUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.societegenerale.commons.plugin.utils.ArchUtils.PREFIX_ARCH_VIOLATION_MESSAGE;
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

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    public Rules getRules() {
        return rules;
    }

    private RuleInvokerService ruleInvokerService = new RuleInvokerService();

    @Override
    public void execute() throws MojoFailureException {

        String ruleFailureMessage;
        try {
            ClassLoader contextClassLoader = fetchContextClassLoader();
            if (!rules.isValid()) {
                throw new MojoFailureException("Arch unit Plugin should have at least one preconfigured/configurable rule");
            }
            ruleFailureMessage = invokeRules(contextClassLoader);
        } catch (final Exception e) {
            throw new MojoFailureException(e.toString(), e);
        }

        if (!StringUtils.isEmpty(ruleFailureMessage)) {
            throw new MojoFailureException(PREFIX_ARCH_VIOLATION_MESSAGE + ruleFailureMessage);
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


    private String invokeRules(ClassLoader contextClassLoader) throws ReflectiveOperationException {

        StringBuilder errorListBuilder = new StringBuilder(StringUtils.EMPTY);

        if (rules.hasSomePreConfiguredRules()) {
            for (String rule : rules.getPreConfiguredRules()) {
                Class<?> testClass = contextClassLoader.loadClass(rule);
                String errorMessage = ruleInvokerService.invokePreConfiguredRule(testClass, projectPath);
                errorListBuilder.append(ArchUtils.prepareErrorMessageForRuleFailures(rule, errorMessage));

            }
        }

        if (rules.hasSomeConfigurableRules()) {
            for (ConfigurableRule rule : rules.getConfigurableRules()) {
                Class<?> customRuleClass = contextClassLoader.loadClass(rule.getRule());
                String errorMessage = ruleInvokerService.invokeConfigurableRules(customRuleClass, rule, projectPath);
                errorListBuilder.append(ArchUtils.prepareErrorMessageForRuleFailures(rule.getRule(), errorMessage));
            }
        }

        return errorListBuilder.toString();
    }
}