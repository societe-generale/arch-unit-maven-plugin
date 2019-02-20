package com.societegenerale.commons.plugin.rules;

import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

/**
 * Created by agarg020917 on 11/3/2017.
 */

public class NoJunitAssertRuleTest implements ArchRuleTest {

  @Override
  public void execute(String path) {
    classes().should(notUseJunitAssertRule()).check(ArchUtils.importAllClassesInPackage(path, ArchUtils.TEST_CLASSES_FOLDER));
  }

  protected static ArchCondition<JavaClass> notUseJunitAssertRule() {

    return new ArchCondition<JavaClass>(ArchUtils.NO_JUNIT_ASSERT_DESCRIPTION) {
      @Override
      public void check(JavaClass item, ConditionEvents events) {

        item.getMethodCallsFromSelf().stream().filter(methodCall -> ArchUtils.isJunitAssert(methodCall.getTarget().getOwner()))
            .forEach(junitAssertCall -> events.add(SimpleConditionEvent.violated(junitAssertCall,
                "Favor AssertJ assertions over Junit's - " + junitAssertCall.getDescription())));
      }
    };
  }
}
