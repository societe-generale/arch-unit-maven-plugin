package com.societegenerale.commons.plugin.rules.classesForTests;

import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.societegenerale.commons.plugin.utils.ArchUtils.isJunitAssert;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
import static com.tngtech.archunit.library.GeneralCodingRules.NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

public class CustomRuleForTest {

    private final ArchRule noGenericExceptionThrown = NO_CLASSES_SHOULD_THROW_GENERIC_EXCEPTIONS;
    private final ArchRule noJavaUtilLogging = NO_CLASSES_SHOULD_USE_JAVA_UTIL_LOGGING;

    public static ArchCondition<JavaClass> notUseJunitAssertRule() {

        return new ArchCondition<JavaClass>(ArchUtils.NO_JUNIT_ASSERT_DESCRIPTION) {
            @Override
            public void check(JavaClass item, ConditionEvents events) {

                item.getMethodCallsFromSelf().stream().filter(methodCall -> isJunitAssert(methodCall.getTarget().getOwner()))
                        .forEach(junitAssertCall -> events.add(SimpleConditionEvent.violated(junitAssertCall,
                                "Favor AssertJ assertions over Junit's - " + junitAssertCall.getDescription())));
            }
        };
    }
}