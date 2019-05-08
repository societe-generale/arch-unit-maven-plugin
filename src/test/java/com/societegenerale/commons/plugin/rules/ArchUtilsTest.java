package com.societegenerale.commons.plugin.rules;

import com.societegenerale.commons.plugin.model.ApplyOn;
import com.societegenerale.commons.plugin.model.ConfigurableRule;
import com.societegenerale.commons.plugin.rules.classesForTests.TestClassHavingArchRule;
import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class ArchUtilsTest {

  private JavaClass testClassWithJunitAsserts = new ClassFileImporter().importClass(Assert.class);

  @Test
  public void testIsJunitAssertMethod() {
    assertThat(ArchUtils.isJunitAssert(testClassWithJunitAsserts)).isTrue();
  }

  @Test
  public void constructorInvocationTest() {
    try {
      final Constructor<ArchUtils> c = ArchUtils.class.getDeclaredConstructor();
      c.setAccessible(true);
      final ArchUtils newInstance = c.newInstance();
      Assert.assertNull(newInstance);
    } catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException
        | InvocationTargetException e) {
      assertThat(e).hasCauseExactlyInstanceOf(UnsupportedOperationException.class);
    }
  }

  @Test
  public void testNumberOfRulesAvailable() {
    JavaClasses classes = ArchUtils.importAllClassesInPackage("./target/classes/com/societegenerale/commons/plugin/", "rules");
    int noOfClasses = 0;
    for (JavaClass javaClass : classes) {
      if(! javaClass.getName().contains("$") && !javaClass.isInterface()) {
        noOfClasses++;
      }
    }
    assertThat(noOfClasses).isEqualTo(9);
  }

  @Test
  public void totalClassesInPathWhenPackageFolderDoesNotExists() {
    JavaClasses classes = ArchUtils.importAllClassesInPackage("./target/classes", "classFolder");
    int noOfClasses = 0;
    for (JavaClass javaClass : classes) {
      if(!javaClass.getName().contains("$")) {
        noOfClasses++;
      }
    }
    assertThat(noOfClasses).isEqualTo(15);
  }

  @Test
  public void getPackageNameTestForConfigurableRuleWhenNoPackageAndScopeIsGiven() {

    ConfigurableRule configurableRule = new ConfigurableRule();
    String actualPackage =  ArchUtils.getPackageNameOnWhichRulesToApply(configurableRule);
    assertThat(actualPackage).isEqualTo("/classes");

  }

  @Test
  public void getPackageNameTestForConfigurableRuleWhenPackageAndScopeAreGiven() {

    ConfigurableRule configurableRule = new ConfigurableRule();
    ApplyOn applyOn = new ApplyOn();
    applyOn.setPackageName("com.socgen.package");
    applyOn.setScope("test");
    configurableRule.setApplyOn(applyOn);
    String actualPackage =  ArchUtils.getPackageNameOnWhichRulesToApply(configurableRule);
    assertThat(actualPackage).isEqualTo("/test-classes/com/socgen/package");
  }


  @Test
  public void testGetAllMethodsWhichReturnAnArchCondition(){

    Class<?> testClass = NoJunitAssertRuleTest.class;

    Map<String, Method> actualArchConditionMap = ArchUtils.getAllMethodsWhichReturnAnArchCondition(testClass.getDeclaredMethods());

    assertThat(actualArchConditionMap).isNotEmpty();

  }

  @Test
  public void testGetAllFieldsWhichAreArchRules(){

    Class<?> testClass = TestClassHavingArchRule.class;

    Map<String, Field> actualArchConditionMap = ArchUtils.getAllFieldsWhichAreArchRules(testClass.getDeclaredFields());

    assertThat(actualArchConditionMap).isNotEmpty();

  }

}
