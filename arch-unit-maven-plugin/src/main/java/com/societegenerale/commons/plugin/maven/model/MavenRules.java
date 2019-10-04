package com.societegenerale.commons.plugin.maven.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.maven.plugins.annotations.Parameter;

public class MavenRules {

    @Parameter(property = "preConfiguredRules")
    private List<String> preConfiguredRules= new ArrayList<>();

    @Parameter(property = "configurableRules")
    private List<MavenConfigurableRule> configurableRules = new ArrayList<>();

    public MavenRules() {
        //no arg constructor required by Maven when running the plugin
    }

    public MavenRules(List<String> preConfiguredRules, List<MavenConfigurableRule> configurableRules) {
        this.preConfiguredRules = preConfiguredRules;
        this.configurableRules = configurableRules;
    }

    public com.societegenerale.commons.plugin.model.Rules toCoreRules(){



        return new com.societegenerale.commons.plugin.model.Rules(preConfiguredRules,
                configurableRules.stream().map(e -> e.toCoreConfigurableRule()).collect(
                Collectors.toList()));
    }

    public List<String> getPreConfiguredRules() {
        return preConfiguredRules;
    }

    public List<MavenConfigurableRule> getConfigurableRules() {
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

    public void setPreConfiguredRules(List<String> preConfiguredRules) {
        this.preConfiguredRules = preConfiguredRules;
    }

    public void setConfigurableRules(List<MavenConfigurableRule> configurableRules) {
        this.configurableRules = configurableRules;
    }


}
