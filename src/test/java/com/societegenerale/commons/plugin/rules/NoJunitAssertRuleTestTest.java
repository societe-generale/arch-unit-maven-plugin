package com.societegenerale.commons.plugin.rules;

import com.societegenerale.aut.test.TestClassWithJunit4Asserts;
import com.societegenerale.aut.test.TestClassWithJunit5Asserts;
import com.societegenerale.aut.test.TestClassWithOutJunitAsserts;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

import static com.societegenerale.commons.plugin.rules.NoJunitAssertRuleTest.NO_JUNIT_ASSERT_DESCRIPTION;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.*;

public class NoJunitAssertRuleTestTest {

    private JavaClasses testClassWithJunit4Asserts = new ClassFileImporter().importClasses(TestClassWithJunit4Asserts.class);
    private JavaClasses testClassWithJunit5Asserts = new ClassFileImporter().importClasses(TestClassWithJunit5Asserts.class);
    private JavaClasses  tesClassesWithoutJunitAsserts = new ClassFileImporter().importClasses(TestClassWithOutJunitAsserts.class);

    @Test
    public void shouldNotThrowAnyViolation(){
        assertThatCode(
                () -> classes().should(NoJunitAssertRuleTest.notUseJunitAssertRule()).check(tesClassesWithoutJunitAsserts))
                .doesNotThrowAnyException();
    }


    @Test
    public void shouldThrowForJunit4Violations(){

        Throwable validationExceptionThrown = catchThrowable(() -> {

            classes().should(NoJunitAssertRuleTest.notUseJunitAssertRule()).check(testClassWithJunit4Asserts);

        });

        assertThat(validationExceptionThrown).isInstanceOf(AssertionError.class)
                .hasMessageContaining("was violated (2 times)")
                .hasMessageContaining("calls method <org.junit.Assert.assertTrue(boolean)")
                .hasMessageContaining("calls method <org.junit.Assert.fail(java.lang.String)");

        assertThat(validationExceptionThrown).hasMessageStartingWith("Architecture Violation")
                .hasMessageContaining(TestClassWithJunit4Asserts.class.getName())
                .hasMessageContaining(NO_JUNIT_ASSERT_DESCRIPTION);
    }

    @Test
    public void shouldThrowForJunit5Violations(){

        Throwable validationExceptionThrown = catchThrowable(() -> {

            classes().should(NoJunitAssertRuleTest.notUseJunitAssertRule()).check(testClassWithJunit5Asserts);

        });

        assertThat(validationExceptionThrown).isInstanceOf(AssertionError.class)
                .hasMessageContaining("was violated (2 times)")
                .hasMessageContaining("calls method <org.junit.jupiter.api.Assertions.assertEquals(java.lang.Object, java.lang.Object)")
                .hasMessageContaining("org.junit.jupiter.api.Assertions.fail(java.lang.String)");

        assertThat(validationExceptionThrown).hasMessageStartingWith("Architecture Violation")
                .hasMessageContaining(TestClassWithJunit5Asserts.class.getName())
                .hasMessageContaining(NO_JUNIT_ASSERT_DESCRIPTION);
    }

}