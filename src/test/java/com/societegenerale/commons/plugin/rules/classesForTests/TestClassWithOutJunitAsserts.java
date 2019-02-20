package com.societegenerale.commons.plugin.rules.classesForTests;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class TestClassWithOutJunitAsserts {

    @Test
    public void someTestUsingAssertJ() {

        assertThat(true).isTrue();

        fail("it failed ");

    }
}
