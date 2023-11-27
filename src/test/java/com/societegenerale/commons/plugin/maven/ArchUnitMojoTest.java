package com.societegenerale.commons.plugin.maven;

import java.io.File;
import java.io.StringReader;

import com.societegenerale.aut.test.TestClassWithPowerMock;
import com.societegenerale.commons.plugin.rules.MyCustomAndDummyRules;
import com.societegenerale.commons.plugin.rules.NoPowerMockRuleTest;
import com.tngtech.archunit.ArchConfiguration;
import com.tngtech.junit.dataprovider.DataProvider;
import com.tngtech.junit.dataprovider.UseDataProvider;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.tngtech.junit.dataprovider.DataProviders.testForEach;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ArchUnitMojoTest extends AbstractArchUnitMojoTest
{

  public final MojoRule mojoRule = new MojoRule();

  public final MockitoRule mockitoRule = MockitoJUnit.rule();

  @InjectMocks
  private ArchUnitMojo archUnitMojo;
  private final MavenProject mavenProject = mock(MavenProject.class);

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
      "</project>";  // @formatter:on

  @BeforeEach
  public void setUp() throws Exception {

    Build mockBuild = mock(Build.class);

    when(mavenProject.getBuild()).thenReturn(mockBuild);
    when(mockBuild.getOutputDirectory()).thenReturn("target/classes");
    when(mockBuild.getTestOutputDirectory()).thenReturn("target/test-classes");

    pluginConfiguration = mojoRule.extractPluginConfiguration("arch-unit-maven-plugin", Xpp3DomBuilder.build(new StringReader(pomWithNoRule)));
  }

  @Test
  public void shouldFailWhenNoRuleConfigured() throws Exception {

    ArchUnitMojo mojo = (ArchUnitMojo) mojoRule.configureMojo(archUnitMojo, pluginConfiguration);

    assertThatExceptionOfType(MojoFailureException.class)
        .isThrownBy(mojo::execute)
        .withMessageContaining("Arch unit Plugin should have at least one preconfigured/configurable rule");
  }

  @Test
  public void shouldExecuteSinglePreconfiguredRule() throws Exception {

    // add single rule
    PlexusConfiguration preConfiguredRules = pluginConfiguration.getChild("rules").getChild("preConfiguredRules");
    preConfiguredRules.addChild("rule", NoPowerMockRuleTest.class.getName());

    ArchUnitMojo mojo = (ArchUnitMojo) mojoRule.configureMojo(archUnitMojo, pluginConfiguration);

    executeAndExpectViolations(mojo,
        expectRuleFailure("classes should not use Powermock")
            .withDetails("Favor Mockito and proper dependency injection - " + TestClassWithPowerMock.class.getName()));
  }

  @Test
  public void shouldExecuteSinglePreconfiguredRuleWithNoFailOnError() throws Exception {

    // add single rule

    PlexusConfiguration preConfiguredRules = pluginConfiguration.getChild("rules").getChild("preConfiguredRules");
    preConfiguredRules.addChild("rule", NoPowerMockRuleTest.class.getName());

    pluginConfiguration.addChild("noFailOnError","true");

    ArchUnitMojo mojo = (ArchUnitMojo) mojoRule.configureMojo(archUnitMojo, pluginConfiguration);

    Log log = mock(Log.class);
    mojo.setLog(log);
    mojo.execute();

    verify(log, times(1)).warn(
            "ArchUnit Maven plugin reported architecture failures listed below :Rule Violated - " + NoPowerMockRuleTest.class.getName()
                    + System.lineSeparator() +
                    "java.lang.AssertionError: Architecture Violation [Priority: MEDIUM] - Rule 'classes should not use Powermock' was violated (1 times):"
                    + System.lineSeparator() +
                    "Favor Mockito and proper dependency injection - " + TestClassWithPowerMock.class.getName() + System.lineSeparator());
  }

  @Test
  public void shouldFailWronglyDefinedConfigurableRule() throws Exception {
    PlexusConfiguration configurableRule = new DefaultPlexusConfiguration("configurableRule");

    String missingCheck = "notThere";
    String ruleClass = MyCustomAndDummyRules.class.getName();

    configurableRule.addChild("rule", ruleClass);
    configurableRule.addChild(buildChecksBlock(missingCheck));
    pluginConfiguration.getChild("rules").getChild("configurableRules").addChild(configurableRule);

    ArchUnitMojo mojo = (ArchUnitMojo) mojoRule.configureMojo(archUnitMojo, pluginConfiguration);

    assertThatExceptionOfType(MojoFailureException.class)
        .isThrownBy(mojo::execute)
        .withMessageContaining(String.format("The following configured checks are not present within %s: [%s]", ruleClass, missingCheck));
  }

  @DataProvider
  public static Object[][] configurableRuleChecks() {
    return testForEach("annotatedWithTest_asField", "annotatedWithTest_asMethod");
  }

  @Test
  @UseDataProvider("configurableRuleChecks")
  public void shouldExecuteSingleConfigurableRuleCheck(String checkName) throws Exception {

    PlexusConfiguration configurableRule = new DefaultPlexusConfiguration("configurableRule");

    configurableRule.addChild("rule", MyCustomAndDummyRules.class.getName());
    configurableRule.addChild(buildChecksBlock(checkName));
    configurableRule.addChild(buildApplyOnBlock("com.societegenerale.aut.test.specificCase", "test"));

    PlexusConfiguration configurableRules = pluginConfiguration.getChild("rules").getChild("configurableRules");
    configurableRules.addChild(configurableRule);

    ArchUnitMojo mojo = (ArchUnitMojo) mojoRule.configureMojo(archUnitMojo, pluginConfiguration);

    executeAndExpectViolations(mojo,
        expectRuleFailure("classes should be annotated with @Test").ofAnyKind());
  }

  @Test
  public void shouldExecuteAllConfigurableRuleChecksIfUnconfigured() throws Exception {

    PlexusConfiguration configurableRule = new DefaultPlexusConfiguration("configurableRule");

    configurableRule.addChild("rule", MyCustomAndDummyRules.class.getName());
    configurableRule.addChild(buildApplyOnBlock("com.societegenerale.aut.test.specificCase", "test"));

    PlexusConfiguration configurableRules = pluginConfiguration.getChild("rules").getChild("configurableRules");
    configurableRules.addChild(configurableRule);

    ArchUnitMojo mojo = (ArchUnitMojo) mojoRule.configureMojo(archUnitMojo, pluginConfiguration);

    executeAndExpectViolations(mojo,
        expectRuleFailure("classes should be annotated with @Test").ofAnyKind(),
        expectRuleFailure("classes should be annotated with @Test").ofAnyKind(),
        expectRuleFailure("classes should reside in a package 'myPackage'").ofAnyKind(),
        expectRuleFailure("classes should reside in a package 'myPackage'").ofAnyKind()
    );
  }

  @Test
  public void shouldExecuteBothConfigurableRule_and_PreConfiguredRule() throws Exception {

    PlexusConfiguration configurableRule = new DefaultPlexusConfiguration("configurableRule");

    configurableRule.addChild("rule", MyCustomAndDummyRules.class.getName());
    configurableRule.addChild(buildChecksBlock("annotatedWithTest_asField"));
    configurableRule.addChild(buildApplyOnBlock("com.societegenerale.aut.test.specificCase", "test"));

    PlexusConfiguration configurableRules = pluginConfiguration.getChild("rules").getChild("configurableRules");
    configurableRules.addChild(configurableRule);

    PlexusConfiguration preConfiguredRules = pluginConfiguration.getChild("rules").getChild("preConfiguredRules");
    preConfiguredRules.addChild("rule", NoPowerMockRuleTest.class.getName());

    ArchUnitMojo mojo = (ArchUnitMojo) mojoRule.configureMojo(archUnitMojo, pluginConfiguration);

    executeAndExpectViolations(mojo,
        expectRuleFailure("classes should be annotated with @Test").ofAnyKind(),
        expectRuleFailure("classes should not use Powermock").ofAnyKind());
  }

  @Test
  public void shouldNotExecuteConfigurableRule_and_PreConfiguredRule_IfSkipIsTrue() throws Exception {

    PlexusConfiguration configurableRule = new DefaultPlexusConfiguration("configurableRule");

    configurableRule.addChild("rule", MyCustomAndDummyRules.class.getName());
    configurableRule.addChild(buildChecksBlock("annotatedWithTest_asField"));
    configurableRule.addChild(buildApplyOnBlock("com.societegenerale.commons.plugin.rules.classesForTests.specificCase", "test"));

    PlexusConfiguration configurableRules = pluginConfiguration.getChild("rules").getChild("configurableRules");
    configurableRules.addChild(configurableRule);

    PlexusConfiguration preConfiguredRules = pluginConfiguration.getChild("rules").getChild("preConfiguredRules");
    preConfiguredRules.addChild("rule", NoPowerMockRuleTest.class.getName());

    pluginConfiguration.getChild("skip").setValue("true");

    ArchUnitMojo mojo = (ArchUnitMojo) mojoRule.configureMojo(archUnitMojo, pluginConfiguration);

    assertThatCode(() -> {
      mojo.execute();
    }).doesNotThrowAnyException();
  }

  @Test
  public void shouldSkipIfPackagingIsPom() throws Exception {
    InterceptingLog interceptingLogger = new InterceptingLog(
        mojoRule.getContainer().lookup(LoggerManager.class).getLoggerForComponent(Mojo.ROLE));

    File testPom = new File(getBasedir(), "target/test-classes/unit/plugin-config.xml");
    ArchUnitMojo archUnitMojo = (ArchUnitMojo) mojoRule.lookupMojo("arch-test", testPom);

    when(mavenProject.getPackaging()).thenReturn("pom");

    mojoRule.setVariableValueToObject(archUnitMojo, "mavenProject", mavenProject);
    archUnitMojo.setLog(interceptingLogger);

    assertThatCode(() -> archUnitMojo.execute()).doesNotThrowAnyException();

    assertThat(interceptingLogger.debugLogs).containsExactly("module packaging is 'pom', so skipping execution");
  }

  @Test
  public void shouldConfigureArchConfigurationProperties() throws Exception {
    final String propertyName = "archunit.propertyName";
    final String propertyValue = "propertyValue";

    // configure the property name and value
    pluginConfiguration.getChild("properties").addChild(propertyName, propertyValue);

    // configure some rule
    pluginConfiguration.getChild("rules")
            .getChild("preConfiguredRules")
            .addChild("rule", NoPowerMockRuleTest.class.getName());

    // execute the rule, which happens to fail
    ArchUnitMojo mojo = (ArchUnitMojo) mojoRule.configureMojo(archUnitMojo, pluginConfiguration);
    Log log = mock(Log.class);
    mojo.setLog(log);
    assertThrows(MojoFailureException.class, mojo::execute);

    // assert that the configuration is applied
    assertThat(ArchConfiguration.get().getProperty(propertyName)).isEqualTo(propertyValue);
    verify(log, times(1)).debug("configuring ArchUnit properties");
  }
}
