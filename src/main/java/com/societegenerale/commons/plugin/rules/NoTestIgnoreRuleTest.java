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
 * This rule will make sure we don't have any test marked as @Ignore. Code is in source control, so we can always remove the test and add it back later, instead of having dozens of @Ignore tests piling up
 */
public class NoTestIgnoreRuleTest implements ArchRuleTest  {

  protected static final String NO_JUNIT_IGNORE_VIOLATION_MESSAGE = "Tests shouldn't been ignored";

  public void execute(String path) {
    classes().should(notBeenIgnore()).check(ArchUtils.importAllClassesInPackage(path, TEST_CLASSES_FOLDER));
  }

  public static ArchCondition<JavaClass> notBeenIgnore() {

    return new ArchCondition<JavaClass>(NO_JUNIT_IGNORE_VIOLATION_MESSAGE) {

      @Override
      @SuppressWarnings("squid:S1166")
      public void check(JavaClass item, ConditionEvents events) {

        try {
          if (item.getAnnotationOfType(Ignore.class) != null) {
            events.add(SimpleConditionEvent.violated(item, item.getName() + ", at class level"));
          }
        } catch (IllegalArgumentException e) {
          //if there's no Ignore annotation, IllegalArgument exception is thrown.
          //we swallow it, as it means there's no annotation at class level.
        }

        for (JavaMethod method : item.getMethods()) {
          try {
            if (method.getAnnotationOfType(Ignore.class) != null) {
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
