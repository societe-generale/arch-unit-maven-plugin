package com.societegenerale.commons.plugin.rules;

import static com.societegenerale.commons.plugin.rules.NoPublicFieldRuleTest.NO_PUBLIC_FIELD_VIOLATION_MESSAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.Test;

import com.societegenerale.commons.plugin.rules.classesForTests.ObjectWithNoPublicField;
import com.societegenerale.commons.plugin.rules.classesForTests.ObjectWithPublicField;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

public class NoPublicFieldRuleTestTest {

	@Test
	public void shouldThrowViolations() {

		assertExceptionIsThrownFor(ObjectWithPublicField.class);

	}

	@Test
	public void shouldNotThrowAnyViolation() {

		assertNoExceptionIsThrownFor(ObjectWithNoPublicField.class);

	}

	private void assertExceptionIsThrownFor(Class clazz) {

		JavaClasses classToTest = new ClassFileImporter().importClasses(clazz);

		Throwable validationExceptionThrown = catchThrowable(() -> {

			fields().should(NoPublicFieldRuleTest.notBePublicField()).check(classToTest);

		});

		assertThat(validationExceptionThrown).isInstanceOf(AssertionError.class)
				.hasMessageStartingWith("Architecture Violation")
				.hasMessageContaining("Rule 'fields should not use public field'")
				.hasMessageContaining("was violated (1 times)").hasMessageContaining(NO_PUBLIC_FIELD_VIOLATION_MESSAGE)
				.hasMessageContaining(" - class: ").hasMessageContaining(clazz.getName())
				.hasMessageContaining(" - field name: ");

	}

	private void assertNoExceptionIsThrownFor(Class clazz) {

		JavaClasses classToTest = new ClassFileImporter().importClasses(clazz);

		assertThatCode(() -> fields().should(NoPublicFieldRuleTest.notBePublicField()).check(classToTest))
				.doesNotThrowAnyException();

	}

}
