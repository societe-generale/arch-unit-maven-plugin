package com.societegenerale.commons.plugin.maven.model;

import java.util.ArrayList;
import java.util.List;

import com.societegenerale.commons.plugin.model.Rules;
import org.apache.maven.plugins.annotations.Parameter;

import static java.util.stream.Collectors.toList;

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

    public Rules toCoreRules(){

        return new Rules(preConfiguredRules,
                configurableRules.stream()
                                 .map(e -> e.toCoreConfigurableRule())
                                 .collect(toList()));
    }

    public List<String> getPreConfiguredRules() {
        return preConfiguredRules;
    }

    public List<MavenConfigurableRule> getConfigurableRules() {
        return configurableRules;
    }

    public void setPreConfiguredRules(List<String> preConfiguredRules) {
        this.preConfiguredRules = preConfiguredRules;
    }

    public void setConfigurableRules(List<MavenConfigurableRule> configurableRules) {
        this.configurableRules = configurableRules;
    }


}
