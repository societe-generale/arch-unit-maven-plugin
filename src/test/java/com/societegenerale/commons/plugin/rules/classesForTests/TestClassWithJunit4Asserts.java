package com.societegenerale.commons.plugin.rules.classesForTests;

import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestClassWithJunit4Asserts {

    @Test
    public void someTest() {

        assertTrue(true);

        fail("it failed ");

    }
}
