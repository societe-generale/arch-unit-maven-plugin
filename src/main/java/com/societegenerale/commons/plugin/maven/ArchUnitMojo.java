package com.societegenerale.commons.plugin.maven;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.societegenerale.commons.plugin.maven.model.MavenRules;
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

import static java.net.URLClassLoader.newInstance;
import static java.util.Collections.emptyList;

/**
 * @goal generate
 * @phase process-classes
 * @configurator include-project-dependencies
 * @requiresDependencyResolution compile+runtime
 */
@Mojo(name = "arch-test", requiresDependencyResolution = ResolutionScope.TEST)
public class ArchUnitMojo extends AbstractMojo {
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

    @Parameter(property = "excludedPaths")
    private List<String> excludedPaths  = emptyList();

    @Parameter(property = "rules")
    private MavenRules rules;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    private MavenProject mavenProject;

    public MavenRules getRules() {
        return rules;
    }

    private RuleInvokerService ruleInvokerService ;

    private static final String PREFIX_ARCH_VIOLATION_MESSAGE = "ArchUnit Maven plugin reported architecture failures listed below :";

    @Override
    public void execute() throws MojoFailureException {
        if (skip) {
            getLog().info("Skipping execution.");
            return;
        }

        Rules coreRules=rules.toCoreRules();

        if (!coreRules.isValid()) {
            throw new MojoFailureException("Arch unit Plugin should have at least one preconfigured/configurable rule");
        }

        if ("pom".equals(mavenProject.getPackaging())) {
            getLog().debug("module packaging is 'pom', so skipping execution");
            return;
        }
        String ruleFailureMessage;
        try {
            configureContextClassLoader();

            ruleInvokerService = new RuleInvokerService(new MavenLogAdapter(getLog()), new MavenScopePathProvider(mavenProject), excludedPaths);

            ruleFailureMessage = ruleInvokerService.invokeRules(coreRules);
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

}
