package com.societegenerale.aut.test;

import org.junit.Ignore;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;


public class TestClassWithIgnoreAtMethodLevel {

    @Test
    @Ignore
    public void someIgnoredTestWithoutAComment() {

        assertThat(true).isTrue();

        fail("it failed ");

    }
}
