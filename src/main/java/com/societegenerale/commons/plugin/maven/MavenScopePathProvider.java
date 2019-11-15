package com.societegenerale.commons.plugin.maven;

import com.societegenerale.commons.plugin.service.ScopePathProvider;

public class MavenScopePathProvider implements ScopePathProvider {

    @Override
    public String getMainClassesPath() {
        return "/classes";
    }

    @Override
    public String getTestClassesPath() {
        return "/test-classes";
    }
}
