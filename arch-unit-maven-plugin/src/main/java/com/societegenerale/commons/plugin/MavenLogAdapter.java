package com.societegenerale.commons.plugin;

import com.societegenerale.commons.plugin.Log;

public class MavenLogAdapter implements Log {

    private org.apache.maven.plugin.logging.Log mavenLogger;

    public MavenLogAdapter(org.apache.maven.plugin.logging.Log mavenLogger ) {
        this.mavenLogger=mavenLogger;
    }

    @Override
    public boolean isInfoEnabled() {
        return mavenLogger.isInfoEnabled();
    }

    @Override
    public void info(String msg) {
        mavenLogger.info(msg);
    }

    @Override
    public void warn(String msg) {
        mavenLogger.warn(msg);
    }
}
