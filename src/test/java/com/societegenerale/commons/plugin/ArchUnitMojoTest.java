package com.societegenerale.commons.plugin;

import com.google.common.collect.ImmutableSet;
import com.societegenerale.commons.plugin.rules.MyCustomRules;
import com.societegenerale.commons.plugin.rules.NoPowerMockRuleTest;
import com.societegenerale.commons.plugin.rules.classesForTests.TestClassWithPowerMock;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.assertj.core.api.AbstractThrowableAssert;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.StringReader;
import java.util.Set;

import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.*;

@RunWith(MockitoJUnitRunner.class)
public class ArchUnitMojoTest {

  @InjectMocks
  private ArchUnitMojo archUnitMojo;

  @Mock
  private MavenProject mavenProject;

  @Rule
  public MojoRule rule = new MojoRule();

  private PlexusConfiguration pluginConfiguration;

  // @formatter:off
  private static final String pomWithNoRule =
      "<project>" +
        "<build>" +
          "<plugins>" +
            "<plugin>" +
              "<artifactId>arch-unit-maven-plugin</artifactId>" +
              "<configuration>" +
                "<rules>" +
                  "<preConfiguredRules></preConfiguredRules>" +
                  "<configurableRules></configurableRules>" +
                "</rules>" +
              "</configuration>" +
            "</plugin>" +
          "</plugins>" +
        "</build>" +
      "</project>";
  // @formatter:on

  @Before
  public void setUp() throws Exception {

    pluginConfiguration = rule.extractPluginConfiguration("arch-unit-maven-plugin", Xpp3DomBuilder.build(new StringReader(pomWithNoRule)));

  }

  @Test
  public void shouldFailWhenNoRuleConfigured() throws Exception {

    ArchUnitMojo mojo = (ArchUnitMojo) rule.configureMojo(archUnitMojo, pluginConfiguration);

    assertThatExceptionOfType(MojoFailureException.class)
        .isThrownBy(mojo::execute)
        .withMessageContaining("Arch unit Plugin should have at least one preconfigured/configurable rule");
  }

  @Test
  public void shouldExecuteSinglePreconfiguredRule() throws Exception {

    pluginConfiguration.getChild("projectPath").setValue("./target/test-classes/com/societegenerale/commons/plugin/rules/classesForTests");

    // add single rule
    PlexusConfiguration preConfiguredRules = pluginConfiguration.getChild("rules").getChild("preConfiguredRules");
    preConfiguredRules.addChild("rule", NoPowerMockRuleTest.class.getName());

    ArchUnitMojo mojo = (ArchUnitMojo) rule.configureMojo(archUnitMojo, pluginConfiguration);

    executeAndExpectViolations(mojo,
        expectRuleFailure("classes should not use Powermock")
            .withDetails("Favor Mockito and proper dependency injection - " + TestClassWithPowerMock.class.getName()));
  }

  @Test
  public void shouldFailWronglyDefinedConfigurableRule() throws Exception {
    PlexusConfiguration configurableRule = new DefaultPlexusConfiguration("configurableRule");

    String missingCheck = "notThere";
    String ruleClass = MyCustomRules.class.getName();

    configurableRule.addChild("rule", ruleClass);
    configurableRule.addChild(buildChecksBlock(missingCheck));
    pluginConfiguration.getChild("rules").getChild("configurableRules").addChild(configurableRule);

    ArchUnitMojo mojo = (ArchUnitMojo) rule.configureMojo(archUnitMojo, pluginConfiguration);

    assertThatExceptionOfType(MojoFailureException.class)
        .isThrownBy(mojo::execute)
        .withMessageContaining(String.format("The following configured checks are not present within %s: [%s]", ruleClass, missingCheck));
  }

  @Test
  public void shouldExecuteSingleConfigurableRule() throws Exception {

    PlexusConfiguration configurableRule = new DefaultPlexusConfiguration("configurableRule");

    configurableRule.addChild("rule", MyCustomRules.class.getName());
    configurableRule.addChild(buildChecksBlock("annotatedWithTest"));
    configurableRule.addChild(buildApplyOnBlock("com.societegenerale.commons.plugin.rules.classesForTests.specificCase", "test"));

    PlexusConfiguration configurableRules = pluginConfiguration.getChild("rules").getChild("configurableRules");
    configurableRules.addChild(configurableRule);

    ArchUnitMojo mojo = (ArchUnitMojo) rule.configureMojo(archUnitMojo, pluginConfiguration);

    executeAndExpectViolations(mojo,
        expectRuleFailure("classes should be annotated with @Test").ofAnyKind());
  }

  @Test
  public void shouldExecuteBothConfigurableRule_and_PreConfiguredRule() throws Exception {

    PlexusConfiguration configurableRule = new DefaultPlexusConfiguration("configurableRule");

    configurableRule.addChild("rule", "com.societegenerale.commons.plugin.rules.MyCustomRules");
    configurableRule.addChild(buildChecksBlock("annotatedWithTest"));
    configurableRule.addChild(buildApplyOnBlock("com.societegenerale.commons.plugin.rules.classesForTests.specificCase", "test"));

    PlexusConfiguration configurableRules = pluginConfiguration.getChild("rules").getChild("configurableRules");
    configurableRules.addChild(configurableRule);

    PlexusConfiguration preConfiguredRules = pluginConfiguration.getChild("rules").getChild("preConfiguredRules");
    preConfiguredRules.addChild("rule", NoPowerMockRuleTest.class.getName());

    ArchUnitMojo mojo = (ArchUnitMojo) rule.configureMojo(archUnitMojo, pluginConfiguration);

    executeAndExpectViolations(mojo,
        expectRuleFailure("classes should be annotated with @Test").ofAnyKind(),
        expectRuleFailure("classes should not use Powermock").ofAnyKind());
  }

  private void executeAndExpectViolations(ArchUnitMojo mojo, ExpectedRuleFailure... expectedRuleFailures) {
    AbstractThrowableAssert<?, ? extends Throwable> throwableAssert = assertThatThrownBy(mojo::execute);
    stream(expectedRuleFailures).forEach(expectedFailure -> {
      throwableAssert.hasMessageContaining(String.format("Rule '%s' was violated", expectedFailure.ruleDescription));
      expectedFailure.details.forEach(throwableAssert::hasMessageContaining);
    });
  }

  private PlexusConfiguration buildApplyOnBlock(String packageName, String scope) {

    PlexusConfiguration packageNameElement = new DefaultPlexusConfiguration("packageName", packageName);
    PlexusConfiguration scopeElement = new DefaultPlexusConfiguration("scope", scope);
    PlexusConfiguration applyOnElement = new DefaultPlexusConfiguration("applyOn");
    applyOnElement.addChild(packageNameElement);
    applyOnElement.addChild(scopeElement);

    return applyOnElement;
  }

  private PlexusConfiguration buildChecksBlock(String... checks) {
    PlexusConfiguration checksElement = new DefaultPlexusConfiguration("checks");
    stream(checks).map(c -> new DefaultPlexusConfiguration("check", c)).forEach(checksElement::addChild);
    return checksElement;
  }

  private static ExpectedRuleFailure.Creator expectRuleFailure(String ruleDescription) {
    return new ExpectedRuleFailure.Creator(ruleDescription);
  }

  private static class ExpectedRuleFailure {
    private final String ruleDescription;
    private final Set<String> details;

    private ExpectedRuleFailure(String ruleDescription, Set<String> details) {
      this.ruleDescription = ruleDescription;
      this.details = details;
    }

    private static class Creator {
      private final String ruleDescription;

      Creator(String ruleDescription) {
        this.ruleDescription = ruleDescription;
      }

      ExpectedRuleFailure ofAnyKind() {
        return withDetails();
      }

      ExpectedRuleFailure withDetails(String... detals) {
        return new ExpectedRuleFailure(ruleDescription, ImmutableSet.copyOf(detals));
      }
    }
  }
}