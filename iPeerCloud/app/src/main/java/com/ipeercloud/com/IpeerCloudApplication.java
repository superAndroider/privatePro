package com.ipeercloud.com;

import android.app.Activity;
import android.app.Application;

import com.bugtags.library.Bugtags;

import java.util.Stack;

/**
 * Created by longhengdong on 2017/4/5.
 */
public class IpeerCloudApplication extends Application {

    public static IpeerCloudApplication instance;
    private static Stack<Activity> activityStack;
    //加载so库
    static {
        System.loadLibrary("gcommonapi");
        System.loadLibrary("gfile");
        System.loadLibrary("gproxy");
        System.loadLibrary("gsCloudServer");
    }
    static {
        System.loadLibrary("gsCloudServer");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Bugtags.start("608175721b1fef21d3bff2d9b032c909", this, Bugtags.BTGInvocationEventBubble);
        instance = this;
        // 初始化异常处理类
//        CrashHandler crashHandler = CrashHandler.getInstance();
//        crashHandler.init(this);
    }

    /**
     * 单一实例
     */
    public static IpeerCloudApplication getInstance(){
        if (instance == null) {
            instance = new IpeerCloudApplication();
        }
        return instance;
    }

    /**
     * 添加Activity到堆栈
     */
    public void addActivity(Activity activity) {
        if (activityStack == null) {
            activityStack = new Stack<Activity>();
        }
        activityStack.add(activity);
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityStack!=null) {
            for (int i = 0, size = activityStack.size(); i < size; i++) {
                if (null != activityStack.get(i)) {
                    activityStack.get(i).finish();
                }
            }
            activityStack.clear();
        }
    }

}
