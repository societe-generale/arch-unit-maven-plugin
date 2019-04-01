package com.societegenerale.commons.plugin.rules;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

public class NoFieldAutowiringTest implements ArchRuleTest  {

    @Override
    public void execute(String path) {
            noFields().should().beAnnotatedWith("org.springframework.beans.factory.annotation.Autowired");
    }
}
