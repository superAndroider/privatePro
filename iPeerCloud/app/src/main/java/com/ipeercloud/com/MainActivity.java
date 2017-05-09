package com.ipeercloud.com;

import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ipeer.imageselect.bean.ImageSet;
import com.ipeer.imageselect.data.DataSource;
import com.ipeer.imageselect.data.OnImagesLoadedListener;
import com.ipeer.imageselect.data.impl.LocalDataSource;
import com.ipeercloud.com.controler.GsFileHelper;
import com.ipeercloud.com.controler.GsJniManager;
import com.ipeercloud.com.controler.GsSocketManager;
import com.ipeercloud.com.httpd.GsHttpd;
import com.ipeercloud.com.model.EventBusEvent.GsProgressEvent;
import com.ipeercloud.com.model.GsCallBack;
import com.ipeercloud.com.model.GsFileModule;
import com.ipeercloud.com.model.GsSimpleResponse;
import com.ipeercloud.com.store.GsDataManager;
import com.ipeercloud.com.utils.GsLog;
import com.ipeercloud.com.utils.UI;
import com.ipeercloud.com.view.activity.BaseAcitivity;
import com.ipeercloud.com.view.activity.GsMediaPlayerActivity;
import com.ipeercloud.com.view.activity.VideoViewActivity;
import com.ipeercloud.com.view.fragment.BaseFragment;
import com.ipeercloud.com.view.fragment.FilesFragment;
import com.ipeercloud.com.view.fragment.HomeFragment;
import com.ipeercloud.com.view.fragment.MediasFragment;
import com.ipeercloud.com.view.fragment.PhotosFragment;
import com.ipeercloud.com.view.fragment.SettingsFragment;
import com.ipeercloud.com.view.service.SyncDownLoadService;
import com.ipeercloud.com.widget.GsProgressDialog;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public class MainActivity extends BaseAcitivity implements OnImagesLoadedListener {

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

    private GsProgressDialog mProgressDialog;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 1) {
                mProgressDialog.setProgress(msg.arg1);
            } else {
                isExit = false;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewUtils.inject(this);
        GsDataManager.getInstance().recoverData();
        initView();
        initFragment();
        String callString = GsSocketManager.getInstance().helloGoonas();
        DataSource dataSource = new LocalDataSource(this);
        dataSource.provideMediaItems(this);//select all images from local database
        //getPhotos();
        testHttpd();
        EventBus.getDefault().register(this);
        isOnLine();
        GsLog.d("onCreate");
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
        mProgressDialog = new GsProgressDialog(this,getString(R.string.gs_uploading));

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        GsLog.d("onNewIntent");
    }

    @Override
    protected void onResume() {
        super.onResume();
        GsLog.d("onResume");
        parseIntent();


    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.getAction() == null) {
            return;
        }
        //处理其他app的调用
        //获取其他app传过来的文件路径
        ClipData data = intent.getClipData();
        String localPath = null;
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
        if (TextUtils.isEmpty(localPath)) {
            return;
        }
        // 解码，中文必须解码
        localPath = Uri.decode(localPath);
        String fileName = GsFileHelper.getFileNameFromLocalPath(localPath);
        String type = GsFileHelper.getFileNameType(fileName);
        GsLog.d("名字： " + fileName + "   type  " + type);
        GsFileModule.FileEntity entity = new GsFileModule.FileEntity();
        entity.FileName = fileName;
        GsDataManager.getInstance().recentFile.addEntity(entity);
        homeFragment.notifyData();
        GsLog.d("上传 " + localPath + "    " + fileName);
        upLoadFile(localPath, fileName);
    }

    private String mUpLoadPath = null;

    /**
     * 将其他app发送过来的文件上传到远端
     */
    private void upLoadFile(String localpath, final String fileName) {
        mUpLoadPath = "\\" + fileName;
        mProgressDialog.show();
        GsJniManager.getInstance().upLoadOneFile(localpath, mUpLoadPath, new GsCallBack<GsSimpleResponse>() {
            @Override
            public void onResult(GsSimpleResponse response) {
                mProgressDialog.dismiss();
                if (response.result) {
                    Toast.makeText(MainActivity.this, fileName + "上传成功", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this, fileName + "上传失败", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Subscribe
    public void updateProgress(GsProgressEvent event) {
        if (event == null || TextUtils.isEmpty(event.remotePath)) {
            return;
        }
        String remoteFolderName = GsFileHelper.getFolderNameFromRemotePath(event.remotePath);
        if (TextUtils.isEmpty(remoteFolderName)) {
            return;
        }
        String fileName = GsFileHelper.getFileNameFromRemotePath(event.remotePath);
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        if (event.remotePath.equals(mUpLoadPath)) {
            GsLog.d("更新缓存文件对话框");
            // 此文件只是缓存文件
            Message message = Message.obtain();
            message.what = 1;
            message.arg1 = event.progress;
            mHandler.sendMessage(message);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        GsLog.d("onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        GsLog.d("onStop");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GsLog.d("onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        GsLog.d("onPause");
        GsDataManager.getInstance().saveDataLocal();
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
//                isOnLine();
                //getPhotos();
                index = 1;
                break;
            case R.id.rl_medias:
                getMedias();
//                testHttpd();
                index = 2;
                break;
            case R.id.rl_files:
                index = 3;
                getAllFiles();
                break;
            case R.id.rl_settings:
                index = 4;
                //testGetFile();
                //testVitamio();
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

    private void testVideo() {
        Intent intent = new Intent(MainActivity.this, GsMediaPlayerActivity.class);
        startActivity(intent);
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
        GsLog.d("开始获取文件");
        GsJniManager.getInstance().getPathFile(GsJniManager.FILE_PARAM, true, new GsCallBack<GsSimpleResponse>() {
            @Override
            public void onResult(GsSimpleResponse response) {
                if (response.result) {
                    filesFragment.notifyData();
                }
            }
        });
    }

    private void getPhotos() {
        GsLog.d("开始获取照片");
        GsJniManager.getInstance().getPathFile(GsJniManager.PHOTO_PARAM, true, new GsCallBack<GsSimpleResponse>() {
            @Override
            public void onResult(GsSimpleResponse response) {
                if (response.result) {
                    GsLog.d("照片数量 = " + GsDataManager.getInstance().photos.fileList.size());
                    downLoad();
                }
            }
        });
    }

    private void getRecentFiles() {

    }

    private void getMedias() {
        GsLog.d("开始获取视频");
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
        new Thread(new Runnable() {
            @Override
            public void run() {
                int reulst = GsSocketManager.getInstance().gsReturnConnectedMode();
                GsLog.d("链接莫斯 "+reulst);
            }
        }).start();


    }

    @Override
    public void onImagesLoaded(List<ImageSet> imageSetList) {

        // TODO: 17/4/26  开启上传同步
//        if (imageSetList == null || imageSetList.size() == 0 || imageSetList.get(0) == null) return;
//        Intent intent = new Intent(this, SyncUploadService.class);
//        intent.putExtra("localPathList", (Serializable) imageSetList.get(0).imageItems);
//        startService(intent);
    }

    // TODO: 17/4/26 下载同步
    private void downLoad() {
        Intent intent = new Intent(this, SyncDownLoadService.class);
        intent.putExtra("remotePathList", (Serializable) GsDataManager.getInstance().photos.fileList);
        startService(intent);

    }

    private void testHttpd() {
        GsHttpd httpServer = new GsHttpd(80080);
        try {
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
            GsLog.d("start 发生了异常");
        }
    }

    private void testVitamio() {
        Intent intent = new Intent(MainActivity.this, VideoViewActivity.class);
        startActivity(intent);
    }

    /**
     * 测试gsReadFileBuffer接口
     */
    private void testGetFile() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                byte buf[] = new byte[1025];
                int len[] = new int[1025];
                len[0] = 1024;//要读取的字节数先放这里
                final int result = GsSocketManager.getInstance().gsReadFileBuffer("\\Medias\\少女时代.mp4", 0, 1024, buf, len);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (result == 0) {
                            Toast.makeText(MainActivity.this, "gsReadFileBuffer 下载成功", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "gsReadFileBuffer 下载失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        }).start();
    }
}
