package com.ipeercloud.com.model;

/**
 * @author 673391138@qq.com
 * @since 17/4/18
 * 主要功能:
 */

public interface GsCallBack<T extends GsResponse> {
    void onResult(T response);
}