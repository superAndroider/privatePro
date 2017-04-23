package com.ipeercloud.com.store;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.ipeercloud.com.model.GsFileModule;
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
    // 视频
    public GsFileModule medias;
    // 最新
    public GsFileModule recentFile;

    //子文件夹的数据
    public GsFileModule subFiles;
    //存储临时目录的数据结构，以当前的远端的绝对路径为key
    public Map<String, GsFileModule> fileMaps = new HashMap<>();

    private static GsDataManager instance;

    private GsDataManager() {
        files = new GsFileModule();
        medias = new GsFileModule();
        recentFile = new GsFileModule();
        subFiles = new GsFileModule();
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
        gsonString = gson.toJson(recentFile, GsFileModule.class);
        GsSp.getInstance().putString("recentFile", gsonString);
        gsonString = gson.toJson(medias, GsFileModule.class);
        GsSp.getInstance().putString("medias", gsonString);
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
        jsonString = GsSp.getInstance().getString("recentFile");
        if (!TextUtils.isEmpty(jsonString)) {
            recentFile = gson.fromJson(jsonString, GsFileModule.class);
        }
        jsonString = GsSp.getInstance().getString("medias");
        if (!TextUtils.isEmpty(jsonString)) {
            medias = gson.fromJson(jsonString, GsFileModule.class);
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
}
