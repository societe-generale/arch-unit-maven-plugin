package com.societegenerale.commons.plugin.rules;

import com.societegenerale.commons.plugin.rules.classesForTests.TestClassWithJunitAsserts;
import com.societegenerale.commons.plugin.rules.classesForTests.TestClassWithOutJunitAsserts;
import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.*;

public class NoJunitAssertRuleTestTest {

    private JavaClasses testClassWithJunitAsserts = new ClassFileImporter().importClasses(TestClassWithJunitAsserts.class);
    private JavaClasses  tesClassesWithoutJunitAsserts = new ClassFileImporter().importClasses(TestClassWithOutJunitAsserts.class);

    @Test
    public void shouldNotThrowAnyViolation(){
        assertThatCode(
                () -> classes().should(NoJunitAssertRuleTest.notUseJunitAssertRule()).check(tesClassesWithoutJunitAsserts))
                .doesNotThrowAnyException();
    }


    @Test
    public void shouldThrowViolations(){

        Throwable validationExceptionThrown = catchThrowable(() -> {

            classes().should(NoJunitAssertRuleTest.notUseJunitAssertRule()).check(testClassWithJunitAsserts);

        });

        assertThat(validationExceptionThrown).isInstanceOf(AssertionError.class)
                .hasMessageContaining("was violated (2 times)")
                .hasMessageContaining("calls method <org.junit.Assert.assertTrue(boolean)")
                .hasMessageContaining("calls method <org.junit.Assert.fail(java.lang.String)");

        assertThat(validationExceptionThrown).hasMessageStartingWith("Architecture Violation")
                .hasMessageContaining(TestClassWithJunitAsserts.class.getName())
                .hasMessageContaining(ArchUtils.NO_JUNIT_ASSERT_DESCRIPTION);
    }

}