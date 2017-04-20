package com.ipeercloud.com.model;

import android.text.TextUtils;

import com.ipeercloud.com.utils.GsLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 673391138@qq.com
 * @since 17/4/18
 * 主要功能: file数据结构
 */

public class GsFileModule {
    public List<FileEntity> fileList;

    public static class FileEntity {
        public String FileName;
        public long FileSize;
        public int FileType;
        public long LastModifyTime;

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
                entity.FileType = jb.optInt("FileType");
                entity.LastModifyTime = jb.optLong("LastModifyTime");
                fileList.add(entity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        GsLog.d("大小是 " + fileList.size());
        GsLog.d("" + fileList.get(0).FileName);
    }
}
