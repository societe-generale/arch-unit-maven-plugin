package com.societegenerale.commons.plugin.rules;

import com.societegenerale.aut.main.ObjectWithJava8TimeLib;
import com.societegenerale.aut.main.ObjectWithJodaTimeReferences;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

import static com.societegenerale.commons.plugin.rules.NoJodaTimeRuleTest.NO_JODA_VIOLATION_MESSAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.*;

public class NoJodaTimeRuleTestTest {

    private JavaClasses classesUsingJodaLibrary = new ClassFileImporter().importClasses(ObjectWithJodaTimeReferences.class);
    private JavaClasses classesUsingJava8Libray= new ClassFileImporter().importClasses(ObjectWithJava8TimeLib.class);

    @Test
    public void shouldCatchViolationsInStaticBlocksAndMemberFields(){

        Throwable validationExceptionThrown = catchThrowable(() -> {

            classes().should(NoJodaTimeRuleTest.notUseJodaTime()).check(classesUsingJodaLibrary);

        });

        assertThat(validationExceptionThrown).isInstanceOf(AssertionError.class)
                                             .hasMessageContaining("was violated (2 times)")
                                             .hasMessageContaining("ObjectWithJodaTimeReferences - field name: jodaDatTime")
                                             .hasMessageContaining("ObjectWithJodaTimeReferences - line: 17");

        assertThat(validationExceptionThrown).hasMessageStartingWith("Architecture Violation")
            .hasMessageContaining(ObjectWithJodaTimeReferences.class.getName())
            .hasMessageContaining(NO_JODA_VIOLATION_MESSAGE);
    }


    @Test(expected = Throwable.class)
    public void shouldThrowNOJODAViolation(){
        classes().should(NoJodaTimeRuleTest.notUseJodaTime()).check(classesUsingJodaLibrary);
    }

    @Test
    public void shouldNotThrowAnyViolation(){
        assertThatCode(
            () -> classes().should(NoJodaTimeRuleTest.notUseJodaTime()).check(classesUsingJava8Libray))
            .doesNotThrowAnyException();
    }
}
