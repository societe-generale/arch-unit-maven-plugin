package com.societegenerale.commons.plugin.maven;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.societegenerale.commons.plugin.Log;
import com.societegenerale.commons.plugin.maven.model.MavenRules;
import com.societegenerale.commons.plugin.model.Rules;
import com.societegenerale.commons.plugin.service.RuleInvokerService;
import com.tngtech.archunit.ArchConfiguration;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import static java.net.URLClassLoader.newInstance;
import static java.util.Collections.emptyList;

@Mojo(name = "arch-test",
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        configurator = "include-project-dependencies")
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

    @Parameter(defaultValue = "${project.build.directory}")
    private String projectBuildDir;

    @Parameter(property = "noFailOnError", defaultValue = "false")
    private boolean noFailOnError;

    @Parameter(defaultValue = "true")
    private boolean fallbackToRootDirectory = true;

    @Parameter
    private Map<String, String> properties = new HashMap<>();

    public MavenRules getRules() {
        return rules;
    }

    private RuleInvokerService ruleInvokerService;

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

        if (!properties.isEmpty()) {
            getLog().debug("configuring ArchUnit properties");
            final ArchConfiguration archConfiguration = ArchConfiguration.get();
            properties.forEach(archConfiguration::setProperty);
        }

        String ruleFailureMessage;
        try {
            configureContextClassLoader();
            final Log mavenLogAdapter = new MavenLogAdapter(getLog());

            ruleInvokerService = new RuleInvokerService(mavenLogAdapter, new MavenScopePathProvider(mavenProject), excludedPaths, projectBuildDir, fallbackToRootDirectory);

            ruleFailureMessage = ruleInvokerService.invokeRules(coreRules);
        } catch (final Exception e) {
            throw new MojoFailureException(e.getMessage(), e);
        }

        if (!StringUtils.isEmpty(ruleFailureMessage)) {
            if(!noFailOnError) {
                throw new MojoFailureException(PREFIX_ARCH_VIOLATION_MESSAGE + ruleFailureMessage);
            }

            getLog().warn(PREFIX_ARCH_VIOLATION_MESSAGE + ruleFailureMessage);
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
    void setProjectBuildDir(final String projectBuildDir) {
        this.projectBuildDir = projectBuildDir;
    }
}
