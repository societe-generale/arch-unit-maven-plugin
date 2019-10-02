package com.societegenerale.commons.plugin.rules;

import com.societegenerale.aut.test.TestClassWithAutowiredField;
import com.societegenerale.aut.test.TestClassWithInjectedField;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

import static com.societegenerale.commons.plugin.rules.NoAutowiredFieldTest.NO_AUTOWIRED_FIELD_MESSAGE;
import static com.societegenerale.commons.plugin.rules.NoAutowiredFieldTest.notBeAutowired;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static org.assertj.core.api.Assertions.*;

public class NoAutowiredFieldTestTest {

    private JavaClasses testClassWithAutowiredField = new ClassFileImporter().importClasses(TestClassWithAutowiredField.class);

    //injected fields should not trigger autowired violation
    private JavaClasses testClassWithInjectedField = new ClassFileImporter().importClasses(TestClassWithInjectedField.class);

    @Test
    public void shouldThrowViolations(){

        Throwable validationExceptionThrown = catchThrowable(() -> {

                fields().should(notBeAutowired()).check(testClassWithAutowiredField);

        });

        assertThat(validationExceptionThrown).isInstanceOf(AssertionError.class)
                .hasMessageStartingWith("Architecture Violation")
                .hasMessageContaining("was violated (1 times)")
                .hasMessageContaining(TestClassWithAutowiredField.class.getName())
                .hasMessageContaining(NO_AUTOWIRED_FIELD_MESSAGE);

    }

    @Test
    public void shouldNotThrowAnyViolation(){
        assertThatCode(
                () -> fields().should(notBeAutowired()).check(testClassWithInjectedField))
                .doesNotThrowAnyException();
    }

}