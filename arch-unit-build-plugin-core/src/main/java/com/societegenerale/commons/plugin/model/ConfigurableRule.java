package com.societegenerale.commons.plugin.model;

import java.util.ArrayList;
import java.util.List;

public class ConfigurableRule {

  private String rule;

  private ApplyOn applyOn;

  private List<String> checks = new ArrayList<>();

  private boolean skip;

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

  public ApplyOn getApplyOn() {
    return applyOn;
  }

  public void setApplyOn(ApplyOn applyOn) {
    this.applyOn = applyOn;
  }

  public boolean isSkip() {
    return skip;
  }

  public void setSkip(boolean skip) {
    this.skip = skip;
  }
}
