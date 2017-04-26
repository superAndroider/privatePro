package com.ipeercloud.com.view.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.ipeer.imageselect.bean.ImageItem;
import com.ipeer.imageselect.bean.ImageSet;
import com.ipeer.imageselect.data.DataSource;
import com.ipeer.imageselect.data.OnImagesLoadedListener;
import com.ipeer.imageselect.data.impl.LocalDataSource;
import com.ipeer.imageselect.ui.ImagePreviewActivity;
import com.ipeercloud.com.controler.GsJniManager;
import com.ipeercloud.com.controler.GsThreadPool;
import com.ipeercloud.com.model.GsCallBack;
import com.ipeercloud.com.model.GsResponse;
import com.ipeercloud.com.model.GsSimpleResponse;
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

public class SyncUploadService extends Service {

    private List<ImageItem> imageItems = new ArrayList<>();

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
        imageItems = (List<ImageItem>) intent.getSerializableExtra(ImagePreviewActivity.LIST_IMAGES);

        new Thread(new Runnable() {
            @Override
            public void run() {
                onImagesLoaded(imageItems);
            }
        }).start();

        GsLog.d("SyncService onStart = " + imageItems.size());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        GsLog.d("SyncService onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }


    //上傳照片
    public void onImagesLoaded(List<ImageItem> imageList) {
        GsLog.d("load listUrls");
        if (imageList == null || imageList.size() == 0) return;

        boolean isOnCameraSync = SharedPreferencesHelper.getInstance(this).getBoolean(ConstantSP.SP_CAMERA_SYNC, false);
        //相机同步没有开启
        if (!isOnCameraSync) return;

        boolean isOnUseCellular = SharedPreferencesHelper.getInstance(this).getBoolean(ConstantSP.SP_CAMERA_SYNC, false);
        NetType type = NetworkUtils.getConnectedType(this);
        //目前网络是蜂窝网络,但是没有开启蜂窝同步
        if (!isOnUseCellular && (NetType.NET_MOBILE == type)) return;

        int saveTime = SharedPreferencesHelper.getInstance(this).getInt(ConstantSP.SP_PHOTO_SAVE_TIME, 0);

        //上传N天以前的照片
        long oneDayAgo = 0l;
        switch (saveTime) {
            case 0:
                oneDayAgo = System.currentTimeMillis() - Contants.MILLIS_ONE_DAY;
                break;
            case 1:
                oneDayAgo = System.currentTimeMillis() - Contants.MILLIS_ONE_MONTH;
                break;
            case 2:
                oneDayAgo = System.currentTimeMillis() - Contants.MILLIS_ONE_YEAR;
                break;
            default:
                oneDayAgo = System.currentTimeMillis() - Contants.MILLIS_ONE_DAY;

        }
        oneDayAgo = oneDayAgo / 1000;

        for (final ImageItem item : imageList) {
            if (item.time > oneDayAgo) {
                GsJniManager.getInstance().uploadFile(item.path, item.name, new GsCallBack<GsSimpleResponse>() {
                    @Override
                    public void onResult(GsSimpleResponse response) {
                        if (response.result) {
                            GsLog.d("上传成功:");
                        } else {
                            GsLog.d("上传失败:");
                        }
                    }
                });
            }
        }

    }


//    /**
//     * 上传照片
//     */
//    private void upLoadFile(String localpath, final String fileName) {
//        GsJniManager.getInstance().upLoadFile(localpath, GsJniManager.PHOTO_PARAM + "\\" + fileName, new GsCallBack<GsSimpleResponse>() {
//            @Override
//            public void onResult(GsSimpleResponse response) {
//                if (response.result) {
//                    GsLog.d("上传成功:" + fileName);
////                    Toast.makeText(getContext(), fileName + "上传成功", Toast.LENGTH_LONG).show();
//                } else {
//                    GsLog.d("上传失败:" + fileName);
////                    Toast.makeText(getContext(), fileName + "上传失败", Toast.LENGTH_LONG).show();
//                }
//            }
//        });
//    }
}
