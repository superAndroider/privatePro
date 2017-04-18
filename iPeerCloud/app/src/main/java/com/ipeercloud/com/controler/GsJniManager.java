package com.ipeercloud.com.controler;

import android.os.Handler;

import com.ipeercloud.com.model.GsFileModule;
import com.ipeercloud.com.model.GsCallBack;
import com.ipeercloud.com.model.GsSimpleResponse;
import com.ipeercloud.com.utils.GsLog;


/**
 * @author 673391138@qq.com
 * @since 17/4/18
 * 主要功能: jin请求类
 */

public class GsJniManager {
    private Handler mHandler;
    private static GsJniManager instance;

    private GsJniManager() {
        mHandler = new Handler();
    }

    public static GsJniManager getInstance() {
        if (instance == null) {
            instance = new GsJniManager();
        }
        return instance;
    }

    public void register(final String server, final String user, final String passowrd, final GsCallBack callback) {
        GsThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final boolean result = GsSocketManager.getInstance().gsUserRegister(server, user, passowrd);
                if (callback == null) return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(new GsSimpleResponse(result));
                    }
                });

            }
        });
    }

    /**
     * 远端设备是否在线
     */
    public void isOnline(final GsCallBack callback) {
        GsThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final boolean result = GsSocketManager.getInstance().gsOnline();
                if (callback == null) return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(new GsSimpleResponse(result));
                    }
                });

            }
        });
    }

    /**
     * 是否已连接远端设备
     */
    public void isLink(final GsCallBack callback) {
        GsThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final boolean result = GsSocketManager.getInstance().gsLinked();
                if (callback == null) return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(new GsSimpleResponse(result));
                    }
                });

            }
        });
    }

    /**
     * 获得指定目录下的文件
     */
    public void getPathFile(final String path, final GsCallBack callback) {
        GsThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final String result = GsSocketManager.getInstance().gsGetPathFile(path);
                if (callback == null) return;
                GsLog.d("文件json数据： "+result);
                new GsFileModule(result);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
//                        callback.onResult(new GsSimpleResponse(result));
                    }
                });

            }
        });
    }
}
