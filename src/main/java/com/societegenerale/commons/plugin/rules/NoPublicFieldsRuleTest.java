package com.societegenerale.commons.plugin.rules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaClass;
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
 * 
 */

public class NoPublicFieldsRuleTest implements ArchRuleTest {

	protected static final String NO_PUBLIC_FIELDS_VIOLATION_MESSAGE = "Respect encapsulation : no public fields";

	@Override
	public void execute(String path) {
		classes().should(noPublicFields()).check(ArchUtils.importAllClassesInPackage(path, SRC_CLASSES_FOLDER));
	}

	protected static ArchCondition<JavaClass> noPublicFields() {

		return new ArchCondition<JavaClass>("no Public Fields") {
			@Override
			public void check(JavaClass item, ConditionEvents events) {

				item.getAllFields().stream().filter(field -> isPublicField(field)).forEach(field -> {
					events.add(SimpleConditionEvent.violated(field,
							NO_PUBLIC_FIELDS_VIOLATION_MESSAGE + " - class: " + field.getOwner().getName()));
				});

			}

			private boolean isPublicField(JavaField field) {

				int modifiers = field.reflect().getModifiers();

				return Modifier.isPublic(modifiers);

			}

		};
	}

}
