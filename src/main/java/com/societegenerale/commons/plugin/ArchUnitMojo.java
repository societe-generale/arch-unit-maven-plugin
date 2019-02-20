package com.societegenerale.commons.plugin;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.net.URLClassLoader.newInstance;

/**
 * @goal generate
 * @phase process-classes
 * @configurator include-project-dependencies
 * @requiresDependencyResolution compile+runtime
 */
@Mojo(name = "arch-test", requiresDependencyResolution = ResolutionScope.TEST)
public class ArchUnitMojo extends AbstractMojo {

  @Parameter(property = "projectPath")
  private String projectPath;

  @Parameter(property = "rules")
  private List<String> rules;

  public List<String> getRules() {
    return rules;
  }

  private static final String EXECUTE_METHOD_NAME = "execute";

  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  private MavenProject mavenProject;

  @Override
  public void execute() throws MojoFailureException {

    try {
      ClassLoader contextClassLoader = fetchContextClassLoader();
      for (String rule : getRules()) {
        Class<?> testClass = contextClassLoader.loadClass(rule);
        Method method = testClass.getDeclaredMethod(EXECUTE_METHOD_NAME, String.class);
        method.invoke(testClass.newInstance(), projectPath);
      }
    } catch (final Exception e) {
      throw new MojoFailureException(e.toString(), e);
    }
  }

  private ClassLoader fetchContextClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {

    List<URL> urls = new ArrayList<>();
    List<String> elements = mavenProject.getTestClasspathElements();
    for (String element : elements) {
      urls.add(new File(element).toURI().toURL());
    }

    ClassLoader contextClassLoader = newInstance(
        urls.toArray(new URL[0]),
        Thread.currentThread().getContextClassLoader());
    Thread.currentThread().setContextClassLoader(contextClassLoader);
    return contextClassLoader;
  }

}
