package com.ipeercloud.com.controler;

import android.os.Handler;
import android.text.TextUtils;

import com.ipeercloud.com.model.GsCallBack;
import com.ipeercloud.com.model.GsFileModule;
import com.ipeercloud.com.model.GsSimpleResponse;
import com.ipeercloud.com.store.GsDataManager;
import com.ipeercloud.com.utils.GsLog;

import java.util.List;


/**
 * @author 673391138@qq.com
 * @since 17/4/18
 * 主要功能: jin请求类
 */

public class GsJniManager {
    //根目录下的内容
    public static final String FILE_PARAM = "\\";
    //Medias下的内容
    public static final String MEDIA_PARAM = "\\Medias";
    public static final String SHARE_PARAM = "\\ShareIn";
    private Handler mHandler;
    private static GsJniManager instance;
    private String server;
    private String user;
    private String password;

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
        GsThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                GsLog.d("本地路径: " + localPath + "  远端路径：  " + remotePath);
                final int result = GsSocketManager.getInstance().gsGetFile(remotePath, localPath);
                /*if (result == -1) {
                    GsLog.d("请求失败，需要重连");
                    loginAgain(new Runnable() {
                        @Override
                        public void run() {
                            downFile(localPath, remotePath, callback);
                        }
                    });
                    return;
                }*/
                GsLog.d("返回结果 " + result);
                if (callback == null) return;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResult(new GsSimpleResponse(result == 0));
                    }
                });

            }
        });
    }

    /**
     * 获得指定目录下的文件
     *
     * @param path
     * @param isTabClick 是不是点击一个tab发起的请求
     */
    public void getPathFile(final String path, final boolean isTabClick, final GsCallBack callback) {
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
        if (localList.size() == 0) {
            localList.addAll(remoteList);
            return;
        }
        if (remoteList.size() == 0) {
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
                localList.add(remoteList.get(i));
            }
        }
    }

    public void upLoadFile(final String localPath, final String remotePath, final GsCallBack<GsSimpleResponse> callBack) {
        GsThreadPool.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final boolean result = GsSocketManager.getInstance().gsPutFile(localPath, remotePath);
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

}
