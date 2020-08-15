package com.societegenerale.commons.plugin.maven;

import com.societegenerale.commons.plugin.Log;

public class MavenLogAdapter implements Log {

    private org.apache.maven.plugin.logging.Log mavenLogger;

    public MavenLogAdapter(org.apache.maven.plugin.logging.Log mavenLogger) {
        this.mavenLogger = mavenLogger;
    }

    @Override
    public boolean isInfoEnabled() {
        return mavenLogger.isInfoEnabled();
    }

    @Override
    public boolean isDebugEnabled() {
        return mavenLogger.isDebugEnabled();
    }

    @Override
    public void info(String msg) {
        mavenLogger.info(msg);
    }

    @Override
    public void debug(String s) {

    }

    @Override
    public void warn(String msg) {
        mavenLogger.warn(msg);
    }
}
