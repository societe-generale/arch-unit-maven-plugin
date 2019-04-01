package com.societegenerale.commons.plugin.rules.classesForTests;

import javax.inject.Inject;


public class TestClassWithInjectedField {

    @Inject
    Object someDummyInjectedStuff;


    public TestClassWithInjectedField() {

        //we should use constructor injection instead

    }
}
