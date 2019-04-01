package com.societegenerale.commons.plugin.rules;

import com.societegenerale.commons.plugin.rules.classesForTests.TestClassWithInjectedField;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

import static com.societegenerale.commons.plugin.rules.NoFieldInjectionTest.notBeInjected;
import static com.societegenerale.commons.plugin.utils.ArchUtils.NO_INJECTED_FIELD_MESSAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;


public class NoFieldInjectionTestTest {

    private JavaClasses testClassWithInjectedField = new ClassFileImporter().importClasses(TestClassWithInjectedField.class);

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

}