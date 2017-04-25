package com.ipeercloud.com.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.ipeercloud.com.IpeerCloudApplication;
import com.ipeercloud.com.model.EventBusEvent.GsCameraSyncEvent;
import com.ipeercloud.com.utils.Contants;
import com.ipeercloud.com.utils.SharedPreferencesHelper;
import com.ipeercloud.com.view.activity.CameraSyncActivity;
import com.ipeercloud.com.view.activity.ChangePasswordAcitivity;
import com.ipeercloud.com.view.activity.LinkGoonasAcitivity;
import com.ipeercloud.com.view.activity.LoginAcitivity;
import com.ipeercloud.com.R;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zhy.android.percent.support.PercentRelativeLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


/**
 * Created by longhengdong on 2016/11/16.
 * 个人中心
 */

public class SettingsFragment extends BaseFragment {

    private PercentRelativeLayout prl_exit;
    @ViewInject(R.id.tv_username)
    TextView tv_username;
    @ViewInject(R.id.tv_camera_sync)
    TextView tv_camera_sync;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ViewUtils.inject(this, view);
        EventBus.getDefault().register(this);

        initView(view);
        return view;
    }

    private void initView(View view) {
        String username = SharedPreferencesHelper.getInstance(IpeerCloudApplication.getInstance()).getString(Contants.SP_USERNAME, "");
        tv_username.setText(username);
    }

    @OnClick({(R.id.prl_exit), (R.id.prl_changepwd), (R.id.prl_camera), (R.id.prl_connectcloud), (R.id.prl_addnewcloud), (R.id.prl_clearcache)})
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.prl_exit:                     // 退出登录
                intent = new Intent(SettingsFragment.this.getActivity(), LoginAcitivity.class);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.prl_changepwd:                // 修改密码
                intent = new Intent(SettingsFragment.this.getActivity(), ChangePasswordAcitivity.class);
                startActivity(intent);
                break;
            case R.id.prl_connectcloud:             // 链接私有云
                intent = new Intent(SettingsFragment.this.getActivity(), LinkGoonasAcitivity.class);
                startActivity(intent);
                break;
            case R.id.prl_addnewcloud:              // 添加新的私有云
                break;
            case R.id.prl_clearcache:               // 清除缓存
                break;
            case R.id.prl_camera:               // 相机同步
                startActivity(new Intent(getActivity(), CameraSyncActivity.class));
                break;
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    //相机同步
    @Subscribe(threadMode = ThreadMode.MAIN) //在ui线程执行
    public void onEvent(GsCameraSyncEvent event) {
        Log.i("lxm","event" +event.isOn);

        tv_camera_sync.setText(event.isOn ? getString(R.string.setting_on_sync_camera) : getString(R.string.setting_off_sync_camera));
    }
}
