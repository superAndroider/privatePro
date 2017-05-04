package com.ipeercloud.com.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ipeercloud.com.R;
import com.ipeercloud.com.controler.GsSocketManager;
import com.ipeercloud.com.utils.GsConfig;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.HashMap;
import java.util.Map;

public class SignUpAcitivity extends BaseAcitivity {

    @ViewInject(R.id.btn_back)PercentRelativeLayout btn_back;
    @ViewInject(R.id.edit_email)EditText edit_email;                // 邮箱输入
    @ViewInject(R.id.edit_password)EditText edit_password;          // 密码输入
    @ViewInject(R.id.btn_create)TextView btn_create;                // 创建

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ViewUtils.inject(this);
        initView();

    }

    private void initView() {
        btn_back = (PercentRelativeLayout) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                Intent intent = new Intent(SignUpAcitivity.this, AccountAcitivity.class);
                startActivity(intent);
                finish();
            }
        });
        btn_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailStr = edit_email.getText().toString();
                final String passwordStr = edit_password.getText().toString();
                if (TextUtils.isEmpty(emailStr)) {
                    Toast.makeText(SignUpAcitivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(passwordStr)) {
                    Toast.makeText(SignUpAcitivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                showLoadingDialog("注册中...");
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        boolean register = GsSocketManager.getInstance().gsUserRegister(GsConfig.serverip, emailStr, passwordStr);
                        Message message = new Message();

                        Map<String, Object> map = new HashMap();
                        map.put("register", register);
                        map.put("emailStr", emailStr);
                        map.put("passwordStr", passwordStr);

                        message.what = MSG_LOGIN;
                        message.obj = map;
                        mHandler.sendMessage(message);
                    }
                }.start();

            }
        });
    }

    private final static int MSG_LOGIN = 111;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_LOGIN:
                    cancelLoadingDialog();
                    Map<String, Object> map = (Map<String, Object>) msg.obj;
                    boolean register = (boolean) map.get("register");
                    String emailStr = (String) map.get("emailStr");
                    String passwordStr = (String) map.get("passwordStr");

                    if (register) {
                        Toast.makeText(SignUpAcitivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpAcitivity.this, LoginAcitivity.class);
                        intent.putExtra("username", emailStr);
                        intent.putExtra("password", passwordStr);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(SignUpAcitivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

}
