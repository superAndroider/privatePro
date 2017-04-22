package com.ipeercloud.com.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author 673391138@qq.com
 * @since 17/4/22
 * 主要功能:SharedPreferences 帮助类
 */

public class GsSp {
    private static GsSp instance;
    private Context context;
    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSp;

    private GsSp() {
        mSp = context.getSharedPreferences("GsSp", Context.MODE_APPEND);
        mEditor = mSp.edit();
    }

    public void putString(String key, String value) {
        mEditor.putString(key, value);
        mEditor.apply();
    }

    public String getString(String key) {
        return mSp.getString(key, "");
    }

    public static GsSp getInstance(Context context) {
        if (context == null)
            return null;
        if (instance == null) {
            instance = new GsSp();
        }
        return instance;
    }
}
