package com.coship.rnkit.update.hotupdate;

import android.content.Context;

import com.coship.rnkit.utils.Utils;
import com.orhanobut.logger.Logger;
import com.coship.rnkit.RNKit;
import com.coship.rnkit.constants.RNKitConstant;
import com.coship.rnkit.utils.FileUtils;
import com.coship.rnkit.utils.IOUtils;
import com.coship.rnkit.utils.SharedPrefManager;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.LinkedList;

/**
 *  author: zoujunda 
 *  date: 2019/6/12 14:216:52 
 *	version: 1.0 
 *  description: One-sentence description
 */
public class HotUpdateHandler {

    /**
     *
     * @return the folder currently in use
     */
    private static String getCurrentBundleFolder() {
        return SharedPrefManager.getInstance(RNKit.get().getContext()).get(RNKitConstant.SHAR_CURRENT_BUNDLE_FOLDER, "");
    }

    /**
     *
     * @return whether the folder 'update1' is being used
     */
    private static boolean isUpdate1InUse() {
        return getCurrentBundleFolder().equals(RNKitConstant.UPDATE_FOLDER1);
    }


    /**
     * Local patch files are not automatically cleared when the app is uninstalled, so when the app first starts, we should clear the remaining patch files
     */
    public static void checkEnviroment(){
        if(RNKit.get().isFirstLaunch()){
            FileUtils.deleteDir(RNKitConstant.UPDATE_PATH1);
            FileUtils.deleteDir(RNKitConstant.UPDATE_PATH2);
            RNKit.get().setFirstLaunch(false);
        }
    }

    /**
     *
     * @return the path of the bundle file currently in use
     */
    public static String getLocalBundlePath() {
        if (isUpdate1InUse()) {
            return RNKitConstant.JS_BUNDLE_FILE1;
        } else {
            return RNKitConstant.JS_BUNDLE_FILE2;
        }
    }

    /**
     * Use a single background thread to handle the patch install
     *
     * @param context
     */
    public static void handleTheUpdateFile(final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 1 upzip the update.zip file
                unzipTheUpdateFile();
                // 2 merge the patch file and the jsBundle file currently in used
                mergePatchAndBundle(context);
                // 3 delete the update.zip file
                FileUtils.deleteFile(RNKitConstant.UPDATE_ZIP_FILE);
            }
        }).start();
    }

    /**
     * upzip the update.zip file
     * ps:If the folder update1 currently in used, unzip it to the folder update2, and vice versa
     */
    private static void unzipTheUpdateFile() {
        String destPath = isUpdate1InUse() ? RNKitConstant.UPDATE_PATH2 : RNKitConstant.UPDATE_PATH1;
        //empty the target folder first
        FileUtils.deleteDir(destPath);
        Logger.i("unzip the file 'update.zip' to "+destPath);

        FileUtils.unzip(RNKitConstant.UPDATE_ZIP_FILE,destPath);
    }

    /**
     * merge the patch and the bundle currently in used
     * @param context
     */
    private static void mergePatchAndBundle(Context context) {
        // 1 convert the local jsBundle file to string
        String jsBundle = "";
        if (getCurrentBundleFolder().equals("")) {
            // read the jsBundle file from assets
            Logger.i("get the jsBundle file from assets");
            jsBundle = FileUtils.getJsBundleFromAssets(context);
        } else {
            //read the jsBundle from sdcard
            Logger.i("get the jsBundle file from "+getLocalBundlePath());
            jsBundle = FileUtils.getJsBundleFromSDCard(getLocalBundlePath());
        }
        //2 convert the patch file to string
        String patchStr = FileUtils.getStringFromFile(isUpdate1InUse() ? RNKitConstant.JS_PATCH_FILE2 : RNKitConstant.JS_PATCH_FILE1);

        //3 merge the patch string and the jsBundle string to generate the new jsBundle file
        merge(patchStr, jsBundle, isUpdate1InUse() ? RNKitConstant.UPDATE_PATH2 : RNKitConstant.UPDATE_PATH1);

        //4 record the folder currently in used
        SharedPrefManager.getInstance(RNKit.get().getContext()).put(RNKitConstant.SHAR_CURRENT_BUNDLE_FOLDER, isUpdate1InUse() ? RNKitConstant.UPDATE_FOLDER2 : RNKitConstant.UPDATE_FOLDER1);

        //5 install the patch
        RNKit.get().installPatch();
    }


    /**
     * Use the diff_match_patch to process the patch (https://github.com/google/diff-match-patch)
     *
     * @param patcheStr
     * @param bundle
     * @param destPath
     */
    private static void merge(String patcheStr, String bundle, String destPath) {
        //Compare the old and new patches, merge the old bundles to generate a new bundle to the target path if there are differences, otherwise copy the old bundle to the target path
        if (needMerge()) {
            Logger.i("merge and generate a new bundle to -->"+destPath);
            Writer writer = null;
            try {
                FileUtils.makeDirs(destPath);
                // 1.initialize the variable dmp
                diff_match_patch dmp = new diff_match_patch();
                // 2.find all the patches
                LinkedList<diff_match_patch.Patch> patches = (LinkedList<diff_match_patch.Patch>) dmp.patch_fromText(patcheStr);
                // 3.merge the patches and the bundle
                Object[] bundleArray = dmp.patch_apply(patches, bundle);
                // 4.save it as a bundle file
                writer = new FileWriter(destPath + File.separator + RNKitConstant.JS_BUNDLE_FILENAME);
                String newBundle = (String) bundleArray[0];
                writer.write(newBundle);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                IOUtils.close(writer);
            }
        } else {
            Logger.i("don't need to merge ,copy old bundle to -->"+destPath);
            if (isUpdate1InUse()) {
                FileUtils.copyFile(RNKitConstant.JS_BUNDLE_FILE1, RNKitConstant.JS_BUNDLE_FILE2);
            } else {
                FileUtils.copyFile(RNKitConstant.JS_BUNDLE_FILE2, RNKitConstant.JS_BUNDLE_FILE1);
            }
        }
    }

    /**
     *  Determine if updates are needed by comparing the old and new patch file
     *
     *  PS：When a patch is installed into a bundle, a new version of the bundle will be generated, and when the same patch
     *  is installed again, because the corresponding bundle version has changed, a new bundle will be generated again, causing confusion
     *
     * @return
     */
    private static boolean needMerge() {
        if (FileUtils.isFileExist(RNKitConstant.JS_PATCH_FILE1) && FileUtils.isFileExist(RNKitConstant.JS_PATCH_FILE2)) {
            diff_match_patch dmp = new diff_match_patch();
            String patch1 = FileUtils.getStringFromFile(RNKitConstant.JS_PATCH_FILE1);
            String patch2 = FileUtils.getStringFromFile(RNKitConstant.JS_PATCH_FILE2);
            LinkedList<diff_match_patch.Diff> diffs = dmp.diff_main(patch1, patch2);
            LinkedList<diff_match_patch.Patch> patches = dmp.patch_make(diffs);
            String patchesStr = dmp.patch_toText(patches);
            return !Utils.isEmpty(patchesStr);
        }
        return true;
    }

}
