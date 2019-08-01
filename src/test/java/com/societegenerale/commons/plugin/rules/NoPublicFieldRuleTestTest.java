package com.societegenerale.commons.plugin.rules;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

public class NoPublicFieldRuleTestTest {

	String pathObjectWithNoPublicField = "./target/aut-target/test-classes/com/societegenerale/aut/test/ObjectWithNoNonStaticPublicField.class";

	String pathObjectWithPublicField = "./target/aut-target/test-classes/com/societegenerale/aut/test/ObjectWithPublicField.class";

	@Test(expected = AssertionError.class)
	public void shouldThrowViolations() {

		new NoPublicFieldRuleTest().execute(pathObjectWithPublicField);

	}

	@Test
	public void shouldNotThrowAnyViolation_even_with_publicStaticFinaField() {

		assertThatCode(() -> new NoPublicFieldRuleTest().execute(pathObjectWithNoPublicField))
				.doesNotThrowAnyException();

	}

}
