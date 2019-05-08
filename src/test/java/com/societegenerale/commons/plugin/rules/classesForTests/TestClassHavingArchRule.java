package com.societegenerale.commons.plugin.rules.classesForTests;

import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;

public class TestClassHavingArchRule {

    private final ArchRule noStandardStreamUsage = NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS;
}
