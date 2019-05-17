package com.societegenerale.commons.plugin.service;

import com.societegenerale.commons.plugin.model.ApplyOn;
import com.societegenerale.commons.plugin.model.ConfigurableRule;
import com.societegenerale.commons.plugin.rules.NoStandardStreamRuleTest;
import com.societegenerale.commons.plugin.rules.classesForTests.DummyCustomRule;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class RuleInvokerServiceTest {

    RuleInvokerService ruleInvokerService = new RuleInvokerService();

    ConfigurableRule configurableRule = new ConfigurableRule();

    @Test
    public void shouldInvokePreConfiguredRulesMethod() {

        String errorMessage = ruleInvokerService.invokePreConfiguredRule(NoStandardStreamRuleTest.class, "./target/test-classes/com/societegenerale/commons/plugin/rules/classesForTests");

        assertThat(errorMessage).isNotEmpty();
        assertThat(errorMessage).contains("Architecture Violation");
        assertThat(errorMessage).contains("Rule 'no classes should access standard streams' was violated ");


    }

    @Test
    public void shouldExecute2ConfigurableRulesOnTest() throws ReflectiveOperationException {

        ApplyOn applyOn = new ApplyOn("com.societegenerale.commons.plugin.rules","test");

        configurableRule.setApplyOn(applyOn);
        configurableRule.setChecks(Arrays.asList("annotatedWithTest","resideInMyPackage"));

        String errorMessage = ruleInvokerService.invokeConfigurableRules(DummyCustomRule.class, configurableRule, "./target/test-classes/com/societegenerale/commons/plugin/rules/classesForTests");
        assertThat(errorMessage).isNotEmpty();
        assertThat(errorMessage).contains("Architecture Violation");
        assertThat(errorMessage).contains("classes should be annotated with @Test");
        assertThat(errorMessage).contains("classes should reside in a package 'myPackage'");
    }

    @Test
    public void shouldExecuteOnlyTheConfiguredRule() throws ReflectiveOperationException {

        ApplyOn applyOn = new ApplyOn("com.societegenerale.commons.plugin.rules","test");

        configurableRule.setApplyOn(applyOn);
        configurableRule.setChecks(Arrays.asList("annotatedWithTest"));

        String errorMessage = ruleInvokerService.invokeConfigurableRules(DummyCustomRule.class, configurableRule, "./target/test-classes/com/societegenerale/commons/plugin/rules/classesForTests");
        assertThat(errorMessage).isNotEmpty();
        assertThat(errorMessage).contains("Architecture Violation");
        assertThat(errorMessage).contains("classes should be annotated with @Test");
        assertThat(errorMessage).doesNotContain("classes should reside in a package 'myPackage'");
    }


    @Test
    public void shouldExecuteAllRulesFromConfigurableClassByDefault() throws ReflectiveOperationException {

        Class<?> ruleClass = DummyCustomRule.class;

        ApplyOn applyOn = new ApplyOn("com.societegenerale.commons.plugin.rules","main");

        configurableRule.setApplyOn(applyOn);

        String errorMessage = ruleInvokerService.invokeConfigurableRules(ruleClass, configurableRule, "./target/classes/com/societegenerale/commons/plugin/rules");

        assertThat(errorMessage).isNotEmpty();
        assertThat(errorMessage).contains("Architecture Violation");
        assertThat(errorMessage).contains("classes should be annotated with @Test");
        assertThat(errorMessage).contains("classes should reside in a package 'myPackage'");
    }

    @Test
    public void shouldExecuteAllRulesOnSpecificPackageInTest() throws ReflectiveOperationException {

        Class<?> ruleClass = DummyCustomRule.class;

        ApplyOn applyOn = new ApplyOn("com.societegenerale.commons.plugin.rules","test");
        configurableRule.setApplyOn(applyOn);

        String errorMessage = ruleInvokerService.invokeConfigurableRules(ruleClass, configurableRule, "./target/test-classes/com/societegenerale/commons/plugin/rules/classesForTests/specificCase");
        assertThat(errorMessage).isNotEmpty();
        assertThat(errorMessage).contains("Architecture Violation");
        assertThat(errorMessage).contains("Rule 'classes should be annotated with @Test' was violated (1 times)");
        assertThat(errorMessage).contains("Rule 'classes should reside in a package 'myPackage'' was violated (1 times)");
    }
}
