package com.societegenerale.commons.plugin.rules;

import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.Ignore;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * You may want to accept having tests marked as @Ignore, but only if a comment is provided explaining the reason - there's nothing worse than a test marked as @Ignore, with no indication as of why it has been ignored.
 */
public class NoTestIgnoreWithoutCommentRuleTest implements ArchRuleTest  {

    protected static final String NO_JUNIT_IGNORE_WITHOUT_COMMENT_VIOLATION_MESSAGE = "Tests shouldn't been ignored without providing a comment explaining why";

    public void execute(String path)  {
        classes().should(notBeIgnoredWithoutAComment()).check(ArchUtils.importAllClassesInPackage(path, TEST_CLASSES_FOLDER));
    }

    public static ArchCondition<JavaClass> notBeIgnoredWithoutAComment() {

        return new ArchCondition<JavaClass>(NO_JUNIT_IGNORE_WITHOUT_COMMENT_VIOLATION_MESSAGE) {

            @Override
            @SuppressWarnings("squid:S1166")
            public void check(JavaClass item, ConditionEvents events) {

                try {
                    if (item.getAnnotationOfType(Ignore.class).value().isEmpty()) {
                        events.add(SimpleConditionEvent.violated(item, item.getName() + ", at class level"));
                    }
                } catch (IllegalArgumentException e) {
                    //if there's no Ignore annotation, IllegalArgument exception is thrown.
                    //we swallow it, as it means there's no annotation at class level.
                }

                for (JavaMethod method : item.getMethods()) {
                    try {
                        if (method.getAnnotationOfType(Ignore.class).value().isEmpty()) {
                            events.add(SimpleConditionEvent.violated(method, item.getName()+" - "+method.getName() + ", at method level"));
                        }
                    } catch (IllegalArgumentException e) {
                        //if there's no Ignore annotation, IllegalArgument exception is thrown.
                        //we swallow it, as it means there's no annotation at method level.
                    }

                }

            }
        };
    }
}
