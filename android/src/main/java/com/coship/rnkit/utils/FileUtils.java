package com.coship.rnkit.utils;

import android.content.Context;

import com.coship.rnkit.constants.RNKitConstant;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 *  author: zoujunda
 *  date: 2019/6/12 15:36
 *	version: 1.0
 *  description: a file-handling classes
 */
public class FileUtils {


    /**
     *
     * @param zipFilePath zip file path
     * @param destPath the unzip target path
     */
    public static void unzip(String zipFilePath,String destPath) {
        ZipInputStream inZip = null;
        FileOutputStream fos = null;
        ZipEntry zipEntry;
        String szName;
        try {
            FileUtils.makeDirs(destPath);
            inZip = new ZipInputStream(new FileInputStream(zipFilePath));

            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(destPath + File.separator + szName);
                    folder.mkdirs();
                } else {
                    File file = new File(destPath + File.separator + szName);
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = inZip.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                        fos.flush();
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(inZip);
            IOUtils.close(fos);
        }
    }


    /**
     * Parse the jsBundle file under assets to string
     *
     * @param context
     * @return
     */
    public static String getJsBundleFromAssets(Context context) {
        String result = "";
        InputStream is = null;
        try {
            is = context.getAssets().open(RNKitConstant.JS_BUNDLE_FILENAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            result = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(is);
        }
        return result;
    }

    /**
     * parse the jsBundle file under sdcard to string
     *
     * @param filePath
     * @return
     */
    public static String getJsBundleFromSDCard(String filePath) {
        String result = "";
        InputStream fis = null;
        try {
            fis = new FileInputStream(filePath);
            int size = fis.available();
            byte[] buffer = new byte[size];
            fis.read(buffer);
            result = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(fis);
        }
        return result;
    }

    /**
     * Parse the file to string
     *
     * @param filePath
     * @return
     */
    public static String getStringFromFile(String filePath) {
        FileReader reader = null;
        String result = "";
        try {
            reader = new FileReader(filePath);
            int ch = reader.read();
            StringBuilder sb = new StringBuilder();
            while (ch != -1) {
                sb.append((char) ch);
                ch = reader.read();
            }
            result = sb.toString();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(reader);
        }
        return result;
    }


    /**
     * Recursively delete all files in the directory and all files in the subdirectory
     *
     * @param dir
     */
    public static void deleteDir(String dir) {
        File file = new File(dir);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if(files != null && files.length > 0){
                for (File f : files) {
                    if (f.isDirectory()) {
                        deleteDir(f.getAbsolutePath());
                    } else {
                        f.delete();
                    }
                }
            }
            file.delete();
        }
    }

    /**
     * Delete the specified file
     *
     * @param filePath
     */
    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * Is the file exist ?
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExist(String filePath) {
        if (Utils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return (file.exists() && file.isFile());
    }

    /**
     * is the dir exist
     *
     * @param directoryPath
     * @return
     */
    public static boolean isFolderExist(String directoryPath) {
        if (Utils.isEmpty(directoryPath)) {
            return false;
        }
        File dire = new File(directoryPath);
        return (dire.exists() && dire.isDirectory());
    }

    /**
     * Create a directory
     *
     * @param filePath
     * @return
     */
    public static boolean makeDirs(String filePath) {
        return isFolderExist(filePath) ? true : new File(filePath).mkdirs();
    }


    /**
     * Copy the file to another directory
     *
     * @param sourceFilePath
     * @param destFilePath
     */
    public static void copyFile(String sourceFilePath, String destFilePath) {
        File oldFile = new File(sourceFilePath);
        File newFile = new File(destFilePath);
        DataInputStream dis = null;
        DataOutputStream dos = null;
        int temp;
        try {
            dos = new DataOutputStream(new FileOutputStream(newFile));
            dis = new DataInputStream(new FileInputStream(oldFile));

            while ((temp = dis.read()) != -1) {
                dos.write(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(dos);
            IOUtils.close(dis);
        }
    }


}
