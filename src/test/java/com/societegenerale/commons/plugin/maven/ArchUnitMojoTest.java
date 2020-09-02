package com.societegenerale.commons.plugin.maven;

import java.io.File;
import java.io.StringReader;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableSet;
import com.societegenerale.aut.test.TestClassWithPowerMock;
import com.societegenerale.commons.plugin.rules.MyCustomAndDummyRules;
import com.societegenerale.commons.plugin.rules.NoPowerMockRuleTest;
import com.tngtech.java.junit.dataprovider.DataProvider;
import com.tngtech.java.junit.dataprovider.DataProviderRunner;
import com.tngtech.java.junit.dataprovider.UseDataProvider;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.assertj.core.api.AbstractThrowableAssert;
import org.assertj.core.api.Condition;
import org.codehaus.plexus.configuration.DefaultPlexusConfiguration;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.logging.LoggerManager;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static com.tngtech.junit.dataprovider.DataProviders.testForEach;
import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(DataProviderRunner.class)
public class ArchUnitMojoTest {

  @Rule
  public final MojoRule mojoRule = new MojoRule();

  @Rule
  public final MockitoRule mockitoRule = MockitoJUnit.rule();

  @InjectMocks
  private ArchUnitMojo archUnitMojo;

  @Mock
  private MavenProject mavenProject;

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

  private String getBasedir() {
    String basedir = System.getProperty("basedir");

    if (basedir == null) {
      basedir = new File("").getAbsolutePath();
    }

    return basedir;
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

  private void executeAndExpectViolations( ArchUnitMojo mojo, ExpectedRuleFailure... expectedRuleFailures) {
    AbstractThrowableAssert<?, ? extends Throwable> throwableAssert = assertThatThrownBy(mojo::execute);
    stream(expectedRuleFailures).forEach(expectedFailure -> {
      throwableAssert.hasMessageContaining(String.format("Rule '%s' was violated", expectedFailure.ruleDescription));
      expectedFailure.details.forEach(throwableAssert::hasMessageContaining);
    });
    throwableAssert.has(exactNumberOfViolatedRules(expectedRuleFailures.length));
  }

  private Condition<Throwable> exactNumberOfViolatedRules(final int number) {
    return new Condition<Throwable>("exactly " + number + " violated rules") {
      @Override
      public boolean matches(Throwable throwable) {
        Matcher matcher = Pattern.compile("Rule '.*' was violated").matcher(throwable.getMessage());
        int numberOfOccurrences = 0;
        while (matcher.find()) {
          numberOfOccurrences++;
        }
        return numberOfOccurrences == number;
      }
    };
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
