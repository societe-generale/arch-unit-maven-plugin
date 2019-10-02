package com.societegenerale.aut.test;


import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.fail;

public class TestClassWithJunit5Asserts {

    @Test
    public void someTest() {

        assertEquals(true,true);

        fail("it failed ");

    }
}
