package com.societegenerale.commons.plugin.maven;

import java.io.StringReader;

import com.societegenerale.commons.plugin.rules.StringFieldsThatAreActuallyDatesRuleTest;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.assertj.core.api.AbstractThrowableAssert;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ArchUnitMojoExcludeTest extends AbstractArchUnitMojoTest
{

    public final MojoRule mojoRule1 = new MojoRule();

    public final MojoRule mojoRule2 = new MojoRule();

    public final MockitoRule mockitoRule = MockitoJUnit.rule();

    @InjectMocks
    private ArchUnitMojo archUnitMojo;

    @Mock
    private MavenProject mavenProject;


    // @formatter:off
    private static final String pomWithNoRuleNoExcludes =
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

    // @formatter:off
    //  "<project.build.directory>./<project.build.directory>" +
    private static final String pomWithExcludes =
      "<project>" +
        "<properties>" +

        "</properties>" +
        "<build> <directory>${project.basedir}/target</directory>" +
          "<plugins>" +
            "<plugin>" +
              "<artifactId>arch-unit-maven-plugin</artifactId>" +
              "<configuration>" +
               "<excludedPaths>" +
                  "<excludedPath>generated-test-sources</excludedPath>" +
              "</excludedPaths>" +
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

    @BeforeEach
    public void setUp()
    {

        final Build mockBuild = mock(Build.class);

        when(mavenProject.getBuild()).thenReturn(mockBuild);
        when(mockBuild.getOutputDirectory()).thenReturn("target/test-classes");
        when(mockBuild.getTestOutputDirectory()).thenReturn("target/test-classes");

        archUnitMojo.setProjectBuildDir(getBasedir() + "/target");
    }

    @Test
    public void generatedSourcesShouldViolatePreconfiguredRule() throws Exception
    {
        final PlexusConfiguration pluginConfiguration = createPluginConfiguration(mojoRule1, pomWithNoRuleNoExcludes);

        final String exceptionMessageRule = ":Rule Violated - " + StringFieldsThatAreActuallyDatesRuleTest.class.getPackage()
                                                                                                                .getName();
        final String exceptionMessageClass = "class: com.societegenerale.commons.plugin.maven.test.generated.Book";

        // add single rule
        final PlexusConfiguration preConfiguredRules = pluginConfiguration.getChild("rules").getChild("preConfiguredRules");
        preConfiguredRules.addChild("rule", StringFieldsThatAreActuallyDatesRuleTest.class.getName());

        final ArchUnitMojo mojo = (ArchUnitMojo) mojoRule1.configureMojo(archUnitMojo, pluginConfiguration);

        final AbstractThrowableAssert<?, ? extends Throwable> throwableAssert = assertThatThrownBy(mojo::execute);
        throwableAssert.hasMessageContaining(exceptionMessageRule);
        throwableAssert.hasMessageContaining(exceptionMessageClass);
    }

    @Test
    public void excludedGeneratedSourcesShouldNotViolatePreconfiguredRule() throws Exception
    {
        final PlexusConfiguration pluginConfiguration = createPluginConfiguration(mojoRule2, pomWithExcludes);
        // add single rule
        final PlexusConfiguration preConfiguredRules = pluginConfiguration.getChild("rules").getChild("preConfiguredRules");
        preConfiguredRules.addChild("rule", StringFieldsThatAreActuallyDatesRuleTest.class.getName());

        final ArchUnitMojo mojo = (ArchUnitMojo) mojoRule2.configureMojo(archUnitMojo, pluginConfiguration);
        mojo.execute();
    }

    private PlexusConfiguration createPluginConfiguration(final MojoRule mojoRule, final String pom) throws Exception
    {
        return mojoRule.extractPluginConfiguration("arch-unit-maven-plugin", Xpp3DomBuilder
                .build(new StringReader(pom)));
    }
}
