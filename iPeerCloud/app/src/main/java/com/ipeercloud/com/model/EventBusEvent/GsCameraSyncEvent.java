package com.ipeercloud.com.model.EventBusEvent;

/**
 * @author 673391138@qq.com
 * @since 17/4/23
 * 主要功能: 下载进度通知事件
 */

public class GsCameraSyncEvent {
    public boolean isOn;

    public GsCameraSyncEvent(boolean isOn) {
        this.isOn = isOn;
    }
}
