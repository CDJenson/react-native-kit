package com.coship.rnkit;

import android.content.pm.PackageInfo;
import android.os.Build;
import android.util.Log;

import com.coship.rnkit.constants.RNKitConstant;
import com.coship.rnkit.log.LogManager;
import com.coship.rnkit.utils.SharedPrefManager;
import com.coship.rnkit.utils.Utils;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


/**
 *  author: zoujunda
 *  date: 2019/6/10 15:51
 *	version: 1.0
 *  description: One-sentence description
 */
public class RNKitModule extends ReactContextBaseJavaModule {

    private static final String MODULE_NAME = "RNKit";

    public RNKitModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
        RNKit.get().registLifecycleEventListener(reactContext);
    }

    @Nonnull
    @Override
    public String getName() {
        return MODULE_NAME;
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        HashMap<String, Object> constants = new HashMap<String, Object>();
        SharedPrefManager sharedPrefManager = SharedPrefManager.getInstance(RNKit.get().getContext());
        PackageInfo info = RNKit.get().getPackageInfo();
        constants.put("packageName",info.packageName);
        constants.put("appVersionName", info.versionName);
        constants.put("appVersionCode", info.versionCode);
        constants.put("bundleVersionCode",sharedPrefManager.get(RNKitConstant.SHAR_BUNDLE_VERSION_CODE,info.versionCode));
        constants.put("serialNumber", Build.SERIAL);
        constants.put("systemName", "Android");
        constants.put("systemVersion", Build.VERSION.RELEASE);
        constants.put("model", Build.MODEL);
        constants.put("brand", Build.BRAND);
        constants.put("deviceId", Build.BOARD);
        constants.put("apiLevel", Build.VERSION.SDK_INT);
        return constants;
    }

    @ReactMethod
    public void startPatchDownload(int bundleVersionCode,String downloadUrl,int installMode){
        RNKit.get().startPatchDownload(bundleVersionCode,downloadUrl,installMode);
    }

    @ReactMethod
    public void startApkDownload(int appVersionCode,String downloadUrl){
        RNKit.get().startApkDownload(appVersionCode,downloadUrl);
    }

    @ReactMethod
    public void log(String priority ,String message){
        LogManager.log(priority,message);
    }

    @ReactMethod
    public void setHandlerforNativeException(Callback callback){
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread thread, final Throwable throwable) {
                String stackTraceString = Log.getStackTraceString(throwable);
                callback.invoke(stackTraceString);
                Utils.safeToast(getReactApplicationContext(),R.string.toast_of_crash);

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
    }

}
