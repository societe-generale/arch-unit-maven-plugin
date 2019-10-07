package com.societegenerale.commons.plugin.maven.model;

import com.societegenerale.commons.plugin.model.ApplyOn;
import org.apache.maven.plugins.annotations.Parameter;

public class MavenApplyOn {

  @Parameter(property = "packageName")
  private String packageName;

  @Parameter(property = "scope")
  private String scope;

  //default constructor is required at runtime
  public MavenApplyOn() {

  }

  //convenience constructor when calling from unit tests
  public MavenApplyOn(String packageName, String scope) {
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

  public ApplyOn toCoreApplyOn() {
      return new ApplyOn(packageName,scope);
  }
}
