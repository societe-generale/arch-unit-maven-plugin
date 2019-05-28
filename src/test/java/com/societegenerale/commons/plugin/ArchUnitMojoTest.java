package com.societegenerale.commons.plugin;


import com.societegenerale.commons.plugin.model.Rules;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.StringReader;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.doReturn;

@RunWith(MockitoJUnitRunner.class)
public class ArchUnitMojoTest {

  @InjectMocks
  private ArchUnitMojo archUnitMojo;

  @Mock
  private MavenProject mavenProject;

  @Rule
  public MojoRule rule = new MojoRule();

  private Xpp3Dom pomDom;

  private PlexusConfiguration pluginConfiguration;

  private static String defaultPom =  "<project>" +
                                          "<build>" +
                                              "<plugins>" +
                                                  "<plugin>" +
                                                  "<artifactId>arch-unit-maven-plugin</artifactId>" +
                                                      "<configuration>" +
                                                          "<rules>" +
                                                            "<preConfiguredRules>" +
                                                                "<rule>com.societegenerale.commons.plugin.rules.NoStandardStreamRuleTest</rule>" +
                                                                "<rule>com.societegenerale.commons.plugin.rules.NoJunitAssertRuleTest</rule>" +
                                                                "<rule>com.societegenerale.commons.plugin.rules.NoJodaTimeRuleTest</rule>" +
                                                                "<rule>com.societegenerale.commons.plugin.rules.NoPowerMockRuleTest</rule>" +
                                                                "<rule>com.societegenerale.commons.plugin.rules.NoPrefixForInterfacesRuleTest</rule>" +
                                                                "<rule>com.societegenerale.commons.plugin.rules.NoTestIgnoreRuleTest</rule>"+
                                                                "<rule>com.societegenerale.commons.plugin.rules.NoTestIgnoreWithoutCommentRuleTest</rule>" +
                                                            "</preConfiguredRules>" +
                                                            "<configurableRules>" +
                                                                "<configurableRule>" +
                                                                  "<rule>com.societegenerale.commons.plugin.rules.NoStandardStreamRuleTest</rule>" +
                                                                "</configurableRule>" +
                                                            "</configurableRules>" +
                                                          "</rules>" +
                                                      "</configuration>" +
                                                  "</plugin>" +
                                              "</plugins>" +
                                          "</build>" +
                                      "</project>";

  String pomWithNoRule=  "<project>" +
          "<build>" +
          "<plugins>" +
          "<plugin>" +
          "<artifactId>arch-unit-maven-plugin</artifactId>" +
          "<configuration>" +
          "<rules>" +
          "<preConfiguredRules>" +
          "</preConfiguredRules>" +
          "<configurableRules>" +
          "</configurableRules>" +
          "</rules>" +
          "</configuration>" +
          "</plugin>" +
          "</plugins>" +
          "</build>" +
          "</project>";


  @Before
  public void setUp() throws Exception {

    pomDom = Xpp3DomBuilder.build(new StringReader(defaultPom));

  }

  @Test
  public void shouldFailWhenNoRuleConfigured() throws Exception {

    pluginConfiguration = rule.extractPluginConfiguration("arch-unit-maven-plugin", Xpp3DomBuilder.build(new StringReader(pomWithNoRule)));

    ArchUnitMojo mojo = (ArchUnitMojo) rule.configureMojo(archUnitMojo, pluginConfiguration);

    assertThatExceptionOfType(MojoFailureException.class)
                      .isThrownBy(() -> mojo.execute())
                      .withMessageContaining("Arch unit Plugin should have at least one preconfigured/configurable rule");
  }

  @Test
  public void shouldExecuteSinglePreconfiguredRule() throws Exception {

    pluginConfiguration = rule.extractPluginConfiguration("arch-unit-maven-plugin", Xpp3DomBuilder.build(new StringReader(pomWithNoRule)));
    pluginConfiguration.getChild("projectPath").setValue("./target/test-classes/com/societegenerale/commons/plugin/rules/classesForTests");

    // add single rule
    PlexusConfiguration preConfiguredRules=pluginConfiguration.getChild("rules").getChild("preConfiguredRules");
    preConfiguredRules.addChild("rule","com.societegenerale.commons.plugin.rules.NoPowerMockRuleTest");

    ArchUnitMojo mojo = (ArchUnitMojo) rule.configureMojo(archUnitMojo, pluginConfiguration);

    executeAndExpectViolations(mojo,1,"Favor Mockito and proper dependency injection");
  }

  @Test
  public void shouldExecuteSingleConfigurableRule() throws Exception {

    pluginConfiguration = rule.extractPluginConfiguration("arch-unit-maven-plugin", Xpp3DomBuilder.build(new StringReader(pomWithNoRule)));

    PlexusConfiguration configurableRule=new DefaultPlexusConfiguration("configurableRule");

    configurableRule.addChild("rule","com.societegenerale.commons.plugin.rules.MyCustomRule");
    configurableRule.addChild(buildChecksBlock("annotatedWithTest"));
    configurableRule.addChild(buildApplyOnBlock("com.societegenerale.commons.plugin.rules.classesForTests.specificCase","test"));

    PlexusConfiguration configurableRules=pluginConfiguration.getChild("rules").getChild("configurableRules");
    configurableRules.addChild(configurableRule);

    ArchUnitMojo mojo = (ArchUnitMojo) rule.configureMojo(archUnitMojo, pluginConfiguration);

    executeAndExpectViolations(mojo,1,"classes should be annotated with @Test");

  }

  @Test
  public void shouldExecuteBothConfigurableRule_and_PreConfiguredRule() throws Exception {

    pluginConfiguration = rule.extractPluginConfiguration("arch-unit-maven-plugin", Xpp3DomBuilder.build(new StringReader(pomWithNoRule)));

    PlexusConfiguration configurableRule=new DefaultPlexusConfiguration("configurableRule");

    configurableRule.addChild("rule","com.societegenerale.commons.plugin.rules.MyCustomRule");
    configurableRule.addChild(buildChecksBlock("annotatedWithTest"));
    configurableRule.addChild(buildApplyOnBlock("com.societegenerale.commons.plugin.rules.classesForTests.specificCase","test"));

    PlexusConfiguration configurableRules=pluginConfiguration.getChild("rules").getChild("configurableRules");
    configurableRules.addChild(configurableRule);

    PlexusConfiguration preConfiguredRules=pluginConfiguration.getChild("rules").getChild("preConfiguredRules");
    preConfiguredRules.addChild("rule","com.societegenerale.commons.plugin.rules.NoPowerMockRuleTest");

    ArchUnitMojo mojo = (ArchUnitMojo) rule.configureMojo(archUnitMojo, pluginConfiguration);

    executeAndExpectViolations(mojo,2,"classes should be annotated with @Test","Favor Mockito and proper dependency injection" );

  }

  private void executeAndExpectViolations(ArchUnitMojo mojo ,int nbExpectedViolations, String... expectedExceptionMessages){

    //would have loved to use AssertJ assertThatExceptionOfType + withMessageMatching(regex) ,
    // but not able to find the regex to match only once..
    // so going the dirty way..
    boolean expectedExceptionFound=false;

    try{
      mojo.execute();
    }
    catch(MojoFailureException e){
      assertThat(StringUtils.countMatches(e.toString(), "was violated")).isEqualTo(nbExpectedViolations);

      assertThat(e.toString()).contains(expectedExceptionMessages);
      expectedExceptionFound=true;
    }

    if(!expectedExceptionFound){
      fail("was expecting an exception");
    }
  }


  private PlexusConfiguration buildApplyOnBlock(String packageName, String scope) {

    PlexusConfiguration packageNameElement=new DefaultPlexusConfiguration("packageName",packageName);
    PlexusConfiguration scopeElement=new DefaultPlexusConfiguration("scope",scope);
    PlexusConfiguration applyOnElement=new DefaultPlexusConfiguration("applyOn");
    applyOnElement.addChild(packageNameElement);
    applyOnElement.addChild(scopeElement);

    return  applyOnElement;
  }

  private PlexusConfiguration buildChecksBlock(String... checks) {

    PlexusConfiguration checksElement=new DefaultPlexusConfiguration("checks");

    for(int i =0; i< checks.length ; i++){
      PlexusConfiguration check=new DefaultPlexusConfiguration("check",checks[i]);
      checksElement.addChild(check);
    }

    return  checksElement;
  }

}