package com.societegenerale.commons.plugin.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.societegenerale.commons.plugin.service.RuleInvokerService;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by agarg020917 on 11/17/2017.
 */
public class ArchUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(RuleInvokerService.class);

    private ArchUtils() {
        throw new UnsupportedOperationException();
    }

    public static JavaClasses importAllClassesInPackage(String path, String classFolder) {
        Path classesPath = Paths.get(path + classFolder);
        if (classesPath.toFile().exists()) {
            return new ClassFileImporter().importPath(classesPath);
        }
        else{
            LOGGER.warn("classpath {} doesn't exist : loading all classes from root, ie {}",classesPath.toFile(),path);
            return new ClassFileImporter().importPath(Paths.get(path));
        }

    }

}
