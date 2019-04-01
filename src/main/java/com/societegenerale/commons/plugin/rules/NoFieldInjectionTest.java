package com.societegenerale.commons.plugin.rules;

import com.societegenerale.commons.plugin.utils.ArchUtils;

import javax.inject.Inject;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noFields;

public class NoFieldInjectionTest implements ArchRuleTest  {

    @Override
    public void execute(String path) {

        noFields().should().beAnnotatedWith(Inject.class).check(ArchUtils.importAllClassesInPackage(path, ArchUtils.SRC_CLASSES_FOLDER));
    }
}
