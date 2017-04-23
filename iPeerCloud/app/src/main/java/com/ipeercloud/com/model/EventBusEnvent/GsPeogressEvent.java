package com.ipeercloud.com.model.EventBusEnvent;

/**
 * @author 673391138@qq.com
 * @since 17/4/23
 * 主要功能:
 */

public class GsPeogressEvent {
    public long currentLength;
    public long totalLength;
    public String remotePath;

    public GsPeogressEvent(long currentLength, long totalLength, String remotePath) {
        this.currentLength = currentLength;
        this.totalLength = totalLength;
        this.remotePath = remotePath;
    }
}
