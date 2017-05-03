package com.ipeercloud.com.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ipeer.imageselect.bean.ImageItem;
import com.ipeercloud.com.SyncUtil;
import com.ipeercloud.com.utils.GsLog;

import java.util.ArrayList;

/**
 * Created by 李小明 on 17/4/26.
 * 邮箱:287907160@qq.com
 */

public class SyncUploadService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        GsLog.d("SyncUploadService  onStart");
        if (intent == null) return;

        final ArrayList<ImageItem> items = (ArrayList<ImageItem>) intent.getSerializableExtra("localPathList");

        if (items == null) {
            GsLog.d("本地照片數量為空");
            return ;
        }


        new Thread(new Runnable() {
            @Override
            public void run() {
                GsLog.d("子线程 Thread = " + Thread.currentThread());
                SyncUtil.onImagesUpLoaded(items);
            }
        }).start();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GsLog.d("SyncUploadService onStartCommand Thread = " + Thread.currentThread());
        return super.onStartCommand(intent, flags, startId);
    }

}
