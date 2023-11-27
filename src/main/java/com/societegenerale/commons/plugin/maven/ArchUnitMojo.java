package com.societegenerale.commons.plugin.maven;

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

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        if (!rules.toCoreRules().isValid()) {
            throw new MojoFailureException("Arch unit Plugin should have at least one preconfigured/configurable rule");
        }

        String ruleFailureMessage;

        Rules coreRules;
        List<MavenProject> targetProjects;
        if ("pom".equals(mavenProject.getPackaging())) {
            coreRules = rules.toCoreRules(true);
            targetProjects = mavenProject.getCollectedProjects();
        }
        else {
            coreRules = rules.toCoreRules(false);
            targetProjects = List.of(mavenProject);
        }

        ruleFailureMessage = executeArchUnitRules(coreRules, targetProjects);

        if (!StringUtils.isEmpty(ruleFailureMessage)) {
            if(!noFailOnError) {
                throw new MojoFailureException(PREFIX_ARCH_VIOLATION_MESSAGE + ruleFailureMessage);
            }

            getLog().info(PREFIX_ARCH_VIOLATION_MESSAGE + ruleFailureMessage);
        }
    }

    private String executeArchUnitRules(Rules coreRules, List<MavenProject> projects) throws MojoFailureException {
        if (!coreRules.isValid()) {
            String debugMessage = "no rule apply for projects";
            if (!projects.isEmpty()) {
                debugMessage += ": %s".formatted(projects.stream()
                        .map(MavenProject::getName)
                        .collect(Collectors.joining(";")));
            }
            getLog().debug(debugMessage);
            return null;
        }
        configureProperties();
        try {
            configureContextClassLoader(projects);
            final Log mavenLogAdapter = new MavenLogAdapter(getLog());

            ruleInvokerService = new RuleInvokerService(mavenLogAdapter,
                    projects.stream().map(MavenScopePathProvider::new).collect(Collectors.toList()),
                    excludedPaths,
                    projectBuildDir);

            return ruleInvokerService.invokeRules(coreRules);
        } catch (final Exception e) {
            throw new MojoFailureException(e.getMessage(), e);
        }
    }

    private void configureProperties() {
        if (!properties.isEmpty()) {
            getLog().debug("configuring ArchUnit properties");
            final ArchConfiguration archConfiguration = ArchConfiguration.get();
            properties.forEach(archConfiguration::setProperty);
        }
    }

    private void configureContextClassLoader(List<MavenProject> projects)
            throws DependencyResolutionRequiredException, MalformedURLException {

        List<URL> urls = new ArrayList<>();
        for (MavenProject project : projects) {
            for (String element : project.getTestClasspathElements()) {
                urls.add(new File(element).toURI().toURL());
            }
        }

        ClassLoader contextClassLoader = newInstance(urls.toArray(new URL[0]),
                Thread.currentThread().getContextClassLoader());
        Thread.currentThread().setContextClassLoader(contextClassLoader);
    }

    void setProjectBuildDir(final String projectBuildDir) {
        this.projectBuildDir = projectBuildDir;
    }
}
