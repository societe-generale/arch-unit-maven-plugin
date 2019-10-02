package com.societegenerale.commons.plugin.rules;

import com.societegenerale.aut.test.TestClassWithIgnoreAtClassLevel;
import com.societegenerale.aut.test.TestClassWithIgnoreAtClassLevelWithComment;
import com.societegenerale.aut.test.TestClassWithIgnoreAtMethodLevel;
import com.societegenerale.aut.test.TestClassWithIgnoreAtMethodLevelWithComment;
import com.societegenerale.aut.test.TestClassWithOutJunitAsserts;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Test;

import static com.societegenerale.commons.plugin.rules.NoTestIgnoreWithoutCommentRuleTest.NO_JUNIT_IGNORE_WITHOUT_COMMENT_VIOLATION_MESSAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.*;

public class NoTestIgnoreWithoutCommentRuleTestTest {

    private JavaClasses testClassWithIgnoreButNoComment = new ClassFileImporter().importClasses(TestClassWithIgnoreAtMethodLevel.class, TestClassWithIgnoreAtClassLevel.class);
    private JavaClasses testClassWithIgnoreAndComment = new ClassFileImporter().importClasses(TestClassWithIgnoreAtMethodLevelWithComment.class, TestClassWithIgnoreAtClassLevelWithComment.class);
    private JavaClasses testClassWithoutIgnoreAtAll= new ClassFileImporter().importClasses(TestClassWithOutJunitAsserts.class);

    @Test
    public void shouldNotThrowAnyViolation(){
        assertThatCode(
                () -> classes().should(NoTestIgnoreWithoutCommentRuleTest.notBeIgnoredWithoutAComment()).check(testClassWithoutIgnoreAtAll))
                .doesNotThrowAnyException();

        assertThatCode(
                () -> classes().should(NoTestIgnoreWithoutCommentRuleTest.notBeIgnoredWithoutAComment()).check(testClassWithIgnoreAndComment))
                .doesNotThrowAnyException();
    }

    @Test
    public void shouldThrowViolations(){

        Throwable validationExceptionThrown = catchThrowable(() -> {

            classes().should(NoTestIgnoreWithoutCommentRuleTest.notBeIgnoredWithoutAComment()).check(testClassWithIgnoreButNoComment);

        });

        assertThat(validationExceptionThrown).isInstanceOf(AssertionError.class)
                .hasMessageStartingWith("Architecture Violation")
                .hasMessageContaining("was violated (2 times)")
                .hasMessageContaining(TestClassWithIgnoreAtClassLevel.class.getName()+", at class level")
                .hasMessageContaining(TestClassWithIgnoreAtMethodLevel.class.getName()+" - someIgnoredTestWithoutAComment, at method level")
                .hasMessageContaining(NO_JUNIT_IGNORE_WITHOUT_COMMENT_VIOLATION_MESSAGE);

    }
}
