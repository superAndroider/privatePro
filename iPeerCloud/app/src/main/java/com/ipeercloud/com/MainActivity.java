package com.ipeercloud.com;

import android.content.ClipData;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ipeercloud.com.controler.GsFileHelper;
import com.ipeercloud.com.controler.GsJniManager;
import com.ipeercloud.com.controler.GsSocketManager;
import com.ipeercloud.com.model.GsCallBack;
import com.ipeercloud.com.model.GsFileModule;
import com.ipeercloud.com.model.GsSimpleResponse;
import com.ipeercloud.com.store.GsDataManager;
import com.ipeercloud.com.utils.GsLog;
import com.ipeercloud.com.utils.UI;
import com.ipeercloud.com.view.activity.BaseAcitivity;
import com.ipeercloud.com.view.fragment.BaseFragment;
import com.ipeercloud.com.view.fragment.FilesFragment;
import com.ipeercloud.com.view.fragment.HomeFragment;
import com.ipeercloud.com.view.fragment.MediasFragment;
import com.ipeercloud.com.view.fragment.PhotosFragment;
import com.ipeercloud.com.view.fragment.SettingsFragment;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class MainActivity extends BaseAcitivity {

    private HomeFragment homeFragment;                                      // 首页
    private PhotosFragment photosFragment;                                  // photos
    private MediasFragment mediasFragment;                                  // medias
    private FilesFragment filesFragment;                                    // files
    private SettingsFragment settingsFragment;                              // settings

    public BaseFragment[] fragments;                         // 界面数组
    private static ImageView[] mTabs;                                     // 切换按钮数组
    private static TextView[] mTexts;                                     // 切换按钮数组

    @ViewInject(R.id.btn_home)
    ImageView btn_home;               // 主页   - 切换按钮
    @ViewInject(R.id.btn_photos)
    ImageView btn_photos;             // 广场   - 切换按钮
    @ViewInject(R.id.btn_medias)
    ImageView btn_medias;                   // 通讯录 - 切换按钮
    @ViewInject(R.id.btn_files)
    ImageView btn_files;                   // 通讯录 - 切换按钮
    @ViewInject(R.id.btn_settings)
    ImageView btn_settings;                   // 通讯录 - 切换按钮

    @ViewInject(R.id.tv_home)
    TextView tv_home;                  // 主页   - 切换按钮
    @ViewInject(R.id.tv_photos)
    TextView tv_photos;            // 幼儿园 - 切换按钮
    @ViewInject(R.id.tv_medias)
    TextView tv_medias;                // 广场   - 切换按钮
    @ViewInject(R.id.tv_files)
    TextView tv_files;                      // 通讯录 - 切换按钮
    @ViewInject(R.id.tv_settings)
    TextView tv_settings;          // 我     - 切换按钮

    @ViewInject(R.id.btn_main)
    TextView btn_main;                // 操作

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
        initFragment();
        String callString = GsSocketManager.getInstance().helloGoonas();
        GsLog.d("返回了返回的字串：" + callString);
    }

    private void initFragment() {
        homeFragment = new HomeFragment();
        photosFragment = new PhotosFragment();
        mediasFragment = new MediasFragment();
        filesFragment = new FilesFragment();
        settingsFragment = new SettingsFragment();

        fragments = new BaseFragment[]{homeFragment, photosFragment, mediasFragment, filesFragment, settingsFragment};

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_container, homeFragment)
              /*  .add(R.id.fragment_container, monitorFragment).hide(monitorFragment)
                .add(R.id.fragment_container, photosFragment).hide(photosFragment)
                .add(R.id.fragment_container, mediasFragment).hide(mediasFragment)
                .add(R.id.fragment_container, recruitmentFragment).hide(recruitmentFragment)*/
                .show(homeFragment).commitAllowingStateLoss();
    }

    private void initView() {
        mTabs = new ImageView[]{btn_home, btn_photos, btn_medias, btn_files, btn_settings};
        mTexts = new TextView[]{tv_home, tv_photos, tv_medias, tv_files, tv_settings};
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        GsLog.d("onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        parseIntent();


    }
    private void parseIntent(){
        Intent intent = getIntent();
        if (intent.getAction() == null) {
            return;
        }
        //处理其他app的调用
        //获取其他app传过来的文件路径
        ClipData data = intent.getClipData();
        String localPath= null;
        if (data == null) {
            return;
        } else {
            int size = data.getItemCount();
            if (size > 0) {
                ClipData.Item item = data.getItemAt(0);
                if (item != null && item.getUri() != null) {
                    localPath = item.getUri().getEncodedPath();
                    GsLog.d("uriString" + localPath);
                }
            }

        }
       if(TextUtils.isEmpty(localPath)){
            return;
        }
       String fileName = GsFileHelper.getFileNameFromPath(localPath);
        String type  = GsFileHelper.getFileNameType(fileName);
        GsLog.d("名字： "+fileName+"   type  "+type);
        GsDataManager.getInstance().recentFile.addEntity(new GsFileModule.FileEntity());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * button点击事件
     **/
    public void onTabSelect(View view) {
        switch (view.getId()) {
            case R.id.rl_home:
                index = 0;
                getRecentFiles();
                break;
            case R.id.rl_photos:
                isOnLine();
                index = 1;
                break;
            case R.id.rl_medias:
                getMedias();
                index = 2;
                break;
            case R.id.rl_files:
                index = 3;
                getAllFiles();
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
        fragments[index].resetData();
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
            if (toast != null) {
                toast.cancel();
            }
        } else {
            isExit = true;
            toast = Toast.makeText(MainActivity.this, "再按一次退出APP", Toast.LENGTH_SHORT);
            toast.show();
            mHandler.sendEmptyMessageDelayed(0, 3000); // 3秒后发送消息
        }
    }


    private void goToOnClick() {

//        boolean login = GsSocketManager.getInstance().gsLogin("sz.goonas.com", "2411309415@qq.com", "1818");
//        GsLog.d("测试 返回的字串  gsLogin : " + login);

    }

    private void register() {
        GsJniManager.getInstance().register("sz.goonas.com", "2411309415@qq.com", "181818", new GsCallBack<GsSimpleResponse>() {
            @Override
            public void onResult(GsSimpleResponse response) {
                GsLog.d("测试 返回的字串  gsUserRegister : " + response.result);
            }
        });
    }

    private void getAllFiles() {
        GsJniManager.getInstance().getPathFile(GsJniManager.FILE_PARAM, true, new GsCallBack<GsSimpleResponse>() {
            @Override
            public void onResult(GsSimpleResponse response) {
                if (response.result) {
                    filesFragment.notifyData();
                }
            }
        });
    }

    private void getRecentFiles() {

    }

    private void getMedias() {
        GsJniManager.getInstance().getPathFile(GsJniManager.MEDIA_PARAM, true, new GsCallBack<GsSimpleResponse>() {
            @Override
            public void onResult(GsSimpleResponse response) {
                if (response.result) {
                    mediasFragment.notifyData();
                }
            }
        });
    }

    private void isOnLine() {
        GsJniManager.getInstance().isOnline(new GsCallBack<GsSimpleResponse>() {
            @Override
            public void onResult(GsSimpleResponse response) {
                GsLog.d("是否在线 " + response.result);
            }
        });
        GsJniManager.getInstance().isLink(new GsCallBack<GsSimpleResponse>() {
            @Override
            public void onResult(GsSimpleResponse response) {
                GsLog.d("是否链接 " + response.result);
            }
        });
       /* GsJniManager.getInstance().getPathFile(GsJniManager.MEDIA_PARAM, new GsCallBack<GsSimpleResponse>() {
            @Override
            public void onResult(GsSimpleResponse response) {

            }
        });
        GsJniManager.getInstance().getPathFile(GsJniManager.SHARE_PARAM, new GsCallBack<GsSimpleResponse>() {
            @Override
            public void onResult(GsSimpleResponse response) {

            }
        });*/


    }
}
