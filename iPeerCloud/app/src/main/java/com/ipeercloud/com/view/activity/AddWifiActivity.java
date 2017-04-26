package com.ipeercloud.com.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ipeer.widget.switchbutton.SwitchButton;
import com.ipeercloud.com.R;
import com.ipeercloud.com.controler.GsJniManager;
import com.ipeercloud.com.model.EventBusEvent.GsCameraSyncEvent;
import com.ipeercloud.com.utils.ConstantSP;
import com.ipeercloud.com.utils.SharedPreferencesHelper;
import com.ipeercloud.com.utils.network.NetworkUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.greenrobot.eventbus.EventBus;


public class AddWifiActivity extends BaseAcitivity implements View.OnClickListener {

    @ViewInject(R.id.tv_ssid_add_activity)
    TextView tv_ssid_add_activity;  //SSID
    @ViewInject(R.id.tv_add_wifi)
    TextView tv_add_wifi;//添加按钮
    @ViewInject(R.id.et_password_wifi)
    EditText et_password_wifi; //WIFI 密码

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wifi);
        ViewUtils.inject(this);
        initView();
        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));

    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i("lxm", "ssid = " + NetworkUtils.getWifiSSID(this));
        tv_ssid_add_activity.setText(NetworkUtils.getWifiSSID(this));
    }

    private void initView() {
        tv_add_wifi.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        GsJniManager.getInstance().addWIFI(tv_ssid_add_activity.getText().toString(), et_password_wifi.getText().toString());
    }
}
