package com.societegenerale.commons.plugin.utils;

import java.lang.reflect.InvocationTargetException;

class ReflectionException extends RuntimeException {
    private ReflectionException(Throwable throwable) {
        super(throwable.getMessage(), throwable);
    }

    static ReflectionException wrap(Exception throwable) {
        return throwable instanceof InvocationTargetException
                ? new ReflectionException(((InvocationTargetException) throwable).getTargetException())
                : new ReflectionException(throwable);
    }
}
