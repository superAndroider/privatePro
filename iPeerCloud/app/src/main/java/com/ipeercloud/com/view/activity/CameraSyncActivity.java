package com.ipeercloud.com.view.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ipeer.widget.switchbutton.SwitchButton;
import com.ipeercloud.com.R;
import com.ipeercloud.com.model.EventBusEvent.GsCameraSyncEvent;
import com.ipeercloud.com.utils.ConstantSP;
import com.ipeercloud.com.utils.GsLog;
import com.ipeercloud.com.utils.SharedPreferencesHelper;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.greenrobot.eventbus.EventBus;


public class CameraSyncActivity extends BaseAcitivity implements CompoundButton.OnCheckedChangeListener, RadioGroup.OnCheckedChangeListener {

    @ViewInject(R.id.camera_sync_switch)
    SwitchButton camera_sync_switch;
    @ViewInject(R.id.sync_video_switch)
    SwitchButton sync_video_switch;
    @ViewInject(R.id.use_celluar_data_switch)
    SwitchButton use_celluar_data_switch;

    @ViewInject(R.id.ll_sync_video)  //视频
            LinearLayout ll_sync_video;
    @ViewInject(R.id.tv_sync_video_detail)//视频描述
            TextView tv_sync_video_detail;

    @ViewInject(R.id.ll_use_cellular_data)//蜂窝数据
            LinearLayout ll_use_cellular_data;
    @ViewInject(R.id.tv_use_cellular_data_detail)//蜂窝数据详情
            TextView tv_use_cellular_data_detail;
    @ViewInject(R.id.rg_camera_sync)  //保存時間
            RadioGroup rg_camera_sync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_sync);
        ViewUtils.inject(this);
        initView();

    }

    private void initView() {
        boolean cameraSyncChecked = SharedPreferencesHelper.getInstance(this).getBoolean(ConstantSP.SP_CAMERA_SYNC, false);
        camera_sync_switch.setChecked(cameraSyncChecked);
        initCheckedStatus(cameraSyncChecked);
        rg_camera_sync.setOnCheckedChangeListener(this);

        camera_sync_switch.setOnCheckedChangeListener(this);
        sync_video_switch.setOnCheckedChangeListener(this);
        use_celluar_data_switch.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.camera_sync_switch:
                GsLog.d("相机 = " + b);
                EventBus.getDefault().post(new GsCameraSyncEvent(b));
                SharedPreferencesHelper.getInstance(this).setInt(ConstantSP.SP_PHOTO_SAVE_TIME, 0);
                SharedPreferencesHelper.getInstance(this).setBoolean(ConstantSP.SP_SYNC_VIDEO, false);
                SharedPreferencesHelper.getInstance(this).setBoolean(ConstantSP.SP_USE_CELLULAR_DATA, false);

                SharedPreferencesHelper.getInstance(this).setBoolean(ConstantSP.SP_CAMERA_SYNC, b);
                initCheckedStatus(b);
                break;
            case R.id.sync_video_switch:
                SharedPreferencesHelper.getInstance(this).setBoolean(ConstantSP.SP_SYNC_VIDEO, b);
                GsLog.d("视频 = " + b);
                break;
            case R.id.use_celluar_data_switch:
                SharedPreferencesHelper.getInstance(this).setBoolean(ConstantSP.SP_USE_CELLULAR_DATA, b);
                GsLog.d("网络 = " + b);
                break;
        }
    }

    private void initCheckedStatus(boolean cameraSyncChecked) {
        if (cameraSyncChecked) {
            ll_sync_video.setVisibility(View.VISIBLE);
            tv_sync_video_detail.setVisibility(View.VISIBLE);
            ll_use_cellular_data.setVisibility(View.VISIBLE);
            tv_use_cellular_data_detail.setVisibility(View.VISIBLE);
            rg_camera_sync.setVisibility(View.VISIBLE);
        } else {
            ll_sync_video.setVisibility(View.GONE);
            tv_sync_video_detail.setVisibility(View.GONE);
            ll_use_cellular_data.setVisibility(View.GONE);
            tv_use_cellular_data_detail.setVisibility(View.GONE);
            rg_camera_sync.setVisibility(View.GONE);
        }

        sync_video_switch.setChecked(SharedPreferencesHelper.getInstance(this).getBoolean(ConstantSP.SP_SYNC_VIDEO, false));
        use_celluar_data_switch.setChecked(SharedPreferencesHelper.getInstance(this).getBoolean(ConstantSP.SP_USE_CELLULAR_DATA, false));
        int checkedPosition = SharedPreferencesHelper.getInstance(this).getInt(ConstantSP.SP_PHOTO_SAVE_TIME, 0);
        switch (checkedPosition) {
            case 0:
                rg_camera_sync.check(R.id.rb_one_day);
                break;
            case 1:
                rg_camera_sync.check(R.id.rb_one_month);
                break;
            case 2:
                rg_camera_sync.check(R.id.rb_one_year);
                break;
        }
    }


    //保存照片时间设置
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, @IdRes int i) {
        switch (i) {
            case R.id.rb_one_day:
                SharedPreferencesHelper.getInstance(this).setInt(ConstantSP.SP_PHOTO_SAVE_TIME, 0);
                GsLog.d("RadioGroup changed = " + 0);
                break;
            case R.id.rb_one_month:
                SharedPreferencesHelper.getInstance(this).setInt(ConstantSP.SP_PHOTO_SAVE_TIME, 1);
                GsLog.d("RadioGroup changed = " + 1);
                break;
            case R.id.rb_one_year:
                SharedPreferencesHelper.getInstance(this).setInt(ConstantSP.SP_PHOTO_SAVE_TIME, 2);
                GsLog.d("RadioGroup changed = " + 2);
                break;

        }
    }
}
