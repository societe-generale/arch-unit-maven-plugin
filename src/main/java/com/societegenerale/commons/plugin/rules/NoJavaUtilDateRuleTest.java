package com.societegenerale.commons.plugin.rules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

/**
 * java.util.Date is deprecated but a lot of people still use it out of years of
 * habit. This rule will catch such instances and remind developers they should
 * use alternatives (java.time, java.util.GregorianCalendar ,
 * java.text.DateFormat (and its subclasses) to parse and format dates) because
 * they support internationalization better
 * 
 * 
 *
 * @see <a href=
 *      "https://www.math.uni-hamburg.de/doc/java/tutorial/post1.0/converting/deprecated.html">java.util.Date
 *      is deprecated</a> : <i>developers can use other libraries : java.time,
 *      java.util.GregorianCalendar ; java.text.DateFormat ; ... </i>
 * 
 * @see <a href=
 *      "https://docs.oracle.com/javase/8/docs/api/java/time/package-summary.html">Java
 *      8 Time Oracle</a>
 */

public class NoJavaUtilDateRuleTest implements ArchRuleTest {

	private static final String JAVA_UTIL_DATE_PACKAGE_PREFIX = "java.util.Date";

	protected static final String NO_JAVA_UTIL_DATE_VIOLATION_MESSAGE = "Use Java8 java.time or java.util.GregorianCalendar or java.text.DateFormat  to parse and format dates instead of java.util.Date library because they  support internationalization better";

	@Override
	public void execute(String path) {
		classes().should(notUseJavaUtilDate()).check(ArchUtils.importAllClassesInPackage(path, SRC_CLASSES_FOLDER));
	}

	protected static ArchCondition<JavaClass> notUseJavaUtilDate() {

		return new ArchCondition<JavaClass>("not use Java Util Date") {
			@Override
			public void check(JavaClass item, ConditionEvents events) {

				item.getAllFields().stream().filter(field -> isJavaUtilDateField(field)).forEach(field -> {
					events.add(SimpleConditionEvent.violated(field,
							NO_JAVA_UTIL_DATE_VIOLATION_MESSAGE + " - class: " + field.getOwner().getName()));
				});

			}

			private boolean isJavaUtilDateField(JavaField field) {
				return field.getRawType().getName().startsWith(JAVA_UTIL_DATE_PACKAGE_PREFIX);
			}

		};
	}

}
