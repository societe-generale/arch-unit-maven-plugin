package com.societegenerale.aut.test;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@Ignore
public class TestClassWithIgnoreAtClassLevel {

    @Test
    public void someTestUsingAssertJ() {

        assertThat(true).isTrue();

        fail("it failed ");

    }
}
