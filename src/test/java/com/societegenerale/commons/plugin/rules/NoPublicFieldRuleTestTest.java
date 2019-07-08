package com.societegenerale.commons.plugin.rules;

import static com.societegenerale.commons.plugin.rules.NoPublicFieldRuleTest.NO_PUBLIC_FIELD_VIOLATION_MESSAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.societegenerale.commons.plugin.rules.classesForTests.ObjectWithNoPublicField;
import com.societegenerale.commons.plugin.rules.classesForTests.ObjectWithPublicField;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchRule;

public class NoPublicFieldRuleTestTest {

	@Test
	public void shouldThrowViolations() {

		assertExceptionIsThrownFor(ObjectWithPublicField.class);

	}

	@Test
	public void implementingArchRuleTest() {

		JavaClasses importedClass = new ClassFileImporter().importClasses(NoPublicFieldRuleTest.class);

		ArchRule rule = classes().should().implement(ArchRuleTest.class);

		rule.check(importedClass);

	}

	@Test
	public void shouldNotThrowAnyViolation() {

		assertNoExceptionIsThrownFor(ObjectWithNoPublicField.class);

	}

	private void assertExceptionIsThrownFor(Class clazz) {

		JavaClasses classToTest = new ClassFileImporter().importClasses(clazz);

		assertThatThrownBy(() -> {
			fields().should(NoPublicFieldRuleTest.notBePublicField()).check(classToTest);
		}).hasMessageStartingWith("Architecture Violation")
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