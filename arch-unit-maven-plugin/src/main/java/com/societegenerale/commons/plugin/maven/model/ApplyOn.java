package com.societegenerale.commons.plugin.maven.model;

import org.apache.maven.plugins.annotations.Parameter;

public class ApplyOn {

  @Parameter(property = "packageName")
  private String packageName;

  @Parameter(property = "scope")
  private String scope;

  //default constructor is required at runtime
  public ApplyOn() {

  }

  //convenience constructor when calling from unit tests
  public ApplyOn(String packageName, String scope) {
    this.packageName = packageName;
    this.scope = scope;
  }

  public String getPackageName() {
    return packageName;
  }

  public String getScope() {
    return scope;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public com.societegenerale.commons.plugin.model.ApplyOn toCoreApplyOn() {
      return new com.societegenerale.commons.plugin.model.ApplyOn(packageName,scope);
  }
}
