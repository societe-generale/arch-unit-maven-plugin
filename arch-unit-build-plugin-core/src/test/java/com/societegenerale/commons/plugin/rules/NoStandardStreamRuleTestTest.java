package com.societegenerale.commons.plugin.rules;

import static com.societegenerale.commons.plugin.rules.NoStandardStreamRuleTest.getNotUseStandardStream;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import com.societegenerale.aut.test.TestClassWithStandardStream;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

public class NoStandardStreamRuleTestTest {

  private JavaClasses classesUsingStandardStream = new ClassFileImporter().importClasses(
      TestClassWithStandardStream.class);

  @Test
  public void shouldThrowViolations() {

    Throwable validationExceptionThrown = catchThrowable(() -> {

      classes().should(getNotUseStandardStream())
          .check(classesUsingStandardStream);
    });
  }
}
