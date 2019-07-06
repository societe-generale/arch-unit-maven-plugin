package com.societegenerale.commons.plugin.rules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import javassist.Modifier;

/**
 * It is important to respect encapsulation.
 * 
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/Encapsulation_(computer_programming)">Encapsulation</a>
 */

public class NoPublicFieldRuleTest implements ArchRuleTest {
	protected static final String NO_PUBLIC_FIELD_VIOLATION_MESSAGE = "Respect encapsulation : no public field";

	@Override
	public void execute(String path) {
		fields().should(notBePublic()).check(ArchUtils.importAllClassesInPackage(path, SRC_CLASSES_FOLDER));
	}

	protected static ArchCondition<JavaField> notBePublic() {

		return new ArchCondition<JavaField>("not use public field") {
			@Override
			public void check(JavaField field, ConditionEvents events) {

				if (Modifier.isPublic(field.reflect().getModifiers()))

					events.add(SimpleConditionEvent.violated(field, NO_PUBLIC_FIELD_VIOLATION_MESSAGE + " - class: "
							+ field.getOwner().getName() + " - field name: " + field.getName()));

			}

		};
	}

}
