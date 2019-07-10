package com.societegenerale.commons.plugin.rules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

import com.societegenerale.commons.plugin.utils.ArchUtils;

/**
 * It is important to respect encapsulation.
 * 
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/Encapsulation_(computer_programming)">Encapsulation</a>
 */

public class NoPublicFieldRuleTest implements ArchRuleTest {
	protected static final String NO_PUBLIC_FIELD_VIOLATION_MESSAGE = "you should respect encapsulation";

	@Override
	public void execute(String path) {

		fields().should().notBePublic().because(NO_PUBLIC_FIELD_VIOLATION_MESSAGE)
				.check(ArchUtils.importAllClassesInPackage(path, ""));
	}

}
