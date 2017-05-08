package com.ipeercloud.com.model;

import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 673391138@qq.com
 * @since 17/4/18
 * 主要功能: file数据结构
 */

public class GsFileModule {
    public List<FileEntity> fileList;

    public static class FileEntity implements Serializable{
        public String FileName;
        public long FileSize;
        public int FileType;
        public long lastModifyTime;
        //下载进度，-1 表示未下载，0-99表示下载中，100表示下载完成
        public int loadingProgress = -1;
        @Override
        public boolean equals(Object o) {
            if (FileName == null || o == null) {
                return false;
            }
            if (o instanceof FileEntity && FileName.equals(((FileEntity) o).FileName)) {
                return true;
            }
            return false;
        }
    }

    public GsFileModule() {
        fileList = new ArrayList<>();
    }

    public GsFileModule(String json) {
        if (TextUtils.isEmpty(json)) {
            return;
        }
        try {
            JSONArray ja = new JSONArray(json);
            int size = ja.length();
            fileList = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                JSONObject jb = (JSONObject) ja.get(i);
                FileEntity entity = new FileEntity();
                entity.FileName = jb.optString("FileName");
                entity.FileSize = jb.optLong("FileSize");
                entity.FileType = jb.optInt("FileName");
                entity.lastModifyTime = jb.optLong("lastModifyTime");
                if (!TextUtils.isEmpty(entity.FileName) && !entity.FileName.contains("gcloudmd5")) {
                    fileList.add(entity);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void addEntity(FileEntity entity) {
        if (entity == null)
            return;
        if (fileList.contains(entity)) {
            return;
        }
        fileList.add(entity);
    }
}
