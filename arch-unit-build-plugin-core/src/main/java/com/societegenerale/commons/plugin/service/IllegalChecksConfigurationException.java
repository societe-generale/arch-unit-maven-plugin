package com.societegenerale.commons.plugin.service;

import java.util.Set;

class IllegalChecksConfigurationException extends RuntimeException {
    IllegalChecksConfigurationException(Class<?> rulesLocation, Set<String> illegalChecks) {
        super(String.format("The following configured checks are not present within %s: %s", rulesLocation.getName(), illegalChecks));
    }
}
