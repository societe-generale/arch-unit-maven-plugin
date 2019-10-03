package com.societegenerale.commons.plugin;

public class SilentLog implements Log {

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public void info(String s) {

    }

    @Override
    public void warn(String toString) {

    }
}
