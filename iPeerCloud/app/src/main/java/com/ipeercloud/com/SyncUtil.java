package com.ipeercloud.com;

import com.ipeer.imageselect.bean.ImageItem;
import com.ipeercloud.com.controler.GsJniManager;
import com.ipeercloud.com.model.GsCallBack;
import com.ipeercloud.com.model.GsFileModule;
import com.ipeercloud.com.model.GsSimpleResponse;
import com.ipeercloud.com.utils.ConstantSP;
import com.ipeercloud.com.utils.Contants;
import com.ipeercloud.com.utils.GsFile;
import com.ipeercloud.com.utils.GsLog;
import com.ipeercloud.com.utils.SharedPreferencesHelper;
import com.ipeercloud.com.utils.network.NetType;
import com.ipeercloud.com.utils.network.NetworkUtils;

import java.util.List;

/**
 * Created by 李小明 on 17/4/28.
 * 邮箱:287907160@qq.com
 */

public class SyncUtil {


    //下载照片
    public static void onImagesDownLoaded(List<GsFileModule.FileEntity> imageList) {
        GsLog.d("下载图片...");
        for (GsFileModule.FileEntity entity : imageList) {
            if (!GsFile.isContainsFile(entity.FileName)) {
                //点击条目，但是条目并没有下载
                GsLog.d("开始下载 = "+entity.FileName+" Thread = "+Thread.currentThread());
                downLoadFile(entity.FileName);
            } else {
                GsLog.d("文件已经存在，" + entity.FileName);
            }
        }
    }

    private static void downLoadFile(final String fileName) {
        String remotePath = "\\"+"\\Photo\\" + fileName;

        GsJniManager.getInstance().downFile(GsFile.getPath(fileName),
                remotePath, new GsCallBack<GsSimpleResponse>() {
                    @Override
                    public void onResult(GsSimpleResponse response) {
                        if (response.result) {
                            GsLog.d("下载成功了");
                        } else {
                            GsLog.d("下载失败了");
                        }
                    }
                });
    }


    //上傳照片
    public static void onImagesUpLoaded(List<ImageItem> imageList) {
        GsLog.d("上传照片....");
        if (imageList == null || imageList.size() == 0) return;
        GsLog.d("上传照片数量 size = " + imageList.size());

        boolean isOnCameraSync = SharedPreferencesHelper.getInstance(IpeerCloudApplication.getInstance()).getBoolean(ConstantSP.SP_CAMERA_SYNC, false);
        //相机同步没有开启
        if (!isOnCameraSync) return;

        boolean isOnUseCellular = SharedPreferencesHelper.getInstance(IpeerCloudApplication.getInstance()).getBoolean(ConstantSP.SP_CAMERA_SYNC, false);
        NetType type = NetworkUtils.getConnectedType(IpeerCloudApplication.getInstance());
        //目前网络是蜂窝网络,但是没有开启蜂窝同步
        if (!isOnUseCellular && (NetType.NET_MOBILE == type)) return;

        int saveTime = SharedPreferencesHelper.getInstance(IpeerCloudApplication.getInstance()).getInt(ConstantSP.SP_PHOTO_SAVE_TIME, 0);

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
                if (!GsFile.isContainsFile(item.name)) {
                    //点击条目，但是条目并没有下载
                    GsLog.d("开始上传 = " + item.name + "   Thread= " + Thread.currentThread());
                    GsJniManager.getInstance().uploadFile(item.path, item.name, new GsCallBack<GsSimpleResponse>() {
                        @Override
                        public void onResult(GsSimpleResponse response) {
                            if (response.result) {
                                GsLog.d("上传成功: Thread = " + android.os.Process.myTid());
                            } else {
                                GsLog.d("上传失败: Thread = " + android.os.Process.myTid());
                            }
                        }
                    });
                } else {
                    GsLog.d("文件已经存在，不需要上传" + item.name);
                }

            }
        }

    }

}
