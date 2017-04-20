package com.ipeercloud.com.store;

import com.ipeercloud.com.model.GsFileModule;

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

    private static GsDataManager instance;

    private GsDataManager() {
    }

    public static GsDataManager getInstance() {
        if (instance == null) {
            instance = new GsDataManager();
        }
        return instance;
    }


}
