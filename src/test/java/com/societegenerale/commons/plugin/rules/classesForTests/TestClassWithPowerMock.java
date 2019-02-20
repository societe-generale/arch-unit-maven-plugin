package com.societegenerale.commons.plugin.rules.classesForTests;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(PowerMockRunner.class)
public class TestClassWithPowerMock {

    @Test
    public void someTestUsingAssertJ() {

        assertThat(true).isTrue();

        fail("it failed ");

    }
}
