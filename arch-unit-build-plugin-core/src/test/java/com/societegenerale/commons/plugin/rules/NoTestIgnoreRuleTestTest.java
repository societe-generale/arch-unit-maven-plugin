package com.societegenerale.commons.plugin.rules;

import com.societegenerale.aut.test.TestClassWithIgnoreAtClassLevel;
import com.societegenerale.aut.test.TestClassWithIgnoreAtMethodLevel;
import com.societegenerale.aut.test.TestClassWithOutJunitAsserts;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

import static com.societegenerale.commons.plugin.rules.NoTestIgnoreRuleTest.NO_JUNIT_IGNORE_VIOLATION_MESSAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.*;

public class NoTestIgnoreRuleTestTest {

    private JavaClasses testClassWithIgnore = new ClassFileImporter().importClasses(TestClassWithIgnoreAtMethodLevel.class,TestClassWithIgnoreAtClassLevel.class);
    private JavaClasses testClassWithoutIgnoreAtAll= new ClassFileImporter().importClasses(TestClassWithOutJunitAsserts.class);

    @Test
    public void shouldNotThrowAnyViolation(){
        assertThatCode(
                () -> classes().should(NoTestIgnoreRuleTest.notBeenIgnore()).check(testClassWithoutIgnoreAtAll))
                .doesNotThrowAnyException();
    }


    @Test
    public void shouldThrowViolations(){

        Throwable validationExceptionThrown = catchThrowable(() -> {

            classes().should(NoTestIgnoreRuleTest.notBeenIgnore()).check(testClassWithIgnore);

        });

        assertThat(validationExceptionThrown).isInstanceOf(AssertionError.class)
                .hasMessageStartingWith("Architecture Violation")
                .hasMessageContaining("was violated (2 times)")
                .hasMessageContaining(TestClassWithIgnoreAtClassLevel.class.getName()+", at class level")
                .hasMessageContaining(TestClassWithIgnoreAtMethodLevel.class.getName()+" - someIgnoredTestWithoutAComment, at method level")
                .hasMessageContaining(NO_JUNIT_IGNORE_VIOLATION_MESSAGE);

    }
}
