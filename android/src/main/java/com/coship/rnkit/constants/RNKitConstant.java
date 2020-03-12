package com.coship.rnkit.constants;

import android.os.Environment;

import com.coship.rnkit.RNKit;

import java.io.File;


/**
 *  author: zoujunda
 *  date: 2019/6/11 17:12
 *	version: 1.0
 *  description: One-sentence description
 */
public class RNKitConstant {

    /**********************************************************SharedPreferences key*******************************************************************/
    //the first time launch the app
    public static final String SHAR_FIRST_LAUNCH = "first_launch";

    //the folder currently in used: update1 or update2
    public static final String SHAR_CURRENT_BUNDLE_FOLDER = "current_bundle_folder";

    //bundle version
    public static final String SHAR_BUNDLE_VERSION_CODE = "bundleVersionCode";


    /**********************************************************File*******************************************************************/
    //the application's local root directory
    public static final String LOCAL_PATH = Environment.getExternalStorageDirectory().toString()
            + File.separator + RNKit.get().getContext().getPackageName();

    public static final String UPDATE_ZIP_NAME = "update.zip";

    //the filename of the patch file
    public static final String JS_PATCH_FILENAME = "update.patch";

    //the filename of the jsBundle file
    public static final String JS_BUNDLE_FILENAME = "index.android.bundle";

    //the dirctory of the downloaded files
    public static final String DOWNLOAD_PATH = LOCAL_PATH + File.separator + "downloads";

    //the directory of the downloaded apk file
    public static final String APK_FILE = DOWNLOAD_PATH + File.separator + "update.apk";

    //the directory of the downloaded zip file
    public static final String UPDATE_ZIP_FILE = DOWNLOAD_PATH + File.separator + UPDATE_ZIP_NAME;

    //folder-->update1
    public static final String UPDATE_FOLDER1 = "update1";

    //folder-->update2
    public static final String UPDATE_FOLDER2 = "update2";

    //directory-->update1
    public static final String UPDATE_PATH1 = LOCAL_PATH + File.separator +UPDATE_FOLDER1;

    //directory-->update2
    public static final String UPDATE_PATH2 = LOCAL_PATH + File.separator +UPDATE_FOLDER2;

    //directory-->bundle1
    public static final String JS_BUNDLE_FILE1 = LOCAL_PATH + File.separator + UPDATE_FOLDER1 + File.separator + JS_BUNDLE_FILENAME;

    //directory-->bundle2
    public static final String JS_BUNDLE_FILE2 = LOCAL_PATH + File.separator + UPDATE_FOLDER2 + File.separator + JS_BUNDLE_FILENAME;

    //directory-->patch1
    public static final String JS_PATCH_FILE1 = LOCAL_PATH + File.separator + UPDATE_FOLDER1 + File.separator + JS_PATCH_FILENAME;

    //directory-->patch2
    public static final String JS_PATCH_FILE2 = LOCAL_PATH + File.separator + UPDATE_FOLDER2 + File.separator + JS_PATCH_FILENAME;

}
