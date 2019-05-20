package com.societegenerale.commons.plugin.rules;

import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;


/**
 * Interfaces shouldn't be prefixed with "I" like it's the case in .Net
 *
 * @see : https://stackoverflow.com/a/2814831/3067542 for the rationale
 */
public class NoPrefixForInterfacesRuleTest implements ArchRuleTest {

  protected static final String NO_PREFIX_INTERFACE_VIOLATION_MESSAGE = " : Interfaces shouldn't be prefixed with \"I\" - caller doesn't need to know it's an interface + this is a .Net convention";

  private static Character upperCaseI = 'I';

  @Override
  public void execute(String path) {

    classes().that().areInterfaces().should(notBePrefixed()).check(ArchUtils.importAllClassesInPackage(path, SRC_CLASSES_FOLDER));

  }

  public static ArchCondition<JavaClass> notBePrefixed() {

    return new ArchCondition<JavaClass>("not be prefixed with I - this is a .Net convention.") {
      @Override
      public void check(JavaClass item, ConditionEvents events) {

        Character firstCharacter = item.getSimpleName().charAt(0);
        Character secondCharacter = item.getSimpleName().charAt(1);

        if (firstCharacter.equals(upperCaseI) && Character.isUpperCase(secondCharacter)) {
          events.add(SimpleConditionEvent.violated(item, item.getName() + NO_PREFIX_INTERFACE_VIOLATION_MESSAGE));
        }
      }
    };


  }


}
