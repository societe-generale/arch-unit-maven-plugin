package com.societegenerale.commons.plugin.rules;

import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * A dummy class with rules to configure through plugin config
 */
@SuppressWarnings("unused")
public class MyCustomRules {

    static ArchRule annotatedWithTest = classes().should().beAnnotatedWith("Test");
    static ArchRule resideInMyPackage = classes().should().resideInAPackage("myPackage");

}
