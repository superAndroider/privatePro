package com.ipeercloud.com.controler;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 673391138@qq.com
 * @since 17/4/18
 * 主要功能:
 */

public class GsThreadPool {
    private ThreadPoolExecutor poolExecutor;
    private static final int CORE_SIZE = 4;
    private static final int MAX_SIZE = 15;
    private static GsThreadPool instance;


    public static GsThreadPool getInstance() {
        if (instance == null) {
            instance = new GsThreadPool();
        }
        return instance;
    }
    private GsThreadPool() {
        poolExecutor = new ThreadPoolExecutor(CORE_SIZE, MAX_SIZE, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(128));
    }

    public void execute(Runnable runnable){
//        GsLog.d("线程信息：  "+poolExecutor.toString());
        poolExecutor.execute(runnable);
    }

}
