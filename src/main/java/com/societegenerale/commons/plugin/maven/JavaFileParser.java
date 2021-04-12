package com.societegenerale.commons.plugin.maven;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.tngtech.archunit.thirdparty.com.google.common.annotations.VisibleForTesting;
import org.apache.maven.plugin.logging.Log;

class JavaFileParser
{
    private static final String PACKAGE             = "package";
    private static final String CLASS               = "class";
    static final         String REGEX_LINE_BREAKS   = "[\n\r]";
    private static final String BLOCK_COMMENT_START = "/**";
    private static final String BLOCK_COMMENT_END   = "*/";
    private static final String LINE_COMMENT_START  = "//";
    private static final String LINE_COMMENT_END    = "\n";

    /**
     * Parse the file and returns {@link JavaFile}
     *
     * @param javafilePath - not null
     * @param mavenLogger  - not null
     *
     * @return not null
     */
    JavaFile parse(final Path javafilePath, final Log mavenLogger) throws IOException
    {
        final String fileContent = readFile(javafilePath);
        String packageString = null;
        String className = null;

        if (fileContent.isEmpty())
        {
            mavenLogger.warn("empty file: " + javafilePath);
        }
        else
        {
            packageString = extractPackage(fileContent, mavenLogger);
            className = extractClassName(fileContent, mavenLogger);
        }

        return new JavaFile(packageString, className);
    }

    /**
     * Extracts the package out of the file content.
     *
     * @param javaFileContent - file content as string
     * @param mavenLogger     - not null
     *
     * @return null if the package could not be extracted
     */
    @VisibleForTesting
    String extractPackage(final String javaFileContent, @Nonnull final Log mavenLogger)
    {
        if (javaFileContent == null || javaFileContent.isEmpty() || !javaFileContent.contains(PACKAGE))
        {
            return null;
        }

        final String substring = javaFileContent.substring(javaFileContent.indexOf(PACKAGE));

        // split at the first ";"
        final String[] split = substring.split(";", 2);
        // there should be at least 1 part which contains the package declaration
        if (split.length < 1)
        {
            mavenLogger.warn("unexpected file content: " + javaFileContent);
            return null;
        }

        final String packageDeclaration = split[0].trim();
        if (!packageDeclaration.startsWith(PACKAGE))
        {
            mavenLogger.warn("unabble to find package declaration in: " + packageDeclaration);
            return null;
        }

        // get the part after the keyword package
        return packageDeclaration.substring(PACKAGE.length()).trim();
    }

    /**
     * Reads the java file.
     *
     * @param javafilePath - not null
     *
     * @return the file content as String if there is a content, not null
     *
     * @throws IOException when
     */
    @VisibleForTesting
    String readFile(@Nonnull final Path javafilePath) throws IOException
    {
        final StringBuilder builder = new StringBuilder();
        Files.readAllLines(javafilePath).forEach(builder::append);

        return builder.toString();
    }

    /**
     * Extracts the class name.
     *
     * @param javaFileContent - file content as String
     * @param mavenLogger     -   not null
     *
     * @return class name or null
     */
    @VisibleForTesting
    @Nullable
    String extractClassName(final String javaFileContent, @Nonnull final Log mavenLogger)
    {
        if (javaFileContent == null || javaFileContent.isEmpty() || !javaFileContent.contains(CLASS))
        {
            return null;
        }

        final String commentsRemoved = removeComments(javaFileContent);
        final String classOffsetContent = commentsRemoved.substring(commentsRemoved.indexOf(CLASS));

        // split at the first "{"
        final String[] split = classOffsetContent.split("\\{", 2);
        // there should be at least 1 part which contains the class declaration
        if (split.length < 1)
        {
            mavenLogger.warn("unexpected file content: " + javaFileContent);
            return null;
        }

        final String classDeclaration = split[0].trim();
        if (!classDeclaration.contains(CLASS))
        {
            mavenLogger.warn("unabble to find class declaration in: " + classDeclaration);
            return null;
        }

        // get the part after the keyword class
        final String substring = classDeclaration.substring(classDeclaration.lastIndexOf(CLASS) + CLASS.length());
        final String withoutLineBreaks = substring.replaceAll(REGEX_LINE_BREAKS, " ").trim();
        // remove extends or implements to get only the class name
        return withoutLineBreaks.split(" ", 2)[0];
    }

    @VisibleForTesting
    String removeComments(@Nonnull final String javaFileContent)
    {
        final String withOutBlockComments = removeAllBlocks(javaFileContent, BLOCK_COMMENT_START, BLOCK_COMMENT_END);
        return removeAllBlocks(withOutBlockComments, LINE_COMMENT_START, LINE_COMMENT_END);

    }

    /**
     * Removes all text blocks defined by blockStartString and blockEndString.
     *
     * @param javaFileContent  -   not null
     * @param blockStartString -   not null
     * @param blockEndString   -   not null
     *
     * @return string without the defined text blocks
     */
    private String removeAllBlocks(@Nonnull final String javaFileContent,
                                   @Nonnull final String blockStartString,
                                   @Nonnull final String blockEndString)
    {
        int startIndex = javaFileContent.indexOf(blockStartString);
        int endIndex = calEndIndex(blockEndString, startIndex, javaFileContent);
        String content = javaFileContent;

        while (startIndex >= 0 && endIndex > startIndex)
        {
            content = content.substring(0, startIndex) + content.substring(endIndex);

            startIndex = content.indexOf(blockStartString);
            endIndex = calEndIndex(blockEndString, startIndex, content);
        }

        return content;
    }

    private int calEndIndex(@Nonnull final String blockEndString,
                            final int startIndex,
                            @Nonnull final String content)
    {
        final int endIndex = content.indexOf(blockEndString, startIndex) + blockEndString.length();
        return endIndex > content.length() ? content.length() : endIndex;
    }

    /**
     * Class that holds relevant java file information
     */
    static class JavaFile
    {
        private final String packageString;
        private final String className;

        JavaFile(final String packageString, final String className)
        {
            this.packageString = packageString == null || packageString.isEmpty() ? null : packageString;
            this.className = className == null || className.isEmpty() ? null : className;
        }

        String getPackageString()
        {
            return packageString;
        }

        String getClassName()
        {
            return className;
        }
    }
}
