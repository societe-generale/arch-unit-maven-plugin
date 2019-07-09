package com.societegenerale.commons.plugin.utils;

import java.nio.file.Path;
import java.nio.file.Paths;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;

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

	public static JavaClasses importAllClassesInPackage(String path) {
		Path classesPath = Paths.get(path);
		if (classesPath.toFile().exists()) {
			return new ClassFileImporter().importPath(classesPath);
		}
		return new ClassFileImporter().importPath(Paths.get(path));
	}

}