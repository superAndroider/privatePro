package com.ipeercloud.com.model.EventBusEvent;

/**
 * @author 673391138@qq.com
 * @since 17/4/23
 * 主要功能: 下载进度通知事件
 */

public class GsProgressEvent {
    public int progress;
    public String remotePath;

    public GsProgressEvent(int progress, String remotePath) {
        this.progress = progress;
        this.remotePath = remotePath;
    }
}
