package com.coship.rnkit;

import com.coship.rnkit.update.hotupdate.diff_match_patch;

import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

/**
 * 作者：909881 时间：2019/6/12 14:31 版本：1.0 描述：一句话描述
 */
public class Generator {

    @Test
    public void generatePatch() {
        try {
            String oldBundle = getJsBundleFromSDCard("D:/RNProjects/rnkit_example/android/app/src/main/assets/index.android.bundle");
            String newBundle = getJsBundleFromSDCard("D:/RNProjects/rnkit_example/bundle/index.android.bundle");

            diff_match_patch dmp = new diff_match_patch();
            //1 找出差异
            LinkedList<diff_match_patch.Diff> diffs = dmp.diff_main(oldBundle, newBundle);
            // 2 生成差异补丁
            LinkedList<diff_match_patch.Patch> patches = dmp.patch_make(diffs);
            // 3 补丁转字符串
            String patchesStr = dmp.patch_toText(patches);
            // 4 生成补丁文件
            Files.write(Paths.get("D:/RNProjects/rnkit_example/bundle/update.patch"),patchesStr.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getJsBundleFromSDCard(String filePath) {
        String result = "";
        try {
            InputStream is = new FileInputStream(filePath);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            result = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
