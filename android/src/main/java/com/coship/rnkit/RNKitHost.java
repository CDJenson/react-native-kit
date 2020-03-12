package com.coship.rnkit;

import android.app.Application;

import com.facebook.react.ReactNativeHost;
import com.orhanobut.logger.Logger;
import com.coship.rnkit.update.hotupdate.HotUpdateHandler;
import com.coship.rnkit.utils.FileUtils;

import javax.annotation.Nullable;


/**
 *  author: zoujunda
 *  date: 2019/6/5 16:34
 *	version: 1.0
 *  description: to load the local bundle
 */
public abstract class RNKitHost extends ReactNativeHost {

    protected RNKitHost(Application application) {
        super(application);
    }

    @Nullable
    @Override
    protected String getJSBundleFile() {
        String localBundlePath = HotUpdateHandler.getLocalBundlePath();
        if (!RNKit.get().isFirstLaunch() && FileUtils.isFileExist(localBundlePath)) {
            Logger.i("load bundle from " + localBundlePath);
            return localBundlePath;
        } else {
            Logger.i("load bundle from assets");
            return super.getJSBundleFile();
        }
    }

}
