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