package com.societegenerale.commons.plugin.rules;

import static org.assertj.core.api.Assertions.assertThatCode;

import org.junit.Test;

public class NoPublicFieldRuleTestTest {

	String pathObjectWithNoPublicField = "./target/test-classes/com/societegenerale/commons/plugin/rules/classesForTests/ObjectWithNoPublicField.class";

	String pathObjectWithPublicField = "./target/test-classes/com/societegenerale/commons/plugin/rules/classesForTests/ObjectWithPublicField.class";

	String pathObjectWithPublicStaticFinalField = "./target/test-classes/com/societegenerale/commons/plugin/rules/classesForTests/ObjectWithPublicStaticFinalField.class";

	@Test(expected = AssertionError.class)
	public void shouldThrowViolations() {

		new NoPublicFieldRuleTest().execute(pathObjectWithPublicField);

	}

	@Test
	public void shouldNotThrowAnyViolation1() {

		assertThatCode(() -> new NoPublicFieldRuleTest().execute(pathObjectWithNoPublicField))
				.doesNotThrowAnyException();

	}

	@Test
	public void shouldNotThrowAnyViolation2() {

		assertThatCode(() -> new NoPublicFieldRuleTest().execute(pathObjectWithPublicStaticFinalField))
				.doesNotThrowAnyException();

	}

}
