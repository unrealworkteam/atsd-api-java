package com.axibase.collector;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.LoggingEvent;

/**
 * @author Nikolay Malevanny.
 */
public class TestUtils {
    public static LoggingEvent createLoggingEvent(Level level, String loggerName, String message, String threadName) {
        LoggingEvent le = new LoggingEvent();
        le.setLevel(level);
        le.setLoggerName(loggerName);
        le.setMessage(message);
        le.setThreadName(threadName);
        le.setTimeStamp(System.currentTimeMillis());
        return le;
    }
}
