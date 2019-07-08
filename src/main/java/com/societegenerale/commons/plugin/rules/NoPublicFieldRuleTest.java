package com.societegenerale.commons.plugin.rules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.lang.ArchRule;

/**
 * It is important to respect encapsulation.
 * 
 * @see <a href=
 *      "https://en.wikipedia.org/wiki/Encapsulation_(computer_programming)">Encapsulation</a>
 */

public class NoPublicFieldRuleTest implements ArchRuleTest {
	protected static final String NO_PUBLIC_FIELD_VIOLATION_MESSAGE = "you should respect encapsulation : no public field";

	protected static final ArchRule rule = fields().should().notBePublic().because(NO_PUBLIC_FIELD_VIOLATION_MESSAGE);

	@Override
	public void execute(String path) {

		rule.check(ArchUtils.importAllClassesInPackage(path, SRC_CLASSES_FOLDER));
	}

}
