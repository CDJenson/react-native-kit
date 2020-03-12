package com.coship.rnkit;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import com.coship.rnkit.constants.RNKitConstant;
import com.coship.rnkit.log.LogManager;
import com.coship.rnkit.update.UpdateManager;
import com.coship.rnkit.update.hotupdate.HotUpdateHandler;
import com.coship.rnkit.update.hotupdate.HotUpdateInstallMode;
import com.coship.rnkit.utils.SharedPrefManager;
import com.facebook.react.ReactApplication;
import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.JSBundleLoader;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.orhanobut.logger.Logger;

import java.lang.reflect.Field;

/**
 *  author: zoujunda
 *  date: 2019/6/19 16:26
 *	version: 1.0
 *  description: One-sentence description
 */
public class RNKit {

    private static RNKit instance;
    private Context context;
    private RNKitConfig rnKitConfig;
    private HotUpdateInstallMode hotUpdateInstallMode;
    private int bundleVersionCode;

    private RNKit() {}

    public static RNKit get() {
        if (instance == null) {
            synchronized (RNKit.class) {
                if (instance == null) {
                    instance = new RNKit();
                }
            }
        }
        return instance;
    }

    public void init(Context context, RNKitConfig rnKitConfig) {
        this.context = context;
        this.rnKitConfig = rnKitConfig;
        LogManager.init(rnKitConfig.isDebugMode);
    }

    public void registLifecycleEventListener(ReactApplicationContext reactContext){
        reactContext.addLifecycleEventListener(lifecycleEventListener);
    }

    public Context getContext() {
        return context;
    }

    public PackageInfo getPackageInfo(){
        PackageInfo packageInfo = null;
        PackageManager packageManager = this.context.getPackageManager();
        String packageName = this.context.getPackageName();
        try {
             packageInfo = packageManager.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }

    public RNKitConfig getRnKitConfig() {
        return this.rnKitConfig;
    }

    public boolean isFirstLaunch() {
        return SharedPrefManager.getInstance(context).get(RNKitConstant.SHAR_FIRST_LAUNCH, true);
    }

    public  void setFirstLaunch(boolean bool) {
        SharedPrefManager.getInstance(RNKit.get().getContext()).put(RNKitConstant.SHAR_FIRST_LAUNCH, bool);
    }

    public void startPatchDownload(int bundleVersionCode,String downloadUrl,int installType){
        Logger.i("startPatchDownload-->bundleVersionCode:"+bundleVersionCode+"  downloadUrl:"+downloadUrl + " installType:"+installType);
        this.bundleVersionCode = bundleVersionCode;
        this.hotUpdateInstallMode = HotUpdateInstallMode.getHotUpdateInstallMode(installType);
        UpdateManager.get().startPatchDownload(downloadUrl);
    }

    public void startApkDownload(int appVersionCode,String downloadUrl){
        Logger.i("startApkDownload-->appVersionCode:"+appVersionCode+"  downloadUrl:"+downloadUrl);
        UpdateManager.get().startApkDownload(downloadUrl);
    }

    public void installPatch(){
        Logger.i("installPatch--------");
        if(this.hotUpdateInstallMode.equals(HotUpdateInstallMode.IMMEDIATE)){
            loadBundle();
        }
        SharedPrefManager.getInstance(this.context).put(RNKitConstant.SHAR_BUNDLE_VERSION_CODE,bundleVersionCode);
    }

    public void loadBundle(){
        try {
            final ReactInstanceManager instanceManager = resolveInstanceManager();
            if(instanceManager == null){
                return;
            }
            setJSBundle(instanceManager, HotUpdateHandler.getLocalBundlePath());

           new Handler(Looper.getMainLooper()).post(new Runnable() {
               @Override
               public void run() {
                    instanceManager.recreateReactContextInBackground();
               }
           });
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Use reflection to find and set the appropriate fields on ReactInstanceManager.
     *
     * @param instanceManager
     * @param latestJSBundleFile
     * @throws IllegalAccessException
     */
    private void setJSBundle(ReactInstanceManager instanceManager, String latestJSBundleFile) throws IllegalAccessException {
        try {
            JSBundleLoader latestJSBundleLoader;
            if (latestJSBundleFile.toLowerCase().startsWith("assets://")) {
                latestJSBundleLoader = JSBundleLoader.createAssetLoader(context, latestJSBundleFile, false);
            } else {
                latestJSBundleLoader = JSBundleLoader.createFileLoader(latestJSBundleFile);
            }

            Field bundleLoaderField = instanceManager.getClass().getDeclaredField("mBundleLoader");
            bundleLoaderField.setAccessible(true);
            bundleLoaderField.set(instanceManager, latestJSBundleLoader);
        } catch (Exception e) {
            Logger.e("Unable to set JSBundle - RNKit may not support this version of React Native");
            throw new IllegalAccessException("Could not setJSBundle");
        }
    }

    private ReactInstanceManager resolveInstanceManager() throws NoSuchFieldException, IllegalAccessException {
        if (this.context == null) {
            return null;
        }
        ReactApplication reactApplication = (ReactApplication) this.context;
        ReactInstanceManager instanceManager = reactApplication.getReactNativeHost().getReactInstanceManager();
        return instanceManager;
    }

    private LifecycleEventListener lifecycleEventListener = new LifecycleEventListener() {

        @Override
        public void onHostResume() {
            Logger.i("onHostResume.............");
        }

        @Override
        public void onHostPause() {
            Logger.i("onHostPause.............");
        }

        @Override
        public void onHostDestroy() {
            //After the application exits, we need to remove all the tasks in progress
            UpdateManager.get().removeAllUpdateTask();
        }
    };

    public static class RNKitConfig {
        boolean isDebugMode;

        public RNKitConfig(boolean isDebugMode) {
            this.isDebugMode = isDebugMode;
        }
    }

}
