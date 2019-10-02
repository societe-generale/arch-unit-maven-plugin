package com.societegenerale.commons.plugin.rules;

import com.societegenerale.aut.test.TestClassWithAutowiredField;
import com.societegenerale.aut.test.TestClassWithInjectedField;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

import static com.societegenerale.commons.plugin.rules.NoInjectedFieldTest.NO_INJECTED_FIELD_MESSAGE;
import static com.societegenerale.commons.plugin.rules.NoInjectedFieldTest.notBeInjected;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static org.assertj.core.api.Assertions.*;

public class NoInjectedFieldTestTest {

    private JavaClasses testClassWithInjectedField = new ClassFileImporter().importClasses(TestClassWithInjectedField.class);

    //autowired fields should not trigger injected violation
    private JavaClasses testClassWithAutowiredField = new ClassFileImporter().importClasses(TestClassWithAutowiredField.class);


    @Test
    public void shouldThrowViolations(){

        Throwable validationExceptionThrown = catchThrowable(() -> {

                fields().should(notBeInjected()).check(testClassWithInjectedField);

        });

        assertThat(validationExceptionThrown).isInstanceOf(AssertionError.class)
                .hasMessageStartingWith("Architecture Violation")
                .hasMessageContaining("was violated (1 times)")
                .hasMessageContaining(TestClassWithInjectedField.class.getName())
                .hasMessageContaining(NO_INJECTED_FIELD_MESSAGE);

    }

    @Test
    public void shouldNotThrowAnyViolation(){
        assertThatCode(
                () -> fields().should(notBeInjected()).check(testClassWithAutowiredField))
                .doesNotThrowAnyException();
    }

}