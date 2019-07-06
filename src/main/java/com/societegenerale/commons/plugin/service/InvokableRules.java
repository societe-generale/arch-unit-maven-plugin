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
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.lang.ArchRule;

import static com.societegenerale.commons.plugin.utils.ReflectionUtils.getValue;
import static com.societegenerale.commons.plugin.utils.ReflectionUtils.invoke;
import static com.societegenerale.commons.plugin.utils.ReflectionUtils.loadClassWithContextClassLoader;
import static com.societegenerale.commons.plugin.utils.ReflectionUtils.newInstance;
import static java.lang.System.lineSeparator;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;

class InvokableRules {
    private final Class<?> rulesLocation;
    private final Set<Field> archRuleFields;
    private final Set<Method> archRuleMethods;

    private InvokableRules(String rulesClassName, List<String> ruleChecks) {

        rulesLocation = loadClassWithContextClassLoader(rulesClassName);

        Set<Field> allFieldsWhichAreArchRules = getAllFieldsWhichAreArchRules(rulesLocation.getDeclaredFields());
        Set<Method> allMethodsWhichAreArchRules = getAllMethodsWhichAreArchRules(rulesLocation.getDeclaredMethods());
        validateRuleChecks(Sets.union(allMethodsWhichAreArchRules, allFieldsWhichAreArchRules), ruleChecks);

        Predicate<String> isChosenCheck = ruleChecks.isEmpty() ? check -> true : ruleChecks::contains;

        archRuleFields = filterNames(allFieldsWhichAreArchRules, isChosenCheck);
        archRuleMethods = filterNames(allMethodsWhichAreArchRules, isChosenCheck);
    }

    private void validateRuleChecks(Set<? extends Member> allFieldsAndMethods, Collection<String> ruleChecks) {
        Set<String> allFieldAndMethodNames = allFieldsAndMethods.stream().map(Member::getName).collect(toSet());
        Set<String> illegalChecks = Sets.difference(ImmutableSet.copyOf(ruleChecks), allFieldAndMethodNames);

        if (!illegalChecks.isEmpty()) {
            throw new IllegalChecksConfigurationException(rulesLocation, illegalChecks);
        }
    }

    private <M extends Member> Set<M> filterNames(Set<M> members, Predicate<String> namePredicate) {
        return members.stream()
                .filter(member -> namePredicate.test(member.getName()))
                .collect(toSet());
    }

    private Set<Method> getAllMethodsWhichAreArchRules(Method[] methods) {
        return stream(methods)
                .filter(m -> m.getParameterCount() == 1 && JavaClasses.class.isAssignableFrom(m.getParameterTypes()[0]))
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
        for (Method method : archRuleMethods) {
            checkForFailure(() -> invoke(method, instance, importedClasses))
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
