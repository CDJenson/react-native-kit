package com.coship.rnkit.log;

/**
 *  author: zoujunda
 *  date: 2019/6/24 17:11
 *	version: 1.0
 *  description: One-sentence description
 */
public enum LogPriority {

    VERBOSE("verbose"),

    DEBUG("debug"),

    INFO("info"),

    WARN("warn"),

    ERROR("error"),

    FATAL("fatal");//will crash the program

    private final String value;

    LogPriority(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static LogPriority getLogPriority(String value) {
        for (LogPriority logPriority : LogPriority.values()) {
            if (logPriority.getValue().equals(value)) {
                return logPriority;
            }
        }
        return LogPriority.VERBOSE;
    }
}
