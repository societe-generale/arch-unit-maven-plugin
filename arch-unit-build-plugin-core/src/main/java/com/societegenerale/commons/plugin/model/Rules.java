package com.societegenerale.commons.plugin.model;

import java.util.ArrayList;
import java.util.List;

public class Rules {

    public Rules() {
        //no arg constructor required by Maven when running the plugin
    }

    public Rules(List<String> preConfiguredRules, List<ConfigurableRule> configurableRules) {
        this.preConfiguredRules = preConfiguredRules;
        this.configurableRules = configurableRules;
    }

    private List<String> preConfiguredRules= new ArrayList<>();

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

    public void setPreConfiguredRules(List<String> preConfiguredRules) {
        this.preConfiguredRules = preConfiguredRules;
    }

    public void setConfigurableRules(List<ConfigurableRule> configurableRules) {
        this.configurableRules = configurableRules;
    }
}
