package com.societegenerale.commons.plugin.model;

import org.apache.maven.plugins.annotations.Parameter;

import java.util.ArrayList;
import java.util.List;

public class Rules {

    @Parameter(property = "preConfiguredRules")
    private List<String> preConfiguredRules= new ArrayList<>();

    @Parameter(property = "configurableRules")
    private List<ConfigurableRule> configurableRules= new ArrayList<>();

    public List<String> getPreConfiguredRules() {
        return preConfiguredRules;
    }

    public List<ConfigurableRule> getConfigurableRules() {
        return configurableRules;
    }

    public boolean isValid() {
        return (hasSomePreConfiguredRules() || hasSomeConfigurableRules());
    }

    public boolean hasSomePreConfiguredRules() {
        return !preConfiguredRules.isEmpty();
    }

    public boolean hasSomeConfigurableRules() {
        return !configurableRules.isEmpty();
    }

}
