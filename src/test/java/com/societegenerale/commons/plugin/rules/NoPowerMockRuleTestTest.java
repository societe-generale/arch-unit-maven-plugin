package com.societegenerale.commons.plugin.rules;

import com.societegenerale.aut.test.TestClassWithOutJunitAsserts;
import com.societegenerale.aut.test.TestClassWithPowerMock;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

import static com.societegenerale.commons.plugin.rules.NoPowerMockRuleTest.POWER_MOCK_VIOLATION_MESSAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.*;

public class NoPowerMockRuleTestTest {

    private JavaClasses testClassWithPowerMock = new ClassFileImporter().importClasses(TestClassWithPowerMock.class);
    private JavaClasses testClassWithoutPowerMock = new ClassFileImporter().importClasses(TestClassWithOutJunitAsserts.class);


    @Test
    public void shouldNotThrowAnyViolation(){
        assertThatCode(
                () -> classes().should(NoPowerMockRuleTest.notUsePowerMock()).check(testClassWithoutPowerMock))
                .doesNotThrowAnyException();
    }


    @Test
    public void shouldThrowViolations(){

        Throwable validationExceptionThrown = catchThrowable(() -> {

            classes().should(NoPowerMockRuleTest.notUsePowerMock()).check(testClassWithPowerMock);

        });

        assertThat(validationExceptionThrown).isInstanceOf(AssertionError.class)
                .hasMessageStartingWith("Architecture Violation")
                .hasMessageContaining("was violated (1 times)")
                .hasMessageContaining(TestClassWithPowerMock.class.getName())
                .hasMessageContaining(POWER_MOCK_VIOLATION_MESSAGE);

    }

}