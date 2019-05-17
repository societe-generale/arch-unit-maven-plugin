package com.societegenerale.commons.plugin.utils;

import com.societegenerale.commons.plugin.model.ConfigurableRule;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by agarg020917 on 11/17/2017.
 */
public class ArchUtils {

    public static final String NO_JUNIT_ASSERT_DESCRIPTION = "not use Junit assertions";

    public static final String TEST_CLASSES_FOLDER = "/test-classes";
    public static final String SRC_CLASSES_FOLDER = "/classes";


    public static final String NO_PREFIX_INTERFACE_VIOLATION_MESSAGE = " : Interfaces shouldn't be prefixed with \"I\" - caller doesn't need to know it's an interface + this is a .Net convention";
    public static final String POWER_MOCK_VIOLATION_MESSAGE = "Favor Mockito and proper dependency injection - ";
    public static final String NO_INJECTED_FIELD_MESSAGE = "Favor constructor injection and avoid field injection - ";
    public static final String NO_AUTOWIRED_FIELD_MESSAGE = "Favor constructor injection and avoid autowiring fields - ";
    public static final String NO_JODA_VIOLATION_MESSAGE = "Use Java8 Date API instead of Joda library";
    public static final String NO_JUNIT_IGNORE_VIOLATION_MESSAGE = "Tests shouldn't been ignored";
    public static final String NO_JUNIT_IGNORE_WITHOUT_COMMENT_VIOLATION_MESSAGE = "Tests shouldn't been ignored without providing a comment explaining why";
    public static final String PREFIX_ARCH_VIOLATION_MESSAGE = "ArchUnit Maven plugin reported architecture failures listed below :";

    public static final String LINE_SEPARATOR = "line.separator";


    private ArchUtils() {
        throw new UnsupportedOperationException();
    }

    public static JavaClasses importAllClassesInPackage(String path, String classFolder) {
        Path classesPath = Paths.get(path + classFolder);
        if (classesPath.toFile().exists()) {
            return new ClassFileImporter().importPath(classesPath);
        }
        return new ClassFileImporter().importPath(Paths.get(path));
    }




}