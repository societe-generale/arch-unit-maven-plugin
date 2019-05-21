package com.societegenerale.commons.plugin;


import com.societegenerale.commons.plugin.model.Rules;
import lombok.val;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
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

  @Before
  public void setUp() throws Exception {

    pomDom = Xpp3DomBuilder.build(new StringReader(defaultPom));

  }

  @Test
  public void shouldFailWhenNoRuleConfigured() throws Exception {

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


    pluginConfiguration = rule.extractPluginConfiguration("arch-unit-maven-plugin", Xpp3DomBuilder.build(new StringReader(pomWithNoRule)));

    ArchUnitMojo mojo = (ArchUnitMojo) rule.configureMojo(archUnitMojo, pluginConfiguration);


    assertThatExceptionOfType(MojoFailureException.class)
                      .isThrownBy(() -> mojo.execute())
                      .withMessageContaining("Arch unit Plugin should have at least one preconfigured/configurable rule");
  }

  @Test
  public void shouldFailWithExpectedMessageWhenViolationsAreFound() throws Exception {

    pluginConfiguration = rule.extractPluginConfiguration("arch-unit-maven-plugin", pomDom);

    ArchUnitMojo mojo = (ArchUnitMojo) rule.configureMojo(archUnitMojo, pluginConfiguration);

    mockTestClasspathElements(mojo.getClass());


    assertThatExceptionOfType(MojoFailureException.class)
            .isThrownBy(() -> mojo.execute())
            // one violation among others...
            .withMessageContaining("classes should not use Junit assertions");
  }


  @Test(expected = Exception.class)
  public void shouldFailAsTestClassPathElementsAreMocked() throws Exception {

    pluginConfiguration = rule.extractPluginConfiguration("arch-unit-maven-plugin", pomDom);

    ArchUnitMojo mojo = (ArchUnitMojo) rule.configureMojo(archUnitMojo, pluginConfiguration);
    mockTestClasspathElements(mojo.getClass());
    mojo.execute();
  }

  private void mockTestClasspathElements(Class<?>... classes) throws Exception {

    val classesToReturn=stream(classes)
            .map(Class::getProtectionDomain)
            .map(ProtectionDomain::getCodeSource)
            .map(CodeSource::getLocation)
            .map(URL::toString)
            .distinct()
            .collect(Collectors.toList());

    doReturn(classesToReturn).when(mavenProject).getTestClasspathElements();
  }

}