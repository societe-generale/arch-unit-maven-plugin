package com.societegenerale.commons.plugin.rules.classesForTests;

import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class DummyCustomRule {

    static ArchRule annotatedWithTest = classes().should().beAnnotatedWith("Test");
    static ArchRule resideInMyPackage = classes().should().resideInAPackage("myPackage");
}