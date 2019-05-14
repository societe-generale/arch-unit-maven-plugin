package com.societegenerale.commons.plugin.rules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static java.util.stream.Collectors.toList;

import java.util.List;

import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaMethodCall;
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

	@Override
	public void execute(String path) {
		classes().should(notUseJavaUtilDate())
				.check(ArchUtils.importAllClassesInPackage(path, ArchUtils.SRC_CLASSES_FOLDER));
	}

	protected static ArchCondition<JavaClass> notUseJavaUtilDate() {

		return new ArchCondition<JavaClass>("not use Java Util Date ") {
			@Override
			public void check(JavaClass item, ConditionEvents events) {

				List<JavaField> classesWithJavaUtilDateFields = item.getAllFields().stream()
						.filter(field -> isJavaUtilDateField(field)).collect(toList());

				for (JavaField field : classesWithJavaUtilDateFields) {
					events.add(SimpleConditionEvent.violated(field, ArchUtils.NO_JAVA_UTIL_DATE_VIOLATION_MESSAGE
							+ " - class: " + field.getOwner().getName() + " - field name: " + field.getName()));
				}

				List<JavaMethodCall> methodsUsingJavaUtilDateInternally = item.getCodeUnits().stream()
						.filter(codeUnit -> codeUnit instanceof JavaMethod)
						.flatMap(method -> method.getMethodCallsFromSelf().stream())
						.filter(method -> method instanceof JavaMethodCall)
						.filter(method -> isMethodUsingJavaUtilDateInternally(method)).collect(toList());

				for (JavaMethodCall methodCall : methodsUsingJavaUtilDateInternally) {
					events.add(SimpleConditionEvent.violated(methodCall.getOriginOwner(),
							ArchUtils.NO_JAVA_UTIL_DATE_VIOLATION_MESSAGE + " - class: "
									+ methodCall.getOriginOwner().getName() + " - line: "
									+ methodCall.getLineNumber()));
				}
			}

			@SuppressWarnings("deprecation")
			private boolean isJavaUtilDateField(JavaField field) {
				return field.getType().getName().startsWith(JAVA_UTIL_DATE_PACKAGE_PREFIX);
			}

			private boolean isMethodUsingJavaUtilDateInternally(JavaMethodCall javaMethodCall) {
				return javaMethodCall.getTarget().getFullName().startsWith(JAVA_UTIL_DATE_PACKAGE_PREFIX);
			}

		};
	}

}
