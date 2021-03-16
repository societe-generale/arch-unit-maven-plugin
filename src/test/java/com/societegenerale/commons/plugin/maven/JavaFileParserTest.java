package com.societegenerale.commons.plugin.maven;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.societegenerale.commons.plugin.maven.JavaFileParser.JavaFile;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith (MockitoJUnitRunner.class)
public class JavaFileParserTest extends AbstractExcludePathTest
{
    private final JavaFileParser javaFileParser = new JavaFileParser();

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
    public void testParse() throws IOException
    {
        final JavaFile fileWithPackage = javaFileParser.parse(getTempJavaFile(), getMavenLogger());
        assertEquals(AbstractExcludePathTest.CLASS_NAME, fileWithPackage.getClassName());
        assertEquals(AbstractExcludePathTest.PACKAGE_NAME, fileWithPackage.getPackageString());

        final JavaFile fileWithoutPackage = javaFileParser.parse(getTempJavaFileWithDefaultPackage(), getMavenLogger());
        assertEquals(AbstractExcludePathTest.CLASS_NAME, fileWithoutPackage.getClassName());
        assertNull(fileWithoutPackage.getPackageString());

        final JavaFile fileWithFileComment = javaFileParser.parse(
                AbstractExcludePathTest.getTempJavaFileWithFileComment(), getMavenLogger());
        assertEquals(AbstractExcludePathTest.CLASS_NAME_WITH_FILE_COMMENT, fileWithFileComment.getClassName());
        assertEquals(AbstractExcludePathTest.PACKAGE_NAME, fileWithFileComment.getPackageString());
    }

    @Test
    public void testExtractPackage_FileContentNull()
    {
        javaFileParser.extractPackage(null, getMavenLogger());
    }

    @Test
    public void testExtractPackage_FileContentEmpty()
    {
        javaFileParser.extractPackage("", getMavenLogger());
    }

    @Test
    public void testExtractPackage_DefaultPackage()
    {
        final String defaultPackage = javaFileParser.extractPackage(
                AbstractExcludePathTest.CONTENT_WITH_DEFAULT_PACKAGE, getMavenLogger());
        assertNull(defaultPackage);
    }

    @Test
    public void testExtractPackage()
    {
        final String extractPackage = javaFileParser.extractPackage(AbstractExcludePathTest.CONTENT_WITH_PACKAGE,
                                                                    getMavenLogger());
        assertEquals(AbstractExcludePathTest.PACKAGE_NAME, extractPackage);

        final String defaultPackage = javaFileParser.extractPackage(
                AbstractExcludePathTest.CONTENT_WITH_DEFAULT_PACKAGE, getMavenLogger());
        assertNull(defaultPackage);
    }

    @Test
    public void testReadFile() throws IOException
    {
        final String fileContentDefaultPackage = javaFileParser.readFile(getTempJavaFileWithDefaultPackage());
        assertEquals(removeLineBreaks(AbstractExcludePathTest.CONTENT_WITH_DEFAULT_PACKAGE), fileContentDefaultPackage);

        final String fileContent = javaFileParser.readFile(getTempJavaFile());
        assertEquals(removeLineBreaks(AbstractExcludePathTest.CONTENT_WITH_PACKAGE), fileContent);

        final String fileContentWithFileComment = javaFileParser.readFile(
                AbstractExcludePathTest.getTempJavaFileWithFileComment());
        assertEquals(removeLineBreaks(AbstractExcludePathTest.CONTENT_WITH_FILE_COMMENT_AND_PACKAGE),
                     fileContentWithFileComment);
    }

    @Test
    public void testExtractClassName()
    {
        final String className1 = javaFileParser.extractClassName(AbstractExcludePathTest.CONTENT_WITH_PACKAGE,
                                                                  getMavenLogger());
        assertEquals(AbstractExcludePathTest.CLASS_NAME, className1);

        final String className2 = javaFileParser.extractClassName(AbstractExcludePathTest.CONTENT_WITH_DEFAULT_PACKAGE,
                                                                  getMavenLogger());
        assertEquals(AbstractExcludePathTest.CLASS_NAME, className2);

        final String className3 = javaFileParser.extractClassName(
                AbstractExcludePathTest.CONTENT_WITH_FILE_COMMENT_AND_PACKAGE,
                getMavenLogger());
        assertEquals(AbstractExcludePathTest.CLASS_NAME_WITH_FILE_COMMENT, className3);
    }

    @Test
    public void testRemoveComments()
    {
        final String blockComment = "/**\n\r"
                                    + "* block comment for class foo\n\r"
                                    + " */";

        final String onelineBlockcomment = "/** one line block comment for class foo */";
        final String lineComment = "// line comment for class foo \n";

        final String expected = "bar";

        assertTrue(javaFileParser.removeComments(lineComment).isEmpty());
        assertEquals(expected, javaFileParser.removeComments(lineComment + expected));
        assertEquals(expected, javaFileParser.removeComments(lineComment + expected + lineComment));

        assertEquals(expected, javaFileParser.removeComments(onelineBlockcomment + expected));
        assertEquals(expected, javaFileParser.removeComments(onelineBlockcomment + expected + onelineBlockcomment));

        assertEquals(expected, javaFileParser.removeComments(blockComment + expected));
        assertEquals(expected, javaFileParser.removeComments(blockComment + expected + blockComment));

    }

    private String removeLineBreaks(final String s)
    {
        return s.replaceAll(JavaFileParser.REGEX_LINE_BREAKS, "");
    }
}