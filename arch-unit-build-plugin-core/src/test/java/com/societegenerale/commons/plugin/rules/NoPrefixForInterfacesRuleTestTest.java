package com.societegenerale.commons.plugin.rules;

import com.societegenerale.aut.main.IInterfaceWithIncorrectName;
import com.societegenerale.aut.main.InterfaceWithCorrectName;
import com.societegenerale.aut.main.TotallyGoodInterfaceName;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

import static com.societegenerale.commons.plugin.rules.NoPrefixForInterfacesRuleTest.NO_PREFIX_INTERFACE_VIOLATION_MESSAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.*;

public class NoPrefixForInterfacesRuleTestTest {

    private JavaClasses interfacesWithIncorrectNames = new ClassFileImporter().importClasses(IInterfaceWithIncorrectName.class);
    private JavaClasses interfacesWithProperNames = new ClassFileImporter().importClasses(InterfaceWithCorrectName.class, TotallyGoodInterfaceName.class);

    @Test
    public void shouldNotThrowAnyViolation(){
        assertThatCode(
                () -> classes().should(NoPrefixForInterfacesRuleTest.notBePrefixed()).check(interfacesWithProperNames))
                .doesNotThrowAnyException();
    }


    @Test
    public void shouldThrowViolations(){

        Throwable validationExceptionThrown = catchThrowable(() -> {

            classes().should(NoPrefixForInterfacesRuleTest.notBePrefixed()).check(interfacesWithIncorrectNames);

        });

        assertThat(validationExceptionThrown).isInstanceOf(AssertionError.class)
                .hasMessageStartingWith("Architecture Violation")
                .hasMessageContaining("was violated (1 times)")
                .hasMessageContaining(IInterfaceWithIncorrectName.class.getName())
                .hasMessageContaining(NO_PREFIX_INTERFACE_VIOLATION_MESSAGE);

    }




}