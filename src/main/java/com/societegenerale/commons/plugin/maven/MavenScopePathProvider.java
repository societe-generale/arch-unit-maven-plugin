package com.societegenerale.commons.plugin.maven;

import com.societegenerale.commons.plugin.model.RootClassFolder;
import com.societegenerale.commons.plugin.service.ScopePathProvider;
import org.apache.maven.project.MavenProject;

public class MavenScopePathProvider implements ScopePathProvider {

    private final MavenProject project;

    public MavenScopePathProvider(MavenProject project) {
        this.project = project;
    }

    @Override
    public RootClassFolder getMainClassesPath() {
        return new RootClassFolder(project.getBuild().getOutputDirectory());
    }

    @Override
    public RootClassFolder getTestClassesPath() {
        return new RootClassFolder(project.getBuild().getTestOutputDirectory());
    }
}
