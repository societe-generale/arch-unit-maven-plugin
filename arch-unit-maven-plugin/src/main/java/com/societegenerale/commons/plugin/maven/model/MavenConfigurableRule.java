package com.societegenerale.commons.plugin.maven.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugins.annotations.Parameter;

public class MavenConfigurableRule {

  @Parameter(property ="rule")
  private String rule;

  @Parameter(property ="applyOn")
  private MavenApplyOn applyOn;

  @Parameter(property ="checks")
  private List<String> checks = new ArrayList<>();

  @Parameter(defaultValue = "false", required = false)
  private boolean skip;

  public com.societegenerale.commons.plugin.model.ConfigurableRule toCoreConfigurableRule(){
    return new com.societegenerale.commons.plugin.model.ConfigurableRule(rule, applyOn ==null ? new com.societegenerale.commons.plugin.model.ApplyOn() : applyOn
            .toCoreApplyOn(),checks,skip);
  }

  public List<String> getChecks() {
    return checks;
  }

  public String getRule() {
    return rule;
  }

  public void setRule(String rule) {
    this.rule = rule;
  }

  public void setChecks(List<String> checks) {
    this.checks = checks;
  }

  public MavenApplyOn getApplyOn() {
    return applyOn;
  }

  public void setApplyOn(MavenApplyOn applyOn) {
    this.applyOn = applyOn;
  }

  public boolean isSkip() {
    return skip;
  }

  public void setSkip(boolean skip) {
    this.skip = skip;
  }
}
