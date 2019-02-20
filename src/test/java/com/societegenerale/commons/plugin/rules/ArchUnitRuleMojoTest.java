package com.societegenerale.commons.plugin.rules;


import static java.util.Arrays.stream;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import com.societegenerale.commons.plugin.ArchUnitMojo;
import java.io.StringReader;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ArchUnitRuleMojoTest {

  @InjectMocks
  private ArchUnitMojo archUnitMojo;

  @Mock
  private MavenProject mavenProject;

  @Rule
  public MojoRule rule = new MojoRule();

  private String pom;

  private Xpp3Dom pomDom;

  private PlexusConfiguration pluginConfiguration;

  @Before
  public void setUp() throws Exception {

    pom =
        "<project>" +
            "<build>" +
            "<plugins>" +
            "<plugin>" +
            "<artifactId>arch-unit-maven-plugin</artifactId>" +
            "<configuration>" +
            "<rules>" +
            "<rule>NoStandardStreamRuleTest</rule>" +
            "<rule>NoJunitAssertRuleTest</rule>" +
            "<rule>NoJodaTimeRuleTest</rule>" +
            "<rule>NoPowerMockRuleTest</rule>" +
            "<rule>NoPrefixForInterfacesRuleTest</rule>" +
            "<rule>NoTestIgnoreRuleTest</rule>"+
            "<rule>NoTestIgnoreWithoutCommentRuleTest</rule>" +
            "</rules>" +
            "</configuration>" +
            "</plugin>" +
            "</plugins>" +
            "</build>" +
            "</project>";

    pomDom = Xpp3DomBuilder.build(new StringReader(pom));

    pluginConfiguration = rule.extractPluginConfiguration("arch-unit-maven-plugin", pomDom);
  }


  @Test
  public void testNumberOfRulesInArchUnitPlugin() throws Exception {

    ArchUnitMojo mojo = (ArchUnitMojo) rule.configureMojo(archUnitMojo, pluginConfiguration);
    List<String> archUnitRules = mojo.getRules();
    assertThat(archUnitRules.size()).isEqualTo(7);
  }


  @Test(expected = Exception.class)
  public void shouldFailAsTestClassPathElementsAreMocked() throws Exception {

    ArchUnitMojo mojo = (ArchUnitMojo) rule.configureMojo(archUnitMojo, pluginConfiguration);
    mockTestClasspathElements(mojo.getClass());
    mojo.execute();

  }

  private void mockTestClasspathElements(Class<?>... classes) throws Exception {
    doReturn(stream(classes)
        .map(Class::getProtectionDomain)
        .map(ProtectionDomain::getCodeSource)
        .map(CodeSource::getLocation)
        .map(URL::toString)
        .distinct()
        .collect(Collectors.toList())).when(mavenProject).getTestClasspathElements();
  }

}
