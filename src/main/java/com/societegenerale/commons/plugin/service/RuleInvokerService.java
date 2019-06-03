package com.societegenerale.commons.plugin.service;

import com.societegenerale.commons.plugin.model.ConfigurableRule;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.societegenerale.commons.plugin.rules.ArchRuleTest.SRC_CLASSES_FOLDER;
import static com.societegenerale.commons.plugin.rules.ArchRuleTest.TEST_CLASSES_FOLDER;
import static com.societegenerale.commons.plugin.utils.ArchUtils.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

public class RuleInvokerService {

    private static final String EXECUTE_METHOD_NAME = "execute";

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");


    public String invokePreConfiguredRule(Class<?> ruleClass, String projectPath) {

        String errorMessage = StringUtils.EMPTY;
        try {
            Method method = ruleClass.getDeclaredMethod(EXECUTE_METHOD_NAME, String.class);
            method.invoke(ruleClass.newInstance(), projectPath);
        } catch (ReflectiveOperationException re) {
            errorMessage = re.getCause().toString();
        }
        return errorMessage;
    }


    public String invokeConfigurableRules(Class<?> customRuleClass, ConfigurableRule rule, String projectPath) throws ReflectiveOperationException {

        StringBuilder failRuleMessagesBuilder = new StringBuilder(StringUtils.EMPTY);

        Map<String, Method> archConditionReturningMethods = getAllMethodsWhichReturnAnArchCondition(customRuleClass.getDeclaredMethods());
        Map<String, Field> archRuleFields = getAllFieldsWhichAreArchRules(customRuleClass.getDeclaredFields());

        String packageOnRuleToApply = getPackageNameOnWhichToApplyRules(rule);

        List<String> ruleChecks = rule.getChecks();
        final Map<String, Method> filteredArchConditions = new HashMap<>();
        final Map<String, Field> filteredArchRules = new HashMap<>();
        if (ruleChecks != null) {
            ruleChecks.forEach(check -> {
                if (archConditionReturningMethods.containsKey(check)) {
                    filteredArchConditions.putAll(archConditionReturningMethods.entrySet().stream()
                            .filter(map -> map.getKey().equals(check)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                } else if (archRuleFields.containsKey(check)) {
                    filteredArchRules.putAll(archRuleFields.entrySet().stream()
                            .filter(map -> map.getKey().equals(check)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
                }
            });
        } else {
            filteredArchConditions.putAll(archConditionReturningMethods);
            filteredArchRules.putAll(archRuleFields);
        }

        for (Method method : filteredArchConditions.values()) {
            failRuleMessagesBuilder.append(invokeArchUnitCondition(projectPath, method, customRuleClass, packageOnRuleToApply));
        }
        for (Field field : filteredArchRules.values()) {
            failRuleMessagesBuilder.append(invokeArchCustomRule(projectPath, field, customRuleClass, packageOnRuleToApply));
        }

        return failRuleMessagesBuilder.toString();
    }


    private String invokeArchCustomRule(String projectPath, Field field, Class<?> testClass, String packageOnRuleToApply)
            throws ReflectiveOperationException {

        StringBuilder errorMessageBuilder = new StringBuilder(StringUtils.EMPTY);
        field.setAccessible(true);
        ArchRule archRule = (ArchRule) field.get(testClass.newInstance());
        try {
            archRule.check(importAllClassesInPackage(projectPath, packageOnRuleToApply));
        } catch (AssertionError e) {
            errorMessageBuilder.append(e.getMessage()).append(LINE_SEPARATOR);
        }
        return errorMessageBuilder.toString();
    }

    @SuppressWarnings(value = "unchecked")
    private String invokeArchUnitCondition(String projectPath, Method method, Class<?> ruleClass, String packageOnRuleToApply) throws ReflectiveOperationException {
        StringBuilder errorMessageBuilder = new StringBuilder(StringUtils.EMPTY);
        Object ruleObject = method.invoke(ruleClass.newInstance());
        ArchCondition<JavaClass> archCondition = (ArchCondition<JavaClass>) ruleObject;
        try {
            classes().should(archCondition).check(importAllClassesInPackage(projectPath, packageOnRuleToApply));
        } catch (AssertionError e) {
            errorMessageBuilder.append(e.getMessage()).append(LINE_SEPARATOR);
        }
        return errorMessageBuilder.toString();
    }

    private String getPackageNameOnWhichToApplyRules(ConfigurableRule rule) {

        StringBuilder packageNameBuilder = new StringBuilder(SRC_CLASSES_FOLDER);

        if (rule.getApplyOn() != null) {
            if (rule.getApplyOn().getScope() != null && "test".equals(rule.getApplyOn().getScope())) {
                packageNameBuilder = new StringBuilder(TEST_CLASSES_FOLDER);
            }
            packageNameBuilder.append("/").append(rule.getApplyOn().getPackageName());

        }
        return packageNameBuilder.toString().replace(".", "/");
    }

    private Map<String, Method> getAllMethodsWhichReturnAnArchCondition(Method[] methods) {

        Map<String, Method> archConditionReturningMethods = new HashMap<>();
        for (Method method : methods) {
            if (method.getReturnType().equals(ArchCondition.class)) {
                archConditionReturningMethods.put(method.getName(), method);
            }
        }
        return archConditionReturningMethods;
    }

    private Map<String, Field> getAllFieldsWhichAreArchRules(Field[] fields) {

        Map<String, Field> archRuleFields = new HashMap<>();

        for (Field field : fields) {
            if (field.getType().equals(ArchRule.class)) {
                archRuleFields.put(field.getName(), field);
            }
        }

        return archRuleFields;
    }

}
