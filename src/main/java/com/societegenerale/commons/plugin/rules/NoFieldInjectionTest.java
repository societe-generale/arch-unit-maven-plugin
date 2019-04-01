package com.societegenerale.commons.plugin.rules;

import javax.inject.Inject;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

public class NoFieldInjectionTest implements ArchRuleTest  {

    @Override
    public void execute(String path) {
        noFields().should().beAnnotatedWith(Inject.class);
    }
}
