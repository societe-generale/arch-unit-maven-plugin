package com.societegenerale.commons.plugin.rules.classesForTests;

public class TestClassWhichThrowGenericException {

    public void testMethod() throws Exception{
        throw new Exception("For testing custom rules");
    }
}
