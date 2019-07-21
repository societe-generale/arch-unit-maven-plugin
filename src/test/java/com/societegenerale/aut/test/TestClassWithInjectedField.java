package com.societegenerale.aut.test;

import javax.inject.Inject;


public class TestClassWithInjectedField {

    @Inject
    Object someDummyInjectedStuff;


    public TestClassWithInjectedField() {

        //we should use constructor injection instead

    }
}
