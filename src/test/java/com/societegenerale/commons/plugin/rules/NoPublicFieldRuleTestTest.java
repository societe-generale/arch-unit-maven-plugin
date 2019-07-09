package com.societegenerale.commons.plugin.rules;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.Test;

public class NoPublicFieldRuleTestTest {

	String pathObjectWithNoPublicField = "./target/test-classes/com/societegenerale/commons/plugin/rules/classesForTests/ObjectWithNoPublicField.class";

	String pathObjectWithPublicField = "./target/test-classes/com/societegenerale/commons/plugin/rules/classesForTests/ObjectWithPublicField.class";

	@Test(expected = AssertionError.class)
	public void shouldThrowViolations() {

		new NoPublicFieldRuleTest().execute(pathObjectWithPublicField);

	}

	@Test
	public void shouldNotThrowAnyViolation() {

		assertThatCode(() -> new NoPublicFieldRuleTest().execute(pathObjectWithNoPublicField))
				.doesNotThrowAnyException();

	}

}
