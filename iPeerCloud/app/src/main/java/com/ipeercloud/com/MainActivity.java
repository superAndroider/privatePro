package com.ipeercloud.com;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ipeercloud.com.utils.UI;
import com.ipeercloud.com.view.activity.BaseAcitivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.ipeercloud.com.view.fragment.BaseFragment;
import com.ipeercloud.com.view.fragment.FilesFragment;
import com.ipeercloud.com.view.fragment.HomeFragment;
import com.ipeercloud.com.view.fragment.MediasFragment;
import com.ipeercloud.com.view.fragment.PhotosFragment;
import com.ipeercloud.com.view.fragment.SettingsFragment;

import java.io.File;
import java.io.IOException;

public class MainActivity extends BaseAcitivity {

    private HomeFragment homeFragment;                                      // 首页
    private PhotosFragment photosFragment;                                  // photos
    private MediasFragment mediasFragment;                                  // medias
    private FilesFragment filesFragment;                                    // files
    private SettingsFragment settingsFragment;                              // settings

    public BaseFragment[] fragments;                         // 界面数组
    private static ImageView[] mTabs;                                     // 切换按钮数组
    private static TextView[] mTexts;                                     // 切换按钮数组

    @ViewInject(R.id.btn_home)ImageView btn_home;               // 主页   - 切换按钮
    @ViewInject(R.id.btn_photos)ImageView btn_photos;             // 广场   - 切换按钮
    @ViewInject(R.id.btn_medias)ImageView btn_medias;                   // 通讯录 - 切换按钮
    @ViewInject(R.id.btn_files)ImageView btn_files;                   // 通讯录 - 切换按钮
    @ViewInject(R.id.btn_settings)ImageView btn_settings;                   // 通讯录 - 切换按钮

    @ViewInject(R.id.tv_home)TextView tv_home;                  // 主页   - 切换按钮
    @ViewInject(R.id.tv_photos)TextView tv_photos;            // 幼儿园 - 切换按钮
    @ViewInject(R.id.tv_medias)TextView tv_medias;                // 广场   - 切换按钮
    @ViewInject(R.id.tv_files)TextView tv_files;                      // 通讯录 - 切换按钮
    @ViewInject(R.id.tv_settings)TextView tv_settings;          // 我     - 切换按钮

    @ViewInject(R.id.btn_main)TextView btn_main;                // 操作

    private int index = 0;                                      // 切换标示位
    private int currentTabIndex = 0;                            // 当前位

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        initView();
        initFragement();
//        helloGoonas();
        String callString = helloGoonas();
        Log.e("返回了", "返回的字串：" + callString);
    }

    private void initFragement() {
        homeFragment = new HomeFragment();
        photosFragment = new PhotosFragment();
        mediasFragment = new MediasFragment();
        filesFragment = new FilesFragment();
        settingsFragment = new SettingsFragment();

        fragments = new BaseFragment[] {homeFragment, photosFragment, mediasFragment, filesFragment, settingsFragment};

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, homeFragment)
              /*  .add(R.id.fragment_container, monitorFragment).hide(monitorFragment)
                .add(R.id.fragment_container, photosFragment).hide(photosFragment)
                .add(R.id.fragment_container, mediasFragment).hide(mediasFragment)
                .add(R.id.fragment_container, recruitmentFragment).hide(recruitmentFragment)*/
                .show(homeFragment).commitAllowingStateLoss();
    }

    private void initView() {
        mTabs = new ImageView[] {btn_home, btn_photos, btn_medias, btn_files, btn_settings};
        mTexts = new TextView[] {tv_home, tv_photos, tv_medias, tv_files, tv_settings};
        mTabs[0].setSelected(true);
        UI.setTextColor(mTexts[0], "#0079FF");
        btn_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToOnClick();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    /** button点击事件 **/
    public void onTabSelect(View view) {
        switch (view.getId()) {
            case R.id.rl_home:
                index = 0;
                break;
            case R.id.rl_photos:
                index = 1;
                break;
            case R.id.rl_medias:
                index = 2;
                break;
            case R.id.rl_files:
                index = 3;
                break;
            case R.id.rl_settings:
                index = 4;
                break;
        }
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.fragment_container, fragments[index]);
            }
            trx.show(fragments[index]).commitAllowingStateLoss();
        }
        mTabs[currentTabIndex].setSelected(false);
        UI.setTextColor(mTexts[currentTabIndex], "#989898");
        // 把当前tab设为选中状态
        mTabs[index].setSelected(true);
        UI.setTextColor(mTexts[index], "#0079FF");
        currentTabIndex = index;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean flag = true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                if (!mTabs[0].isSelected()) {
                    if (currentTabIndex != 0) {
                        FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
                        trx.hide(fragments[currentTabIndex]);
                        if (!fragments[0].isAdded()) {
                            trx.add(R.id.fragment_container, fragments[0]);
                        }
                        trx.show(fragments[0]).commit();
                    }
                    mTabs[currentTabIndex].setSelected(false);
                    mTabs[0].setSelected(true);
                    UI.setTextColor(mTexts[currentTabIndex], "#989898");
                    UI.setTextColor(mTexts[0], "#0079FF");
                    currentTabIndex = 0;
                } else {
                    ToQuitTheApp();
                }
            } else {
                flag = super.onKeyDown(keyCode, event);
            }
        }
        return flag;
    }

    Toast toast;
    private boolean isExit = false;                             // 后退键退出标示位
    private void ToQuitTheApp() {
        if (isExit) {
            // ACTION_MAIN with category CATEGORY_HOME 启动主屏幕
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            isExit = false;
            startActivity(intent);
            if (toast!=null) {
                toast.cancel();
            }
        } else {
            isExit = true;
            toast = Toast.makeText(MainActivity.this, "再按一次退出APP", Toast.LENGTH_SHORT);
            toast.show();
            mHandler.sendEmptyMessageDelayed(0, 3000); // 3秒后发送消息
        }
    }


//    public static native String helloGoonas();
//    public static native long GProxyInit(String serverip, int serverport, String u, String password);

    public static native String helloGoonas();
    public static native boolean gsUserRegister(String serverip, String user, String passowrd);
    public static native boolean gsChangePassword(String user, String oldpassword, String newpassword);
    public static native boolean gsresetPassword(String serverip, String user);
    public static native boolean gsLogin(String serverip, String user, String password);
    public static native boolean gsLinked();
    public static native boolean gsOnline();
    public static native boolean gsLinkCloudServer(String CloudServerUuid);

    static {
//		System.loadLibrary("grudp");
		System.loadLibrary("gcommonapi");
		System.loadLibrary("gfile");
        System.loadLibrary("gproxy");
        System.loadLibrary("gsCloudServer");
    }

    private void goToOnClick() {
//        Log.e("测试", "into goToOnClick");
//        final Integer argc = 8089;
//        long backlong = GProxyInit("sz.goonas.com", argc, "2411309415@qq.com", "1818");
//        Log.e("测试", "返回的字串  GProxyInit : " + backlong);

        boolean login = gsLogin("sz.goonas.com", "2411309415@qq.com", "1818");
        Log.e("测试", "返回的字串  gsLogin : " + login);


//        boolean reset = gsResetPassword(Config.serverip, "472596846@qq.com");
//        Log.e("测试", "返回的字串  gsLogin : " + reset);

//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//                boolean reset = gsresetPassword(Config.serverip, "2411309415@qq.com");
//                Log.e("测试", "返回的字串  gsLogin : " + reset);
//            }
//        }.start();
    }



}
