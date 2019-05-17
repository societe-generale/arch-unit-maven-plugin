package com.societegenerale.commons.plugin.rules.classesForTests;

import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.societegenerale.commons.plugin.utils.ArchUtils.isJunitAssert;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

public class DummyCustomRule {

    static ArchRule annotatedWithTest = classes().should().beAnnotatedWith("Test");
    static ArchRule resideInMyPackage = classes().should().resideInAPackage("myPackage");
}