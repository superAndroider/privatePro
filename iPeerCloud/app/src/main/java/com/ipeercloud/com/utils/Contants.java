package com.ipeercloud.com.utils;

import android.widget.TextView;

/**
 * 各种界面设置处理
 *
 * @author Administrator
 */
public class Contants {

    public final static String SP_USERNAME = "username";

    /**一天**/
    /**一个月**/
    /**
     * 一年
     **/
    public final static long MILLIS_ONE_DAY = 24 * 60 * 60 * 1000;
    public final static long MILLIS_ONE_MONTH = MILLIS_ONE_DAY * 30;
    public final static long MILLIS_ONE_YEAR = MILLIS_ONE_MONTH * 12;


    public final static String getText(TextView textView) {
        return textView.getText().toString().trim();
    }

}
