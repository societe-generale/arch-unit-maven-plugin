package com.societegenerale.commons.plugin.rules;

import static com.societegenerale.commons.plugin.rules.NoJavaUtilDateRuleTest.NO_JAVA_UTIL_DATE_VIOLATION_MESSAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Test;

import com.societegenerale.aut.main.ObjectWithAdateField;
import com.societegenerale.aut.main.ObjectWithJava8TimeLib;
import com.societegenerale.aut.main.ObjectWithJavaTextDateFormat;
import com.societegenerale.aut.main.ObjectWithJavaUtilGregorianCalendar;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

public class NoJavaUtilDateRuleTestTest {

	@Test
	public void shouldThrowViolations() {

		assertExceptionIsThrownFor(ObjectWithAdateField.class);

	}

	@Test
	public void shouldNotThrowAnyViolation() {

		assertNoExceptionIsThrownFor(ObjectWithJava8TimeLib.class);

		assertNoExceptionIsThrownFor(ObjectWithJavaTextDateFormat.class);

		assertNoExceptionIsThrownFor(ObjectWithJavaUtilGregorianCalendar.class);

	}

	private void assertExceptionIsThrownFor(Class clazz) {

		JavaClasses classToTest = new ClassFileImporter().importClasses(clazz);

		assertThatThrownBy(() -> {
			classes().should(NoJavaUtilDateRuleTest.notUseJavaUtilDate()).check(classToTest);
		}).hasMessageContaining(ObjectWithAdateField.class.getName())
				.hasMessageContaining(NO_JAVA_UTIL_DATE_VIOLATION_MESSAGE);

	}

	private void assertNoExceptionIsThrownFor(Class clazz) {

		JavaClasses classToTest = new ClassFileImporter().importClasses(clazz);

		assertThatCode(() -> classes().should(NoJavaUtilDateRuleTest.notUseJavaUtilDate()).check(classToTest))
				.doesNotThrowAnyException();

	}

}
