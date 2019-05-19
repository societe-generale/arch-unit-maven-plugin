package com.societegenerale.commons.plugin.service;

import com.societegenerale.commons.plugin.model.ApplyOn;
import com.societegenerale.commons.plugin.model.ConfigurableRule;
import com.societegenerale.commons.plugin.rules.NoStandardStreamRuleTest;
import com.societegenerale.commons.plugin.rules.classesForTests.CustomRuleForTest;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleInvokerServiceTest {


    @Test
    public void testInvokePreConfiguredRulesMethod() {

        Class<?> ruleClass = NoStandardStreamRuleTest.class;

        RuleInvokerService ruleInvokerService = new RuleInvokerService();
        String errorMessage = ruleInvokerService.invokePreConfiguredRule(ruleClass, "./target/test-classes/com/societegenerale/commons/plugin/rules/classesForTests");

        assertThat(errorMessage).isNotEmpty();
        assertThat(errorMessage).contains("Architecture Violation");
        assertThat(errorMessage).contains("Rule 'no classes should access standard streams' was violated ");


    }

    @Test
    public void testCustomConfiguredRuleChecksWithScopeTest() throws ReflectiveOperationException {

        Class<?> ruleClass = CustomRuleForTest.class;

        ConfigurableRule configurableRule = new ConfigurableRule();
        ApplyOn applyOn = new ApplyOn();
        applyOn.setPackageName("com.societegenerale.commons.plugin.rules");
        applyOn.setScope("test");

        List<String> checks = new ArrayList<>();
        checks.add("noGenericExceptionThrown");
        checks.add("notUseJunitAssertRule");

        configurableRule.setApplyOn(applyOn);
        configurableRule.setChecks(checks);

        RuleInvokerService ruleInvokerService = new RuleInvokerService();

        String errorMessage = ruleInvokerService.invokeConfigurableRules(ruleClass, configurableRule, "./target/test-classes/com/societegenerale/commons/plugin/rules/classesForTests");
        assertThat(errorMessage).isNotEmpty();
        assertThat(errorMessage).contains("Architecture Violation");
        assertThat(errorMessage).contains("Rule 'classes should not use Junit assertions' was violated (4 times)");
        assertThat(errorMessage).contains("no classes should throw generic exceptions' was violated (1 times)");
    }


    @Test
    public void testCustomConfiguredRuleWithScopeMain() throws ReflectiveOperationException {

        Class<?> ruleClass = CustomRuleForTest.class;

        ConfigurableRule configurableRule = new ConfigurableRule();
        ApplyOn applyOn = new ApplyOn();
        applyOn.setPackageName("com.societegenerale.commons.plugin.rules");
        applyOn.setScope("main");
        configurableRule.setApplyOn(applyOn);

        RuleInvokerService ruleInvokerService = new RuleInvokerService();
        String errorMessage = ruleInvokerService.invokeConfigurableRules(ruleClass, configurableRule, "./target/classes/com/societegenerale/commons/plugin/rules");
        assertThat(errorMessage).isEmpty();

    }

    @Test
    public void testCustomConfiguredRuleWithScopeTest() throws ReflectiveOperationException {

        Class<?> ruleClass = CustomRuleForTest.class;

        ConfigurableRule configurableRule = new ConfigurableRule();
        ApplyOn applyOn = new ApplyOn();
        applyOn.setPackageName("com.societegenerale.commons.plugin.rules");
        applyOn.setScope("test");
        configurableRule.setApplyOn(applyOn);
        RuleInvokerService ruleInvokerService = new RuleInvokerService();

        String errorMessage = ruleInvokerService.invokeConfigurableRules(ruleClass, configurableRule, "./target/test-classes/com/societegenerale/commons/plugin/rules/classesForTests");
        assertThat(errorMessage).isNotEmpty();
        assertThat(errorMessage).contains("Architecture Violation");
        assertThat(errorMessage).contains("Rule 'classes should not use Junit assertions' was violated (4 times)");
        assertThat(errorMessage).contains("no classes should throw generic exceptions' was violated (1 times)");
    }
}
