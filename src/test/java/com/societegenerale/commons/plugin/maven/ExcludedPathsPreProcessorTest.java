package com.societegenerale.commons.plugin.maven;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.mockito.junit.MockitoJUnitRunner;

import static com.societegenerale.commons.plugin.maven.ExcludedPathsPreProcessor.PACKAGE_INFO_JAVA;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith (MockitoJUnitRunner.class)
public class ExcludedPathsPreProcessorTest extends AbstractExcludePathTest
{
    private final ExcludedPathsPreProcessor preProcessor = new ExcludedPathsPreProcessor();

    @BeforeClass
    public static void init() throws IOException
    {
        AbstractExcludePathTest.init();
    }

    @AfterClass
    public static void cleanup() throws IOException
    {
        AbstractExcludePathTest.cleanup();
    }

    @Test
    public void testFindJavaFiles_EmptyDir() throws IOException
    {
        final Path emptyDir =
                Files.createDirectory(Paths.get(getTestTempRootDirectory().toString() + "/EmptyDirectory"));

        final Set<Path> actual = preProcessor.findJavaFiles(emptyDir, getMavenLogger());

        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testFindJavaFiles() throws IOException
    {
        final Path notAJavaFilePath =
                Files.createFile(Paths.get(getTestTempRootDirectory().toString() + "/NotAJavaFile.txt"));

        final Path packageInfoFilePath =
                Files.createFile(Paths.get(
                        getTestTempRootDirectory().toString() + "/" + PACKAGE_INFO_JAVA));

        final Set<Path> expected = new HashSet<>();
        expected.add(getTempJavaFile());
        expected.add(getTempJavaFileWithDefaultPackage());
        expected.add(AbstractExcludePathTest.getTempJavaFileWithFileComment());

        final Set<Path> actual = preProcessor.findJavaFiles(getTestTempRootDirectory(), getMavenLogger());

        assertNotNull(actual);
        assertFalse(actual.contains(notAJavaFilePath));
        assertFalse(actual.contains(packageInfoFilePath));
        assertEquals(expected.size(), actual.size());
        assertEquals(expected, actual);
    }

    @Test
    public void testDetermineClassNames_EmptySet()
    {
        final Set<String> actual = preProcessor.determineClassNames(new HashSet<>(), getMavenLogger());
        assertNotNull(actual);
        assertTrue(actual.isEmpty());
    }

    @Test
    public void testDetermineClassNames()
    {
        final Set<String> expected = new HashSet<>();
        expected.add(AbstractExcludePathTest.CLASS_NAME);
        expected.add(AbstractExcludePathTest.PACKAGE_NAME + "." + AbstractExcludePathTest.CLASS_NAME);
        expected.add(AbstractExcludePathTest.PACKAGE_NAME + "." + AbstractExcludePathTest.CLASS_NAME_WITH_FILE_COMMENT);

        final Set<Path> paths = new HashSet<>();
        paths.add(getTempJavaFile());
        paths.add(getTempJavaFileWithDefaultPackage());
        paths.add(AbstractExcludePathTest.getTempJavaFileWithFileComment());

        final Set<String> actual = preProcessor.determineClassNames(paths, getMavenLogger());
        assertNotNull(actual);
        assertEquals(expected, actual);
    }

    @Test
    public void testConvertToPath() throws IOException
    {
        final Path targetDir = AbstractExcludePathTest.getTestProjectBuildDirectory();

        final Path generatedSourcesDir = Files.createDirectory(
                Paths.get(targetDir.toString(), ExcludedPathsPreProcessor.GENERATTED_SOURCES));

        final Path actualGenSrc = preProcessor.convertToPath(getMavenLogger(), targetDir.toString(),
                                                             ExcludedPathsPreProcessor.GENERATTED_SOURCES);
        assertEquals(generatedSourcesDir, actualGenSrc);

        // Directory outsite of the project
        final Path dirOutsiteTarget =
                Files.createDirectory(Paths.get(getTestTempRootDirectory().toString(), "outsideDir"));

        final Path actualOutsiteDir = preProcessor.convertToPath(getMavenLogger(), targetDir.toString(),
                                                                 dirOutsiteTarget.toString());
        assertEquals(dirOutsiteTarget, actualOutsiteDir);

        // path that point a to a file
        final Path javaFile = AbstractExcludePathTest.getTempJavaFileWithFileComment();
        final Path actualJavaFile = preProcessor.convertToPath(getMavenLogger(), targetDir.toString(),
                                                               javaFile.toString());
        assertEquals(javaFile, actualJavaFile);
    }

    @Test
    public void testProcessExcludedPaths()
    {
        assertNotNull(preProcessor.processExcludedPaths(getMavenLogger(), null, null));

        final Path targetDir = AbstractExcludePathTest.getTestProjectBuildDirectory();

        //exclude all java files under directory com (...tempDir../target/com/...)
        final Set<String> excludes = preProcessor.processExcludedPaths(getMavenLogger(), targetDir.toString(),
                                                                       Collections.singletonList("com"));

        assertNotNull(excludes);
        assertEquals(3, excludes.size());
        assertTrue(excludes.contains("com"));
        assertTrue(excludes.contains(AbstractExcludePathTest.PACKAGE_NAME + "." + AbstractExcludePathTest.CLASS_NAME));
        assertTrue(excludes.contains(
                AbstractExcludePathTest.PACKAGE_NAME + "." + AbstractExcludePathTest.CLASS_NAME_WITH_FILE_COMMENT));
    }

    @Test
    public void testIsJavaFile()
    {
        assertFalse(preProcessor.isJavaFile(null));
        assertFalse(preProcessor.isJavaFile(""));
        assertFalse(preProcessor.isJavaFile("abc"));

        assertFalse(preProcessor.isJavaFile("c:\\" + PACKAGE_INFO_JAVA.toUpperCase()));
        assertFalse(preProcessor.isJavaFile("c:\\" + PACKAGE_INFO_JAVA.toLowerCase()));
        assertFalse(preProcessor.isJavaFile("c:\\" + PACKAGE_INFO_JAVA + "   "));
        assertFalse(preProcessor.isJavaFile("c:\\" + PACKAGE_INFO_JAVA));

        final String pathString = "c:\\MyJavaFile.java";
        assertTrue(preProcessor.isJavaFile(pathString.toUpperCase()));
        assertTrue(preProcessor.isJavaFile(pathString.toLowerCase()));
        assertTrue(preProcessor.isJavaFile(pathString + "   "));
        assertTrue(preProcessor.isJavaFile(pathString));

    }
}