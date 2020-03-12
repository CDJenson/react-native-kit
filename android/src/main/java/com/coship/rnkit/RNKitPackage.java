package com.coship.rnkit;

import android.content.Context;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;

/**
 *  author: zoujunda
 *  date: 2019/6/10 15:55
 *	version: 1.0
 *  description: One-sentence description
 */
public class RNKitPackage implements ReactPackage {

    public RNKitPackage(Context context,boolean isDebugMode){
        RNKit.get().init(context,new RNKit.RNKitConfig(isDebugMode));
    }

    @Nonnull
    @Override
    public List<NativeModule> createNativeModules(@Nonnull ReactApplicationContext reactContext) {
        List<NativeModule> modules = new ArrayList<>();
        modules.add(new RNKitModule(reactContext));
        return modules;
    }

    @Nonnull
    @Override
    public List<ViewManager> createViewManagers(@Nonnull ReactApplicationContext reactContext) {
        return Collections.emptyList();
    }

}
