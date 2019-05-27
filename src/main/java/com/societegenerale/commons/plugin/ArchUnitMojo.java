package com.societegenerale.commons.plugin;

import com.societegenerale.commons.plugin.model.ConfigurableRule;
import com.societegenerale.commons.plugin.model.Rules;
import com.societegenerale.commons.plugin.service.RuleInvokerService;
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
    private String projectPath="./target";

    @Parameter(property = "rules")
    private Rules rules;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    public Rules getRules() {
        return rules;
    }

    private RuleInvokerService ruleInvokerService = new RuleInvokerService();

    private static final String PREFIX_ARCH_VIOLATION_MESSAGE = "ArchUnit Maven plugin reported architecture failures listed below :";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public void execute() throws MojoFailureException {

        if (!rules.isValid()) {
            throw new MojoFailureException("Arch unit Plugin should have at least one preconfigured/configurable rule");
        }

        String ruleFailureMessage;
        try {
            ClassLoader contextClassLoader = fetchContextClassLoader();

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
                Class<?> ruleToApplyClass = contextClassLoader.loadClass(rule);
                String errorMessage = ruleInvokerService.invokePreConfiguredRule(ruleToApplyClass, projectPath);
                errorListBuilder.append(prepareErrorMessageForRuleFailures(rule, errorMessage));

            }
        }

        if (rules.hasSomeConfigurableRules()) {
            for (ConfigurableRule rule : rules.getConfigurableRules()) {
                Class<?> customRuleToApplyClass = contextClassLoader.loadClass(rule.getRule());
                String errorMessage = ruleInvokerService.invokeConfigurableRules(customRuleToApplyClass, rule, projectPath);
                errorListBuilder.append(prepareErrorMessageForRuleFailures(rule.getRule(), errorMessage));
            }
        }

        return errorListBuilder.toString();
    }

    private String prepareErrorMessageForRuleFailures(String rule, String errorMessage) {

        StringBuilder errorBuilder = new StringBuilder();
        if (StringUtils.isNotEmpty(errorMessage)) {
            errorBuilder
                    .append("Rule Violated - ").append(rule).append(LINE_SEPARATOR)
                    .append(errorMessage)
                    .append(LINE_SEPARATOR);
        }
        return errorBuilder.toString();
    }
}