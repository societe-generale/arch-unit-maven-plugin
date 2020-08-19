package com.societegenerale.commons.plugin.maven;

import com.societegenerale.commons.plugin.model.RootClassFolder;
import com.societegenerale.commons.plugin.service.ScopePathProvider;

public class MavenScopePathProvider implements ScopePathProvider {

    @Override
    public RootClassFolder getMainClassesPath() {
        return new RootClassFolder("target/classes");
    }

    @Override
    public RootClassFolder getTestClassesPath() {
        return new RootClassFolder("target/test-classes");
    }
}
