package com.societegenerale.commons.plugin.service;

import java.lang.reflect.Method;

import com.societegenerale.commons.plugin.model.ConfigurableRule;
import com.societegenerale.commons.plugin.service.InvokableRules.InvocationResult;
import com.tngtech.archunit.core.domain.JavaClasses;

import static com.societegenerale.commons.plugin.rules.ArchRuleTest.SRC_CLASSES_FOLDER;
import static com.societegenerale.commons.plugin.rules.ArchRuleTest.TEST_CLASSES_FOLDER;
import static com.societegenerale.commons.plugin.utils.ArchUtils.importAllClassesInPackage;
import static com.societegenerale.commons.plugin.utils.ReflectionUtils.loadClassWithContextClassLoader;

public class RuleInvokerService {

    private static final String EXECUTE_METHOD_NAME = "execute";

    public String invokePreConfiguredRule(String ruleClassName, String projectPath) {
        Class<?> ruleClass = loadClassWithContextClassLoader(ruleClassName);

        String errorMessage = "";
        try {
            Method method = ruleClass.getDeclaredMethod(EXECUTE_METHOD_NAME, String.class);
            method.invoke(ruleClass.newInstance(), projectPath);
        } catch (ReflectiveOperationException re) {
            errorMessage = re.getCause().toString();
        }
        return errorMessage;
    }

    public String invokeConfigurableRules(ConfigurableRule rule, String projectPath) {

        InvokableRules invokableRules = InvokableRules.of(rule.getRule(), rule.getChecks());

        String packageOnRuleToApply = getPackageNameOnWhichToApplyRules(rule);
        JavaClasses classes = importAllClassesInPackage(projectPath, packageOnRuleToApply);

        InvocationResult result = invokableRules.invokeOn(classes);
        return result.getMessage();
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
}
