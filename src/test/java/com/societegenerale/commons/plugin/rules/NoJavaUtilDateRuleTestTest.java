package com.societegenerale.commons.plugin.rules;

import static com.societegenerale.commons.plugin.utils.ArchUtils.NO_JAVA_UTIL_DATE_VIOLATION_MESSAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.Test;

import com.societegenerale.commons.plugin.rules.classesForTests.ObjectWithJava8TimeLib;
import com.societegenerale.commons.plugin.rules.classesForTests.ObjectWithJavaTextDateFormat;
import com.societegenerale.commons.plugin.rules.classesForTests.ObjectWithJavaUtilDateReferences;
import com.societegenerale.commons.plugin.rules.classesForTests.ObjectWithJavaUtilGregorianCalendar;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

public class NoJavaUtilDateRuleTestTest {

	private JavaClasses classesUsingJavaUtilDateLibrary = new ClassFileImporter()
			.importClasses(ObjectWithJavaUtilDateReferences.class);

	private JavaClasses classesUsingJava8Library = new ClassFileImporter().importClasses(ObjectWithJava8TimeLib.class);
	private JavaClasses classesUsingJavaTextDateFormatLibrary = new ClassFileImporter()
			.importClasses(ObjectWithJavaTextDateFormat.class);
	private JavaClasses classesUsingJavaUtilGregorianCalendarLibrary = new ClassFileImporter()
			.importClasses(ObjectWithJavaUtilGregorianCalendar.class);

	@Test
	public void shouldCatchViolationsInStaticBlocksAndMemberFields() {

		Throwable validationExceptionThrown = catchThrowable(() -> {

			classes().should(NoJavaUtilDateRuleTest.notUseJavaUtilDate()).check(classesUsingJavaUtilDateLibrary);

		});

		assertThat(validationExceptionThrown).hasMessageContaining(ObjectWithJavaUtilDateReferences.class.getName())
				.hasMessageContaining(NO_JAVA_UTIL_DATE_VIOLATION_MESSAGE);
	}

	@Test(expected = Throwable.class)
	public void shouldThrowNoJavaUtilDateViolation() {
		classes().should(NoJavaUtilDateRuleTest.notUseJavaUtilDate()).check(classesUsingJavaUtilDateLibrary);
	}

	@Test
	public void shouldNotThrowAnyViolation() {
		assertThatCode(
				() -> classes().should(NoJavaUtilDateRuleTest.notUseJavaUtilDate()).check(classesUsingJava8Library))
						.doesNotThrowAnyException();

		assertThatCode(() -> classes().should(NoJavaUtilDateRuleTest.notUseJavaUtilDate())
				.check(classesUsingJavaTextDateFormatLibrary)).doesNotThrowAnyException();

		assertThatCode(() -> classes().should(NoJavaUtilDateRuleTest.notUseJavaUtilDate())
				.check(classesUsingJavaUtilGregorianCalendarLibrary)).doesNotThrowAnyException();
	}

}
