package com.societegenerale.commons.plugin.service;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;

import static com.societegenerale.commons.plugin.utils.ReflectionUtils.getValue;
import static com.societegenerale.commons.plugin.utils.ReflectionUtils.invoke;
import static com.societegenerale.commons.plugin.utils.ReflectionUtils.loadClassWithContextClassLoader;
import static com.societegenerale.commons.plugin.utils.ReflectionUtils.newInstance;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

class InvokableRules {
    private final Class<?> rulesLocation;
    private final Set<Field> archRuleFields;
    private final Set<Method> archConditionReturningMethods;

    private InvokableRules(String rulesClassName, List<String> ruleChecks) {

        rulesLocation = loadClassWithContextClassLoader(rulesClassName);

        Set<Field> allFieldsWhichAreArchRules = getAllFieldsWhichAreArchRules(rulesLocation.getDeclaredFields());
        Set<Method> allMethodsWhichReturnAnArchCondition = getAllMethodsWhichReturnAnArchCondition(rulesLocation.getDeclaredMethods());

        Predicate<String> isChosenCheck = ruleChecks.isEmpty() ? check -> true : ruleChecks::contains;

        archRuleFields = filterNames(allFieldsWhichAreArchRules, isChosenCheck);
        archConditionReturningMethods = filterNames(allMethodsWhichReturnAnArchCondition, isChosenCheck);
    }

    private <M extends Member> Set<M> filterNames(Set<M> members, Predicate<String> namePredicate) {
        return members.stream()
                .filter(member -> namePredicate.test(member.getName()))
                .collect(toSet());
    }

    private Set<Method> getAllMethodsWhichReturnAnArchCondition(Method[] methods) {
        return stream(methods)
                .filter(m -> ArchCondition.class.isAssignableFrom(m.getReturnType()))
                .collect(toSet());
    }

    private Set<Field> getAllFieldsWhichAreArchRules(Field[] fields) {
        return stream(fields)
                .filter(f -> ArchRule.class.isAssignableFrom(f.getType()))
                .collect(toSet());
    }

    InvocationResult invokeOn(JavaClasses importedClasses) {

        Object instance = newInstance(rulesLocation);

        InvocationResult result = new InvocationResult();
        for (Method method : archConditionReturningMethods) {
            ArchCondition<JavaClass> condition = invoke(method, instance);
            checkForFailure(() -> classes().should(condition).check(importedClasses))
                    .ifPresent(result::add);
        }
        for (Field field : archRuleFields) {
            ArchRule rule = getValue(field, instance);
            checkForFailure(() -> rule.check(importedClasses))
                    .ifPresent(result::add);
        }
        return result;
    }

    private Optional<String> checkForFailure(Runnable runnable) {
        try {
            runnable.run();
            return Optional.empty();
        } catch (RuntimeException | AssertionError e) {
            return Optional.of(e.getMessage());
        }
    }

    static InvokableRules of(String rulesClassName, List<String> checks) {
        return new InvokableRules(rulesClassName, checks);
    }

    static class InvocationResult {
        private final List<String> violations = new ArrayList<>();

        private void add(String violationMessage) {
            violations.add(violationMessage);
        }

        String getMessage() {
            return violations.stream().collect(joining(lineSeparator()));
        }
    }
}
