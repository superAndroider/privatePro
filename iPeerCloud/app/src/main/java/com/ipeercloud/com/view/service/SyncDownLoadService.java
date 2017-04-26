package com.ipeercloud.com.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.ipeer.imageselect.bean.ImageItem;
import com.ipeer.imageselect.ui.ImagePreviewActivity;
import com.ipeercloud.com.controler.GsJniManager;
import com.ipeercloud.com.model.GsCallBack;
import com.ipeercloud.com.model.GsFileModule;
import com.ipeercloud.com.model.GsSimpleResponse;
import com.ipeercloud.com.store.GsDataManager;
import com.ipeercloud.com.utils.ConstantSP;
import com.ipeercloud.com.utils.Contants;
import com.ipeercloud.com.utils.GsLog;
import com.ipeercloud.com.utils.SharedPreferencesHelper;
import com.ipeercloud.com.utils.network.NetType;
import com.ipeercloud.com.utils.network.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李小明 on 17/4/26.
 * 邮箱:287907160@qq.com
 */

public class SyncDownLoadService extends Service {

    private List<GsFileModule.FileEntity> imageItems = new ArrayList<>();

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

        if (intent == null) return;
        imageItems = GsDataManager.getInstance().photos.fileList;

        new Thread(new Runnable() {
            @Override
            public void run() {
                onImagesLoaded(imageItems);
            }
        }).start();

        GsLog.d("SyncDownLoadService onStart = " + imageItems.size());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GsLog.d("SyncDownLoadService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }


    //下载照片
    public void onImagesLoaded(List<GsFileModule.FileEntity> imageList) {
        GsLog.d("load listUrls");

    }
}
