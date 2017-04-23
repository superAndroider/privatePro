package com.ipeercloud.com.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.ipeercloud.com.IpeerCloudApplication;

/**
 * @author 673391138@qq.com
 * @since 17/4/22
 * 主要功能:SharedPreferences 帮助类
 */

public class GsSp {
    private static GsSp instance;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSp;

    private GsSp() {
        mSp = IpeerCloudApplication.instance.getSharedPreferences("GsSp", Context.MODE_APPEND);
        mEditor = mSp.edit();
    }

    public void putString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.commit();
    }

    /**
     * @param jsonString 存储目录文件专用
     */
    public void putFileMap(String jsonString) {
        SharedPreferences sp = IpeerCloudApplication.instance.getSharedPreferences("GsSpMap", Context.MODE_APPEND);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("filemap", jsonString);
        editor.apply();
    }

    /**
     * 存储目录文件专用
     */
    public String getFileMap() {
        SharedPreferences sp = IpeerCloudApplication.instance.getSharedPreferences("GsSpMap", Context.MODE_APPEND);
        return sp.getString("filemap", "");
    }

    public String getString(String key) {
        return mSp.getString(key, "");
    }

    public static GsSp getInstance() {
        if (instance == null) {
            instance = new GsSp();
        }
        return instance;
    }
}
