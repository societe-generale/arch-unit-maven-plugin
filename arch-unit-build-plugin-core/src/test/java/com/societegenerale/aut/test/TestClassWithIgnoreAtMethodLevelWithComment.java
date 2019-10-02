package com.societegenerale.aut.test;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class TestClassWithIgnoreAtMethodLevelWithComment {

    @Test
    @Ignore("here's a comment explaining why it's ignored")
    public void someTestUsingAssertJ() {

        assertThat(true).isTrue();

        fail("it failed ");

    }
}
