package com.coship.rnkit.update;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.orhanobut.logger.Logger;
import com.coship.rnkit.RNKit;


/**
 * author: zoujunda
 * date: 2019/6/29 10:01
 * version: 1.0
 * description: One-sentence description
 */
public class Downloader {

    private Builder builder;
    private DownloadManager downloadManager;
    private long downloadId;
    private DownloadCompleteReceiver downloadCompleteReceiver;

    private Downloader(Builder builder) {
        this.builder = builder;
    }

    public void startDownload() {
        if (builder.context == null) {
            throw new UpdateException("context is null , please initialize first!");
        }
        if (TextUtils.isEmpty(builder.downloadUrl)) {
            throw new UpdateException("invalid downloadUrl");
        }
        if (TextUtils.isEmpty(builder.destPath)) {
            throw new UpdateException("invalid destPath");
        }

        this.registDownloadReceiver();

        downloadManager = (DownloadManager) builder.context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(builder.downloadUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
        request.setDestinationUri(Uri.parse("file://" + builder.destPath));
        request.allowScanningByMediaScanner();
        if (builder.silent) {
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        } else {
            ApplicationInfo applicationInfo = RNKit.get().getPackageInfo().applicationInfo;
            PackageManager packageManager = builder.context.getPackageManager();
            request.setTitle(applicationInfo.loadLabel(packageManager).toString());
            request.setVisibleInDownloadsUi(true);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        }
        downloadId = downloadManager.enqueue(request);

    }

    /**
     * Cancel downloads and remove them from the download manager.  Each download will be stopped if
     * it was running, and it will no longer be accessible through the download manager.
     * If there is a downloaded file, partial or complete, it is deleted.
     *
     * @return
     */
    public int remove() {
        try {
            unregisterDownloadReceiver();
            return downloadManager.remove(downloadId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void registDownloadReceiver() {
        if (builder.context != null && downloadCompleteReceiver == null) {
            this.downloadCompleteReceiver = new DownloadCompleteReceiver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
            intentFilter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
            builder.context.registerReceiver(downloadCompleteReceiver, intentFilter);
        }
    }

    private void unregisterDownloadReceiver() {
        if (downloadCompleteReceiver != null) {
            builder.context.unregisterReceiver(downloadCompleteReceiver);
            downloadCompleteReceiver = null;
        }
    }

    /**
     * This class is intended to receive the broadcast intent action sent by the download manager when a download completes(ACTION_DOWNLOAD_COMPLETE)
     * or the user clicks on a running  download(ACTION_NOTIFICATION_CLICKED)
     */
    private class DownloadCompleteReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (builder == null || builder.downloaderListener == null) {
                return;
            }

            String action = intent.getAction();
            if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                long completeId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (completeId == downloadId) {
                    if (downloadManager == null) {
                        downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
                    }

                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(completeId);

                    Cursor cursor = downloadManager.query(query);
                    if (cursor != null && cursor.moveToFirst()) {
                        int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        int status = cursor.getInt(columnIndex);
                        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
                        int reason = cursor.getInt(columnReason);

                        switch (status) {
                            case DownloadManager.STATUS_FAILED:
                                Logger.e("download fialed : errorCode=" + reason);
                                builder.downloaderListener.onFailed(reason, "");
                                break;
                            case DownloadManager.STATUS_PAUSED:
                                builder.downloaderListener.onPaused();
                                break;
                            case DownloadManager.STATUS_PENDING:
                                builder.downloaderListener.onPending();
                                break;
                            case DownloadManager.STATUS_RUNNING:
                                builder.downloaderListener.onRuning();
                                break;
                            case DownloadManager.STATUS_SUCCESSFUL:
                                builder.downloaderListener.onSuccess(downloadId);
                                break;
                            default:
                                break;
                        }
                    }
                }
            } else if (action.equals(DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
                builder.downloaderListener.onNotificationClicked(downloadId);
            }
        }
    }

    static abstract class DownloaderListener {

        void onFailed(int errorCode, String errorMsg) {
        }

        void onPaused() {
        }

        void onPending() {
        }

        void onRuning() {
        }

        void onSuccess(long downloadId) {
        }

        void onNotificationClicked(long downloadId) {
        }
    }

    static class Builder {

        private Context context;
        private boolean silent;
        private String downloadUrl;
        private String destPath;
        private DownloaderListener downloaderListener;

        Builder(Context context) {
            this.context = context;
        }

        Builder setSilent(boolean silent) {
            this.silent = silent;
            return this;
        }

        Builder setDownloadUrl(String downloadUrl) {
            this.downloadUrl = downloadUrl;
            return this;
        }

        Builder setDestPath(String destPath) {
            this.destPath = destPath;
            return this;
        }

        Builder setDownloaderListener(DownloaderListener downloaderListener) {
            this.downloaderListener = downloaderListener;
            return this;
        }

        Downloader create() {
            return new Downloader(this);
        }
    }
}
