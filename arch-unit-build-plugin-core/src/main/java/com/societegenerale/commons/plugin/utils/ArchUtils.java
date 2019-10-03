package com.societegenerale.commons.plugin.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.societegenerale.commons.plugin.Log;

/**
 * Created by agarg020917 on 11/17/2017.
 */
public class ArchUtils {

    private static Log log;

    public ArchUtils(Log log) {
        this.log=log;
    }

    public static JavaClasses importAllClassesInPackage(String path, String classFolder) {

        //not great design, but since all the rules need to call this, it's very convenient to keep this method static
        if(log==null){
            throw new IllegalStateException("please make sure you instantiate "+ArchUtils.class+" with a proper "+Log.class+" before calling this static method");
        }

        Path classesPath = Paths.get(path + classFolder);

        if (classesPath.toFile().exists()) {
            return new ClassFileImporter().importPath(classesPath);
        }
        else{
            StringBuilder warnMessage=new StringBuilder("classpath ").append(classesPath.toFile()).append("doesn't exist : loading all classes from root, ie ").append(path);
            log.warn(warnMessage.toString());
            return new ClassFileImporter().importPath(Paths.get(path));
        }

    }

}
