package com.societegenerale.commons.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.System.lineSeparator;
import static java.net.URLClassLoader.newInstance;

/**
 * @goal generate
 * @phase process-classes
 * @configurator include-project-dependencies
 * @requiresDependencyResolution compile+runtime
 */
@Mojo(name = "arch-test", requiresDependencyResolution = ResolutionScope.TEST)
public class ArchUnitMojo extends AbstractMojo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArchUnitMojo.class);

    /**
     * Skips all processing performed by this plugin.
     *
     * <pre>
     * {@code
     * ...
     * <configuration>
     *   <skip>false</skip>
     * </configuration>
     * ...
     * }
     * </pre>
     */
    @Parameter(defaultValue = "false", property = "archunit.skip", required = false)
    private boolean skip;

    @Parameter(property = "projectPath")
    private String projectPath = "./target";

    @Parameter(property = "rules")
    private Rules rules;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    public Rules getRules() {
        return rules;
    }

    private RuleInvokerService ruleInvokerService = new RuleInvokerService();

    private static final String PREFIX_ARCH_VIOLATION_MESSAGE = "ArchUnit Maven plugin reported architecture failures listed below :";

    @Override
    public void execute() throws MojoFailureException {

        if("pom".equals(mavenProject.getPackaging())){
            LOGGER.debug("module packaging is 'pom', so skipping execution");
            return;
        }

        if (skip) {
            LOGGER.info("Skipping execution.");
            return;
        }

        if (!rules.isValid()) {
            throw new MojoFailureException("Arch unit Plugin should have at least one preconfigured/configurable rule");
        }

        String ruleFailureMessage;
        try {
            configureContextClassLoader();

            ruleFailureMessage = invokeRules();
        } catch (final Exception e) {
            throw new MojoFailureException(e.getMessage(), e);
        }

        if (!StringUtils.isEmpty(ruleFailureMessage)) {
            throw new MojoFailureException(PREFIX_ARCH_VIOLATION_MESSAGE + ruleFailureMessage);
        }
    }

    private void configureContextClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {

        List<URL> urls = new ArrayList<>();
        List<String> elements = mavenProject.getTestClasspathElements();
        for (String element : elements) {
            urls.add(new File(element).toURI().toURL());
        }

        ClassLoader contextClassLoader = newInstance(urls.toArray(new URL[0]), Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }

    private String invokeRules() {

        StringBuilder errorListBuilder = new StringBuilder();

        for (String rule : rules.getPreConfiguredRules()) {
            String errorMessage = ruleInvokerService.invokePreConfiguredRule(rule, projectPath);
            errorListBuilder.append(prepareErrorMessageForRuleFailures(rule, errorMessage));
        }

        for (ConfigurableRule rule : rules.getConfigurableRules()) {
            String errorMessage = ruleInvokerService.invokeConfigurableRules(rule, projectPath);
            errorListBuilder.append(prepareErrorMessageForRuleFailures(rule.getRule(), errorMessage));
        }

        return errorListBuilder.toString();
    }

    private String prepareErrorMessageForRuleFailures(String rule, String errorMessage) {

        StringBuilder errorBuilder = new StringBuilder();
        if (StringUtils.isNotEmpty(errorMessage)) {
            errorBuilder
                    .append("Rule Violated - ").append(rule).append(lineSeparator())
                    .append(errorMessage)
                    .append(lineSeparator());
        }
        return errorBuilder.toString();
    }
}
