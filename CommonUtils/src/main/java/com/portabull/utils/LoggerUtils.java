package com.portabull.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerUtils {

    private LoggerUtils() {
    }

    static final Logger logger = LoggerFactory.getLogger(LoggerUtils.class);

    private static final String METHOD_STARTS = "() starts";

    private static final String METHOD_ENDS = "() ends";

    public static Logger getLogger() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        if (stackTrace == null || stackTrace.length == 0)
            return logger;

        if (stackTrace.length >= 2) {
            return LoggerFactory.getLogger(stackTrace[2].getClassName());
        }

        if (stackTrace.length >= 1) {
            return LoggerFactory.getLogger(stackTrace[1].getClassName());
        }

        return LoggerFactory.getLogger(stackTrace[0].getClassName());

    }

    public static String getMethodName() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        if (stackTrace == null || stackTrace.length == 0)
            return "main";

        if (stackTrace.length >= 2) {
            return stackTrace[2].getMethodName();
        }

        if (stackTrace.length >= 1) {
            return stackTrace[1].getMethodName();
        }

        return stackTrace[0].getMethodName();
    }


    public static void logError(String message, Exception e) {
        LoggerUtils.getLogger().error(message, e);
    }

    public static void logError(Logger logger, String message, Exception e) {
        logger.error(message, e);
    }

    public static void methodStartInfo() {
        String message = prepareMessage(getMethodName(), METHOD_STARTS);
        LoggerUtils.getLogger().info(message);
    }

    public static void methodEndInfo() {
        String message = prepareMessage(getMethodName(), METHOD_ENDS);
        LoggerUtils.getLogger().info(message);
    }

    public static void methodStartInfo(Logger logger) {
        String message = prepareMessage(getMethodName(), METHOD_STARTS);
        logger.info(message);
    }

    public static void methodEndInfo(Logger logger) {
        String message = prepareMessage(getMethodName(), METHOD_ENDS);
        logger.info(message);
    }

    public static void methodStartInfo(String methodName) {
        String message = prepareMessage(methodName, METHOD_STARTS);
        LoggerUtils.getLogger().info(message);
    }

    public static void methodEndInfo(String methodName) {
        String message = prepareMessage(methodName, METHOD_ENDS);
        LoggerUtils.getLogger().info(message);
    }

    public static void methodStartInfo(Logger logger, String methodName) {
        String message = prepareMessage(methodName, METHOD_STARTS);
        logger.info(message);
    }

    public static void methodEndInfo(Logger logger, String methodName) {
        String message = prepareMessage(methodName, METHOD_ENDS);
        logger.info(message);
    }

    public static String prepareMessage(String methodName, String message) {
        return new StringBuilder(methodName).append(message).toString();
    }

}
