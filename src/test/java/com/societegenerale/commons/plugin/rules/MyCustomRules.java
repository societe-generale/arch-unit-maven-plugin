package com.societegenerale.commons.plugin.rules;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * A dummy class with rules to configure through plugin config
 */
@SuppressWarnings("unused")
public class MyCustomRules {

    static ArchRule annotatedWithTest_asField = classes().should().beAnnotatedWith("Test");
    static ArchRule resideInMyPackage_asField = classes().should().resideInAPackage("myPackage");

    static void annotatedWithTest_asMethod(JavaClasses importedClasses) {
        classes().should().beAnnotatedWith("Test").check(importedClasses);
    }

    static void resideInMyPackage_asMethod(JavaClasses importedClasses) {
        classes().should().resideInAPackage("myPackage").check(importedClasses);
    }
}
