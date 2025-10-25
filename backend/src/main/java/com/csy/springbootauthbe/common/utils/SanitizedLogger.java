package com.csy.springbootauthbe.common.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class SanitizedLogger {

    private final Logger logger;

    private SanitizedLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }

    public static SanitizedLogger getLogger(Class<?> clazz) {
        return new SanitizedLogger(clazz);
    }

    // Core sanitizer
    private String sanitize(String message) {
        if (message == null) return null;

        // Mask common sensitive fields
        message = message.replaceAll("(?i)password\\s*[:=]\\s*[^\\s,]+", "password=***");
        message = message.replaceAll("(?i)token\\s*[:=]\\s*[^\\s,]+", "token=***");
        message = message.replaceAll("(?i)authorization\\s*[:=]\\s*[^\\s,]+", "authorization=***");
        message = message.replaceAll("[\\r\\n\\t\\f\\u000B\\u0000-\\u001F\\u007F]+", " ");

        return message;
    }

    private Object[] sanitizeArgs(Object... args) {
        if (args == null) {
            return new Object[] {};
        }
        return Arrays.stream(args)
                .map(arg -> arg == null ? null : sanitize(arg.toString()))
                .toArray();
    }

    // ---- Safe logging methods ----
    public void info(String msg, Object... args) {
        if (logger.isInfoEnabled()) {
            logger.info(sanitize(msg), sanitizeArgs(args));
        }
    }

    public void debug(String msg, Object... args) {
        if(logger.isDebugEnabled()){
            logger.debug(sanitize(msg), sanitizeArgs(args));
        }
    }

    public void warn(String msg, Object... args) {
        if (logger.isWarnEnabled()) {
            logger.warn(sanitize(msg), sanitizeArgs(args));
        }
    }

    public void error(String msg, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(sanitize(msg), sanitizeArgs(args));
        }
    }

    public void error(String msg, Throwable t, Object... args) {
        if (logger.isErrorEnabled()) {
            logger.error(sanitize(msg), sanitizeArgs(args), t);
        }
    }
}

