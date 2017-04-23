package com.ipeercloud.com.model.EventBusEvent;

/**
 * @author 673391138@qq.com
 * @since 17/4/23
 * 主要功能: 下载进度通知事件
 */

public class GsProgressEvent {
    public long currentLength;
    public long totalLength;
    public String remotePath;

    public GsProgressEvent(long currentLength, long totalLength, String remotePath) {
        this.currentLength = currentLength;
        this.totalLength = totalLength;
        this.remotePath = remotePath;
    }
}
