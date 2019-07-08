package com.societegenerale.commons.plugin.rules;

import static com.societegenerale.commons.plugin.rules.NoPublicFieldRuleTest.NO_PUBLIC_FIELD_VIOLATION_MESSAGE;
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

			NoPublicFieldRuleTest.rule.check(classToTest);

		});

		assertThat(validationExceptionThrown).isInstanceOf(AssertionError.class)
				.hasMessageStartingWith("Architecture Violation")
				.hasMessageContaining(NO_PUBLIC_FIELD_VIOLATION_MESSAGE).hasMessageContaining("was violated (1 times)")
				.hasMessageContaining(clazz.getName());

	}

	private void assertNoExceptionIsThrownFor(Class clazz) {

		JavaClasses classToTest = new ClassFileImporter().importClasses(clazz);

		assertThatCode(() -> NoPublicFieldRuleTest.rule.check(classToTest)).doesNotThrowAnyException();

	}

	/*
	 * 
	 * private ArchCondition<JavaField> notBePublicField() {
	 * 
	 * return new ArchCondition<JavaField>("not use public field") {
	 * 
	 * @Override public void check(JavaField field, ConditionEvents events) {
	 * 
	 * if (Modifier.isPublic(field.reflect().getModifiers()))
	 * 
	 * events.add(SimpleConditionEvent.violated(field,
	 * NO_PUBLIC_FIELD_VIOLATION_MESSAGE + " - class: " + field.getOwner().getName()
	 * + " - field name: " + field.getName()));
	 * 
	 * }
	 * 
	 * }; }
	 */

}
