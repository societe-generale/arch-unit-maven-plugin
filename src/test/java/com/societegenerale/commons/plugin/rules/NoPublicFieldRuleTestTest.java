package com.societegenerale.commons.plugin.rules;

import static com.societegenerale.commons.plugin.rules.NoPublicFieldRuleTest.NO_PUBLIC_FIELD_VIOLATION_MESSAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

		assertThatThrownBy(() -> {
			fields().should().notBePublic().because(NO_PUBLIC_FIELD_VIOLATION_MESSAGE).check(classToTest);
		}).hasMessageStartingWith("Architecture Violation").hasMessageContaining(NO_PUBLIC_FIELD_VIOLATION_MESSAGE)
				.hasMessageContaining("was violated (1 times)").hasMessageContaining(clazz.getName());

	}

	private void assertNoExceptionIsThrownFor(Class clazz) {

		JavaClasses classToTest = new ClassFileImporter().importClasses(clazz);

		assertThatCode(
				() -> fields().should().notBePublic().because(NO_PUBLIC_FIELD_VIOLATION_MESSAGE).check(classToTest))
						.doesNotThrowAnyException();

	}

}
