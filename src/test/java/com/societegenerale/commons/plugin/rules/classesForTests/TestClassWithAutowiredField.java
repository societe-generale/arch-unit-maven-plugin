package com.societegenerale.commons.plugin.rules.classesForTests;

import org.springframework.beans.factory.annotation.Autowired;

public class TestClassWithAutowiredField {

    @Autowired
    Object someDummyInjectedStuff;


    public TestClassWithAutowiredField() {

        //we should use constructor injection instead

    }
}
