package com.coship.rnkit.log;

import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.coship.rnkit.RNKit;
import com.coship.rnkit.utils.Utils;
import com.orhanobut.logger.LogStrategy;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 *  author: zoujunda
 *  date: 2019/7/3 16:27
 *	version: 1.0
 *  description: One-sentence description
 */
public class DiskLogStrategy implements LogStrategy {

    @NonNull
    private final Handler handler;

    public DiskLogStrategy(@NonNull Handler handler) {
        this.handler = Utils.checkNotNull(handler);
    }

    @Override public void log(int level, @Nullable String tag, @NonNull String message) {
        Utils.checkNotNull(message);

        // do nothing on the calling thread, simply pass the tag/msg to the background thread
        handler.sendMessage(handler.obtainMessage(level, message));
    }

    static class WriteHandler extends Handler {

        @NonNull private final String folder;
        private final int maxFileSize;

        WriteHandler(@NonNull Looper looper, @NonNull String folder, int maxFileSize) {
            super(Utils.checkNotNull(looper));
            this.folder = Utils.checkNotNull(folder);
            this.maxFileSize = maxFileSize;
        }

        @SuppressWarnings("checkstyle:emptyblock")
        @Override public void handleMessage(@NonNull Message msg) {
            String content = (String) msg.obj;

            FileWriter fileWriter = null;
            File logFile = getLogFile(folder, "logs");

            try {
                fileWriter = new FileWriter(logFile, true);
                if(logFile.length() == 0){
                    writeLog(fileWriter,getLogHeader());
                }

                writeLog(fileWriter, content);

                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                if (fileWriter != null) {
                    try {
                        fileWriter.flush();
                        fileWriter.close();
                    } catch (IOException e1) { /* fail silently */ }
                }
            }
        }

        /**
         * This is always called on a single background thread.
         * Implementing classes must ONLY write to the fileWriter and nothing more.
         * The abstract class takes care of everything else including close the stream and catching IOException
         *
         * @param fileWriter an instance of FileWriter already initialised to the correct file
         */
        private void writeLog(@NonNull FileWriter fileWriter, @NonNull String content) throws IOException {
            Utils.checkNotNull(fileWriter);
            Utils.checkNotNull(content);

            fileWriter.append(content);
        }

        private File getLogFile(@NonNull String folderName, @NonNull String fileName) {
            Utils.checkNotNull(folderName);
            Utils.checkNotNull(fileName);

            File folder = new File(folderName);
            if (!folder.exists()) {
                //TODO: What if folder is not created, what happens then?
                folder.mkdirs();
            }

            int newFileCount = 0;
            File newFile;
            File existingFile = null;

            newFile = new File(folder, String.format("%s_%s.csv", fileName, newFileCount));
            while (newFile.exists()) {
                existingFile = newFile;
                newFileCount++;
                newFile = new File(folder, String.format("%s_%s.csv", fileName, newFileCount));
            }

            if (existingFile != null) {
                if (existingFile.length() >= maxFileSize) {
                    return newFile;
                }
                return existingFile;
            }

            return newFile;
        }

        private String getLogHeader(){
            String header = "\n**************************  Log Head **************************" +
                    "\nDevice Manufacturer: " + Build.MANUFACTURER +
                    "\nDevice Model       : " + Build.MODEL +
                    "\nAndroid Version    : " + Build.VERSION.RELEASE +
                    "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
                    "\nApp VersionName    : " + RNKit.get().getPackageInfo().versionName +
                    "\nApp VersionCode    : " + RNKit.get().getPackageInfo().versionCode +
                    "\n**************************  Log Head **************************\n\n";

           return header;
        }
    }
    
}