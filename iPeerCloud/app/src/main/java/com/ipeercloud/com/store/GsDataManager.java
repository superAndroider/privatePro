package com.ipeercloud.com.store;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ipeercloud.com.controler.GsFileHelper;
import com.ipeercloud.com.model.GsFileModule;
import com.ipeercloud.com.utils.GsLog;
import com.ipeercloud.com.utils.GsSp;

import java.util.HashMap;
import java.util.Map;

/**
 * @author 673391138@qq.com
 * @since 17/4/19
 * 主要功能:
 */

public class GsDataManager {
    // 文件
    public GsFileModule files;
    // 文件
    public GsFileModule photos;
    // 视频
    public GsFileModule medias;
    // 最新
    public GsFileModule recentFile;

    //存储临时目录的数据结构，以当前的远端的绝对路径为key
    public Map<String, GsFileModule> fileMaps = new HashMap<>();

    private static GsDataManager instance;

    private GsDataManager() {
        files = new GsFileModule();
        photos = new GsFileModule();
        medias = new GsFileModule();
        recentFile = new GsFileModule();
    }

    public static GsDataManager getInstance() {
        if (instance == null) {
            instance = new GsDataManager();
        }
        return instance;
    }

    /**
     * 将数据本地化
     */
    public void saveDataLocal() {
        Gson gson = new Gson();
        String gsonString = gson.toJson(files, GsFileModule.class);
        GsSp.getInstance().putString("files", gsonString);

        gsonString = gson.toJson(photos, GsFileModule.class);
        GsSp.getInstance().putString("photos", gsonString);

        gsonString = gson.toJson(recentFile, GsFileModule.class);
        GsSp.getInstance().putString("recentFile", gsonString);
        gsonString = gson.toJson(medias, GsFileModule.class);
        GsSp.getInstance().putString("medias", gsonString);
        gsonString = gson.toJson(fileMaps);
        GsSp.getInstance().putFileMap(gsonString);

    }

    /**
     * 恢复本地数据
     */
    public void recoverData() {
        Gson gson = new Gson();
        String jsonString = GsSp.getInstance().getString("files");
        if (!TextUtils.isEmpty(jsonString)) {
            files = gson.fromJson(jsonString, GsFileModule.class);
        }

        jsonString = GsSp.getInstance().getString("photos");
        if (!TextUtils.isEmpty(jsonString)) {
            photos = gson.fromJson(jsonString, GsFileModule.class);
        }

        jsonString = GsSp.getInstance().getString("recentFile");
        if (!TextUtils.isEmpty(jsonString)) {
            recentFile = gson.fromJson(jsonString, GsFileModule.class);
        }
        jsonString = GsSp.getInstance().getString("medias");
        if (!TextUtils.isEmpty(jsonString)) {
            medias = gson.fromJson(jsonString, GsFileModule.class);
        }
        jsonString = GsSp.getInstance().getFileMap();
        GsLog.d("拿出数据 " + jsonString);
        if (!TextUtils.isEmpty(jsonString)) {
            fileMaps = gson.fromJson(jsonString, new TypeToken<Map<String, GsFileModule>>() {
            }.getType());
        }
    }

    /**
     * 清除最近列表
     */
    public void clearRecentFiles() {
        Gson gson = new Gson();
        GsSp.getInstance().putString("recentFile", "");
        recentFile.fileList.clear();
    }

    /**
     * 更新数据层中下载进度
     */
    public void updateDownLoadProgress(int progress, String remotePath) {
        if (TextUtils.isEmpty(remotePath)) {
            return;
        }
        String folderName = GsFileHelper.getFolderNameFromRemotePath(remotePath);
        String fileName = GsFileHelper.getFileNameFromRemotePath(remotePath);
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        if (TextUtils.isEmpty(folderName)) {
            //根路径
            updateProgressInner(files, fileName, progress);
            return;
        }
        if (folderName.equals("\\medias")) {
            //视频路径
            updateProgressInner(medias, fileName, progress);
            return;
        }
        GsFileModule module = fileMaps.get(folderName);
        if (module == null) {
            return;
        }
        updateProgressInner(module, fileName, progress);
    }

    private void updateProgressInner(GsFileModule module, String fileName, int progress) {
        if (module == null || module.fileList == null || module.fileList.size() == 0) {
            return;
        }
        int size = module.fileList.size();
        for (int i = 0; i < size; i++) {
            if (fileName.equals(module.fileList.get(i).FileName)) {
                module.fileList.get(i).loadingProgress = progress;
                return;
            }
        }
    }

    /**
     * 退出登录，清除用户名和密码
     */
    public void loginOut() {
        GsSp.getInstance().putString("email", null);
        GsSp.getInstance().putString("passWord", null);
    }
}
