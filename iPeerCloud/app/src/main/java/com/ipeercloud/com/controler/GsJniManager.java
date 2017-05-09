package com.ipeercloud.com.controler;

import android.os.Handler;
import android.text.TextUtils;

import com.ipeercloud.com.model.GsCallBack;
import com.ipeercloud.com.model.GsFileModule;
import com.ipeercloud.com.model.GsSimpleResponse;
import com.ipeercloud.com.store.GsDataManager;
import com.ipeercloud.com.utils.GsLog;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * @author 673391138@qq.com
 * @since 17/4/18
 * 主要功能: jin请求类
 */

public class GsJniManager {
    //根目录下的内容
    public static final String FILE_PARAM = "\\";
    //Medias下的内容
    public static final String PHOTO_PARAM = "\\Photo";
    public static final String MEDIA_PARAM = "\\Medias";
    public static final String SHARE_PARAM = "\\ShareIn";
    private Handler mHandler;
    private static GsJniManager instance;
    private String server;
    private String user;
    private String password;
    private Queue<Runnable> mDownLoadRunnables = new LinkedBlockingQueue<>(128);
    private Queue<Runnable> mUpLoadRunnables = new LinkedBlockingQueue<>(128);
    private boolean mCanLoad = true;
    private boolean mCanUpLoad = true;

    private GsJniManager() {
        mHandler = new Handler();
    }

    public static GsJniManager getInstance() {
        if (instance == null) {
            instance = new GsJniManager();
        }
        return instance;
    }

    public void register(final String server, final String user, final String password, final GsCallBack callback) {
        this.server = server;
        this.user = user;
        this.password = password;
        GsThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final boolean result = GsSocketManager.getInstance().gsUserRegister(server, user, password);
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

    public void login(final String server, final String user, final String password, final GsCallBack callback) {
        this.server = server;
        this.user = user;
        this.password = password;
        GsThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final boolean result = GsSocketManager.getInstance().gsLogin(server, user, password);
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

    public void loginAgain(final Runnable runnable) {
        GsThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                GsLog.d("重连入参 " + server + "   " + user + "   " + password);
                final boolean result = GsSocketManager.getInstance().gsLogin(server, user, password);
                if (result) {
                    GsLog.d("重连后继续工作");
                    GsThreadPool.getInstance().execute(runnable);
                } else {
                    GsLog.d("重连失败");
                }
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
     * 从远端下载文件
     */
    public void downFile(final String localPath, final String remotePath, final GsCallBack callback) {
        mDownLoadRunnables.add(new Runnable() {
            @Override
            public void run() {
                GsLog.d("开始下载  "+localPath+"    远端路径："+remotePath);
                final int result = GsSocketManager.getInstance().gsGetFile(remotePath, localPath);
                downFinish();
                if (callback == null) return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(new GsSimpleResponse(result == 0));
                    }
                });
            }
        });
        if (mCanLoad && mDownLoadRunnables.peek() != null) {
            mCanLoad = false;
            GsThreadPool.getInstance().execute(mDownLoadRunnables.poll());
        }

    }

    /**
     * 一个下载任务结束后，开始下一个下载任务
     */
    private void downFinish() {
        mCanLoad = true;
        if (mDownLoadRunnables.peek() != null) {
            mCanLoad = false;
            GsThreadPool.getInstance().execute(mDownLoadRunnables.poll());
        }
    }

    /**
     * 获得指定目录下的文件
     *
     * @param path
     * @param isTabClick 是不是点击一个tab发起的请求
     */
    public void getPathFile(final String path, final boolean isTabClick, final GsCallBack callback) {
        GsLog.d("请求路径： " + path);
        GsThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String result = null;
                try {
                    result = GsSocketManager.getInstance().gsGetPathFile(path);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                GsLog.d("json数据： " + result);
                if (result == null) {
                    return;
                }
                if (isTabClick) {
                    switch (path) {
                        case FILE_PARAM:
                            updateList(GsDataManager.getInstance().files.fileList, new GsFileModule(result).fileList);

                            break;
                        case PHOTO_PARAM:
                            updateList(GsDataManager.getInstance().photos.fileList, new GsFileModule(result).fileList);

                            break;

                        case MEDIA_PARAM:
                            updateList(GsDataManager.getInstance().medias.fileList, new GsFileModule(result).fileList);
                            break;
                    }
                } else {
                    GsFileModule fileModule = GsDataManager.getInstance().fileMaps.get(path);
                    if (fileModule == null) {
                        GsDataManager.getInstance().fileMaps.put(path, new GsFileModule(result));
                    } else {
                        updateList(fileModule.fileList, new GsFileModule(result).fileList);
                    }
                }
                if (callback == null) return;
                final boolean success = !TextUtils.isEmpty(result);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(new GsSimpleResponse(success));
                    }
                });

            }
        });
    }

    /**
     * 从远端获取某个文件夹下面的文件后需要将本地没有的补上，本地有的不再变化，防止本地文件下载状态丢失
     */
    private void updateList(List<GsFileModule.FileEntity> localList, List<GsFileModule.FileEntity> remoteList) {
        if (remoteList == null || remoteList.size() == 0) {
            return;
        }
        if (localList.size() == 0) {
            //localList.addAll(remoteList);
            for (int k = 0; k < remoteList.size(); k++) {
                //GsLog.d("index="+remoteList.get(k).FileName.indexOf("gcloudmd5"));
                if (remoteList.get(k).FileName.indexOf("gcloudmd5") == -1) {
                    localList.add(remoteList.get(k));
                }
            }
            return;
        }

        int localSize = localList.size();
        int remoteSize = remoteList.size();
        for (int i = 0; i < remoteSize; i++) {
            boolean find = false;
            for (int j = 0; j < localSize; j++) {
                if (localList.get(j).equals(remoteList.get(i))) {
                    find = true;
                }
            }
            if (!find) {
                if (remoteList.get(i).FileName.indexOf("gcloudmd5") == -1) {
                    localList.add(remoteList.get(i));
                }
            }
        }
    }

    public void upLoadOneFile(final String localPath, final String remotePath, final GsCallBack<GsSimpleResponse> callBack) {
        GsThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final boolean result = GsSocketManager.getInstance().gsPutFile(localPath, remotePath);
                GsLog.d("上传 = " + remotePath);
                if (callBack == null)
                    return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onResult(new GsSimpleResponse(result));
                    }
                });
            }
        });

    }

    /**
     * 上传文件
     */
    public void uploadFile(final String localPath, final String remotePath, final GsCallBack callback) {
        mUpLoadRunnables.add(new Runnable() {
            @Override
            public void run() {
                GsLog.d("上传文件 ==" + localPath);
                final boolean result = GsSocketManager.getInstance().gsPutFile(localPath, remotePath);
                uploadFinish();
                if (callback == null) return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(new GsSimpleResponse(result));
                    }
                });
            }
        });
        if (mCanUpLoad && mUpLoadRunnables.peek() != null) {
            mCanUpLoad = false;
            GsThreadPool.getInstance().execute(mUpLoadRunnables.poll());
        }

    }

    private void uploadFinish() {
        mCanUpLoad = true;
        if (mUpLoadRunnables.peek() != null) {
            mCanLoad = false;
            GsThreadPool.getInstance().execute(mUpLoadRunnables.poll());
        }
    }

    public void addWIFI(final String wifiName, final String password, final GsCallBack<GsSimpleResponse> callBack) {
        GsThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                GsLog.d("請求添加wifi模块");
                final boolean result = GsSocketManager.getInstance().gsAddWifi(wifiName, password);
                GsLog.d("添加wifi模块 = " + result);
                if (callBack == null)
                    return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callBack.onResult(new GsSimpleResponse(result));
                    }
                });
            }
        });
    }

    public void loginOut(final String server, final String userName, final String password) {
        GsThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final boolean result = GsSocketManager.getInstance().gsUserRegister(server, userName, password);
            }
        });
    }
}
