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
	public static final String NO_PUBLIC_FIELD_VIOLATION_MESSAGE = "you should respect encapsulation";

	@Override
	public void execute(String path) {

		ArchRule rulePublic = fields().that().areNotStatic().or().areNotFinal().should().notBePublic()
				.because(NO_PUBLIC_FIELD_VIOLATION_MESSAGE);

		rulePublic.check(ArchUtils.importAllClassesInPackage(path, ""));

	}

}
