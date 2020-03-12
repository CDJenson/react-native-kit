package com.coship.rnkit.log;

import android.os.Handler;
import android.os.HandlerThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.coship.rnkit.constants.RNKitConstant;
import com.coship.rnkit.utils.Utils;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.orhanobut.logger.Logger.ASSERT;
import static com.orhanobut.logger.Logger.DEBUG;
import static com.orhanobut.logger.Logger.ERROR;
import static com.orhanobut.logger.Logger.INFO;
import static com.orhanobut.logger.Logger.VERBOSE;
import static com.orhanobut.logger.Logger.WARN;

/**
 *  author: zoujunda 
 *  date: 2019/7/3 16:29
 *	version: 1.0 
 *  description: One-sentence description
 */
public class CsvFormatStrategy implements FormatStrategy {

    private static final String NEW_LINE = System.getProperty("line.separator");
    private static final String NEW_LINE_REPLACEMENT = " <br> ";
    private static final String SEPARATOR = ",";

    @NonNull
    private final Date date;
    @NonNull
    private final SimpleDateFormat dateFormat;
    @NonNull
    private final LogStrategy logStrategy;
    @Nullable
    private final String tag;

    private CsvFormatStrategy(@NonNull CsvFormatStrategy.Builder builder) {
        Utils.checkNotNull(builder);

        date = builder.date;
        dateFormat = builder.dateFormat;
        logStrategy = builder.logStrategy;
        tag = builder.tag;
    }

    @NonNull
    public static CsvFormatStrategy.Builder newBuilder() {
        return new CsvFormatStrategy.Builder();
    }

    @Override
    public void log(int priority, @Nullable String onceOnlyTag, @NonNull String message) {
        Utils.checkNotNull(message);

        String tag = formatTag(onceOnlyTag);

        date.setTime(System.currentTimeMillis());

        StringBuilder builder = new StringBuilder();

        builder.append(dateFormat.format(date));

        // level
        builder.append(SEPARATOR);
        builder.append(logLevel(priority));

        // tag
        builder.append(SEPARATOR);
        builder.append(tag);

        // message
        if (message.contains(NEW_LINE)) {
            // a new line would break the CSV format, so we replace it here
            message = message.replaceAll(NEW_LINE, NEW_LINE_REPLACEMENT);
        }
        builder.append(SEPARATOR);
        builder.append(message);

        // new line
        builder.append(NEW_LINE);

        logStrategy.log(priority, tag, builder.toString());
    }

    @Nullable
    private String formatTag(@Nullable String tag) {
        if (!Utils.isEmpty(tag) && !Utils.equals(this.tag, tag)) {
            return this.tag + "-" + tag;
        }
        return this.tag;
    }

    public static final class Builder {
        private static final int MAX_BYTES = 500 * 1024; // 500K averages to a 4000 lines per file

        Date date;
        SimpleDateFormat dateFormat;
        LogStrategy logStrategy;
        String tag = "PRETTY_LOGGER";

        private Builder() {
        }

        @NonNull
        public CsvFormatStrategy.Builder date(@Nullable Date val) {
            date = val;
            return this;
        }

        @NonNull
        public CsvFormatStrategy.Builder dateFormat(@Nullable SimpleDateFormat val) {
            dateFormat = val;
            return this;
        }

        @NonNull
        public CsvFormatStrategy.Builder logStrategy(@Nullable LogStrategy val) {
            logStrategy = val;
            return this;
        }

        @NonNull
        public CsvFormatStrategy.Builder tag(@Nullable String tag) {
            this.tag = tag;
            return this;
        }

        @NonNull
        public CsvFormatStrategy build() {
            if (date == null) {
                date = new Date();
            }
            if (dateFormat == null) {
                dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS", Locale.CHINA);
            }
            if (logStrategy == null) {
                String folder = RNKitConstant.LOCAL_PATH + File.separatorChar + "log";

                HandlerThread ht = new HandlerThread("AndroidFileLogger." + folder);
                ht.start();
                Handler handler = new DiskLogStrategy.WriteHandler(ht.getLooper(), folder, MAX_BYTES);
                logStrategy = new DiskLogStrategy(handler);
            }
            return new CsvFormatStrategy(this);
        }
    }

    private String logLevel(int value) {
        switch (value) {
            case VERBOSE:
                return "VERBOSE";
            case DEBUG:
                return "DEBUG";
            case INFO:
                return "INFO";
            case WARN:
                return "WARN";
            case ERROR:
                return "ERROR";
            case ASSERT:
                return "ASSERT";
            default:
                return "UNKNOWN";
        }
    }


}