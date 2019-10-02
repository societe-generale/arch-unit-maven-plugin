package com.societegenerale.commons.plugin.rules;

import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import javax.inject.Inject;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;

/**
 * We usually favor constructor injection rather than field injection : this way we can make sure the object is in correct state whenever it's used.
 */
public class NoInjectedFieldTest implements ArchRuleTest  {

    protected static final String NO_INJECTED_FIELD_MESSAGE = "Favor constructor injection and avoid field injection - ";

    @Override
    public void execute(String path) {

        fields().should(notBeInjected()).check(ArchUtils.importAllClassesInPackage(path, SRC_CLASSES_FOLDER));
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
