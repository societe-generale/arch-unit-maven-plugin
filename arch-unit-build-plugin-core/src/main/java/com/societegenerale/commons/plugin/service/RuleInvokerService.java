package com.societegenerale.commons.plugin.service;

import java.lang.reflect.Method;

import com.societegenerale.commons.plugin.model.ConfigurableRule;
import com.societegenerale.commons.plugin.model.Rules;
import com.societegenerale.commons.plugin.service.InvokableRules.InvocationResult;
import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaClasses;
import org.apache.commons.lang3.StringUtils;
import com.societegenerale.commons.plugin.Log;

import static com.societegenerale.commons.plugin.rules.ArchRuleTest.SRC_CLASSES_FOLDER;
import static com.societegenerale.commons.plugin.rules.ArchRuleTest.TEST_CLASSES_FOLDER;
import static com.societegenerale.commons.plugin.utils.ReflectionUtils.loadClassWithContextClassLoader;
import static java.lang.System.lineSeparator;

public class RuleInvokerService {
    private static final String EXECUTE_METHOD_NAME = "execute";

    private Log log;

    private ArchUtils archUtils;

    public RuleInvokerService(Log log) {
        this.log=log;
        archUtils =new ArchUtils(log);
    }

    public String invokeRules(Rules rules, String projectPath) {

        StringBuilder errorListBuilder = new StringBuilder();

        for (String rule : rules.getPreConfiguredRules()) {
            String errorMessage = invokePreConfiguredRule(rule, projectPath);
            errorListBuilder.append(prepareErrorMessageForRuleFailures(rule, errorMessage));
        }

        for (ConfigurableRule rule : rules.getConfigurableRules()) {
            String errorMessage = invokeConfigurableRules(rule, projectPath);
            errorListBuilder.append(prepareErrorMessageForRuleFailures(rule.getRule(), errorMessage));
        }

        return errorListBuilder.toString();

    }

    private String invokePreConfiguredRule(String ruleClassName, String projectPath) {
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

    private String invokeConfigurableRules(ConfigurableRule rule, String projectPath) {
        if(rule.isSkip()) {
            if(log.isInfoEnabled()) {
                log.info("Skipping rule " + rule.getRule());
            }
            return "";
        }

        InvokableRules invokableRules = InvokableRules.of(rule.getRule(), rule.getChecks());

        String packageOnRuleToApply = getPackageNameOnWhichToApplyRules(rule);
        JavaClasses classes = archUtils.importAllClassesInPackage(projectPath, packageOnRuleToApply);

        InvocationResult result = invokableRules.invokeOn(classes);
        return result.getMessage();
    }

    private String getPackageNameOnWhichToApplyRules(ConfigurableRule rule) {

        StringBuilder packageNameBuilder = new StringBuilder(SRC_CLASSES_FOLDER);

        if (rule.getApplyOn() != null) {
            if (rule.getApplyOn().getScope() != null && "test".equals(rule.getApplyOn().getScope())) {
                packageNameBuilder = new StringBuilder(TEST_CLASSES_FOLDER);
            }
            if (rule.getApplyOn().getPackageName() != null) {
                packageNameBuilder.append("/").append(rule.getApplyOn().getPackageName());
            }

        }
        return packageNameBuilder.toString().replace(".", "/");
    }


    private String prepareErrorMessageForRuleFailures(String rule, String errorMessage) {

        StringBuilder errorBuilder = new StringBuilder();
        if (StringUtils.isNotEmpty(errorMessage)) {
            errorBuilder
                    .append("Rule Violated - ").append(rule).append(lineSeparator())
                    .append(errorMessage)
                    .append(lineSeparator());
        }
        return errorBuilder.toString();
    }
}
