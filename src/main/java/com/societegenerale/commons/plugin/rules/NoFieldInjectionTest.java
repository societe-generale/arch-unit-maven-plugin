package com.societegenerale.commons.plugin.rules;

import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import javax.inject.Inject;

import static com.societegenerale.commons.plugin.utils.ArchUtils.NO_INJECTED_FIELD_MESSAGE;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

public class NoFieldInjectionTest implements ArchRuleTest  {


    @Override
    public void execute(String path) {

        fields().should(notBeInjected()).check(ArchUtils.importAllClassesInPackage(path, ArchUtils.SRC_CLASSES_FOLDER));
    }

    protected static ArchCondition<JavaField> notBeInjected() {

        return new ArchCondition<JavaField>("not be injected") {

            @Override
            public void check(JavaField javaField, ConditionEvents events) {

                if(javaField.isAnnotatedWith(Inject.class)){

                    events.add(SimpleConditionEvent.violated(javaField, NO_INJECTED_FIELD_MESSAGE
                            +" - class: "+javaField.getOwner().getName()
                            +" - field name: "+javaField.getName()));

                }


            }
        };
    }
}
