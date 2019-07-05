package com.societegenerale.commons.plugin.rules;

import static com.societegenerale.commons.plugin.rules.NoPublicFieldsRuleTest.NO_PUBLIC_FIELDS_VIOLATION_MESSAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.societegenerale.commons.plugin.rules.classesForTests.ObjectWithPublicField;
import com.societegenerale.commons.plugin.rules.classesForTests.ObjectWithNoPublicField;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

public class NoPublicFieldsRuleTestTest {

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
			classes().should(NoPublicFieldsRuleTest.noPublicFields()).check(classToTest);
		}).hasMessageContaining(ObjectWithPublicField.class.getName())
				.hasMessageContaining(NO_PUBLIC_FIELDS_VIOLATION_MESSAGE);

	}

	private void assertNoExceptionIsThrownFor(Class clazz) {

		JavaClasses classToTest = new ClassFileImporter().importClasses(clazz);

		assertThatCode(() -> classes().should(NoPublicFieldsRuleTest.noPublicFields()).check(classToTest))
				.doesNotThrowAnyException();

	}

}
