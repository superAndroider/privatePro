package com.ipeercloud.com.utils;

import android.os.Environment;
import android.text.TextUtils;

import java.io.File;

/**
 * @author 673391138@qq.com
 * @since 17/4/19
 * 主要功能: 本地文件管理
 */

public class GsFile {
    public static final String DIR_NAME = "hkctest";

    /**
     * 创建根目录下的文件夹
     */
    public static File getDir() {
        File root = Environment.getExternalStorageDirectory();
        if (!root.exists()) {
            root.mkdir();
        }
        File dir = new File(root, DIR_NAME);
        if (!dir.exists()) {
            dir.mkdir();
        }
        return dir;
    }

    public static String getPath(String fileName) {
        File root = Environment.getExternalStorageDirectory();
        if (!root.exists()) {
            root.mkdir();
        }
        if (root == null) {
            return null;
        }
        File dir = new File(root, DIR_NAME);
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, fileName);
        return file.getPath();
    }

    /**
     * @param fileName 是否包括指定的文件名
     * @return
     */
    public static boolean isContainsFile(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            GsLog.e("isContainsFile fileName is null !");
            return false;
        }
        File dir = getDir();
        if (dir == null)
            return false;
        File[] fileList = dir.listFiles();
        if (fileList == null) {
            return false;
        }
        int length = fileList.length;
        for (int i = 0; i < length; i++) {
            if (fileName.equals(fileList[i].getName())) {
                return true;
            }
        }
        return false;
    }
}
