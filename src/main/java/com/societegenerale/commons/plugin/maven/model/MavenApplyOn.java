package com.societegenerale.commons.plugin.maven.model;

import com.societegenerale.commons.plugin.model.ApplyOn;
import org.apache.maven.plugins.annotations.Parameter;

public class MavenApplyOn {

  @Parameter(property = "packageName")
  private String packageName;

  @Parameter(property = "scope")
  private String scope;

  @Parameter(property = "aggregator", defaultValue = "false")
  private boolean aggregator;

  //default constructor is required at runtime
  public MavenApplyOn() {

  }

  //convenience constructor when calling from unit tests
  public MavenApplyOn(String packageName, String scope, boolean aggregator) {
    this.packageName = packageName;
    this.scope = scope;
    this.aggregator = aggregator;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getScope() {
    return scope;
  }

  public boolean getAggregator() {
    return aggregator;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public void setAggregator(boolean aggregator) {
    this.aggregator = aggregator;
  }

  public ApplyOn toCoreApplyOn() {
    return new ApplyOn(packageName,scope);
  }
}
