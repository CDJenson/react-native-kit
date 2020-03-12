package com.coship.rnkit.log;

import android.support.annotation.Nullable;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.DiskLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

/**
 *  author: zoujunda
 *  date: 2019/6/24 10:11
 *	version: 1.0
 *  description: One-sentence description
 */
public class LogManager {

    /**
     *  The logger is only valid when debug mode is true
     *
     * @param debugMode
     */
    public static void init(boolean debugMode) {
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)
                .tag("RNKit")
                .build();

        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy){
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return debugMode;
            }
        });

        //save the fatal error to the local file system
        Logger.addLogAdapter(new DiskLogAdapter(CsvFormatStrategy.newBuilder().build()){
            @Override
            public boolean isLoggable(int priority, @Nullable String tag) {
                return tag != null && tag.equals(LogPriority.FATAL.name());
            }
        });
    }

    /**
     * the method is used by js
     *
     * @param priority
     * @param message
     */
    public static void log(String priority, String message) {
        try {
            LogPriority logPriority = LogPriority.getLogPriority(priority);
            switch (logPriority) {
                case VERBOSE:
                    Logger.v(message);
                    break;
                case DEBUG:
                    Logger.d(message);
                    break;
                case INFO:
                    Logger.i(message);
                    break;
                case WARN:
                    Logger.w(message);
                    break;
                case ERROR:
                    Logger.e(message);
                    break;
                case FATAL:
                    Logger.log(Logger.ERROR,logPriority.name(),message,null);
                    break;
                default:
                    log(LogPriority.VERBOSE.getValue(), message);
                    break;
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

}
