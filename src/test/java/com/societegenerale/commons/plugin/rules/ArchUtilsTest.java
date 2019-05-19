package com.societegenerale.commons.plugin.rules;

import com.societegenerale.commons.plugin.utils.ArchUtils;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.assertj.core.api.Assertions.assertThat;

public class ArchUtilsTest {


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
    assertThat(noOfClasses).isEqualTo(16);
  }

}
