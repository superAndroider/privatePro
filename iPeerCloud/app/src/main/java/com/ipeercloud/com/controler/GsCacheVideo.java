package com.ipeercloud.com.controler;

import android.content.Context;
import android.os.Handler;

import com.ipeercloud.com.utils.GsFile;
import com.ipeercloud.com.utils.GsLog;
import com.ipeercloud.com.view.activity.VideoViewActivity;

import java.io.File;
import java.io.IOException;

/**
 * @author 673391138@qq.com
 * @since 17/4/29
 * 主要功能: 缓存视频音频文件
 */

public class GsCacheVideo {
    private static File localDir = GsFile.getDir();
    private static File tempFile;
    private static Handler mHandler= new Handler();
    public static void cacheVideo(final Context context, final String remotePath) {
        GsLog.d("准备缓存 "+remotePath);
         tempFile = new File(localDir, "temp");
        if (!tempFile.exists()) {
            try {
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte[] buf = new byte[]{};
                int[] bufLen = new int[]{};
                boolean result = GsSocketManager.getInstance().gsReadFileBuffer(remotePath, 0, 1024 * 1024, buf, bufLen);
                GsLog.d("缓存结果 " + result);
//                if(!result){x
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            GsLog.d("打开播放");
                            VideoViewActivity.startActivity(context,localDir+"/少女时代.mp4");
                        }
                    });
//                }
            }
        }).start();
    }
}
