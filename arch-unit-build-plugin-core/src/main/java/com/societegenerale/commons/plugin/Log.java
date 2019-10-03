package com.societegenerale.commons.plugin;

public interface Log {
    
    boolean isInfoEnabled();

    void info(String s);

    void warn(String toString);
}
