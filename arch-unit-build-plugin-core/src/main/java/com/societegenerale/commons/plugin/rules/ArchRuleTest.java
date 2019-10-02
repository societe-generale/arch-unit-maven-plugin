package com.societegenerale.commons.plugin.rules;

/**
 * Created by agarg020917 on 11/10/2017.
 */
@FunctionalInterface
public interface ArchRuleTest {

  static final String SRC_CLASSES_FOLDER = "/classes";
  static final String TEST_CLASSES_FOLDER = "/test-classes";

  void execute(String path);

}
