package com.coship.rnkit.update;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.orhanobut.logger.Logger;
import com.coship.rnkit.RNKit;
import com.coship.rnkit.constants.RNKitConstant;
import com.coship.rnkit.update.hotupdate.HotUpdateHandler;
import com.coship.rnkit.utils.FileUtils;

/**
 *  author: zoujunda
 *  date: 2019/6/5 17:117:01
 *	version: 1.0
 *  description: manage the apk update and the hot update
 */
public class UpdateManager {

    private static UpdateManager instance;
    private Context context;
    private Downloader apkDownloader;
    private Downloader patchDownloader;

    private UpdateManager(Context context) {
        this.context = context;
    }

    public static UpdateManager get() {
        if (instance == null) {
            synchronized (UpdateManager.class) {
                if (instance == null) {
                    instance = new UpdateManager(RNKit.get().getContext());
                }
            }
        }
        return instance;
    }

    /**
     * start silently downloading the js patch file
     *
     * @param downloadUrl
     */
    public void startPatchDownload(String downloadUrl) {
        HotUpdateHandler.checkEnviroment();
        patchDownloader = new Downloader.Builder(context)
                .setDownloadUrl(downloadUrl)
                .setDestPath(RNKitConstant.UPDATE_ZIP_FILE)
                .setSilent(true)
                .setDownloaderListener(new Downloader.DownloaderListener() {
                    @Override
                    void onSuccess(long downloadId) {
                        super.onSuccess(downloadId);
                        Logger.i("patch downloaded successfully:"+downloadUrl);
                        HotUpdateHandler.handleTheUpdateFile(context);
                    }
                }).create();
        patchDownloader.startDownload();
    }

    /**
     * start to download the apk file
     *
     * @param downloadUrl
     */
    public void startApkDownload(String downloadUrl) {
        if (FileUtils.isFileExist(RNKitConstant.APK_FILE)) {
            FileUtils.deleteFile(RNKitConstant.APK_FILE);
        }
        apkDownloader = new Downloader.Builder(RNKit.get().getContext())
                .setDownloadUrl(downloadUrl)
                .setDestPath(RNKitConstant.APK_FILE)
                .setSilent(false)
                .setDownloaderListener(new Downloader.DownloaderListener() {
                    @Override
                    void onSuccess(long downloadId) {
                        super.onSuccess(downloadId);
                        Logger.i("apk downloaded successfully:"+downloadUrl);
                        installApk(downloadId);
                    }

                    @Override
                    void onFailed(int errorCode, String errorMsg) {
                        super.onFailed(errorCode, errorMsg);
                    }

                    @Override
                    void onNotificationClicked(long downloadId) {
                        super.onNotificationClicked(downloadId);
                    }
                }).create();
        apkDownloader.startDownload();
    }

    /**
     * remove all the running task
     */
    public void removeAllUpdateTask() {
        Logger.i("removeAllUpdateTask--------");
        if (apkDownloader != null) {
            apkDownloader.remove();
        }
        if (patchDownloader != null) {
            patchDownloader.remove();
        }
    }

    /**
     *
     * @param downloadId
     */
    private void installApk(long downloadId) {
        try {
            DownloadManager dManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Intent intent = new Intent(Intent.ACTION_VIEW);
            Uri downloadFileUri = dManager.getUriForDownloadedFile(downloadId);
            if (downloadFileUri != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
                context.startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
