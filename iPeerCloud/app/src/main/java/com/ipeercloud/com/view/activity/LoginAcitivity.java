package com.ipeercloud.com.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ipeercloud.com.IpeerCloudApplication;
import com.ipeercloud.com.MainActivity;
import com.ipeercloud.com.R;
import com.ipeercloud.com.controler.GsJniManager;
import com.ipeercloud.com.controler.GsSocketManager;
import com.ipeercloud.com.model.GsCallBack;
import com.ipeercloud.com.model.GsSimpleResponse;
import com.ipeercloud.com.utils.Contants;
import com.ipeercloud.com.utils.GsConfig;
import com.ipeercloud.com.utils.GsSp;
import com.ipeercloud.com.utils.SharedPreferencesHelper;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

import java.util.HashMap;
import java.util.Map;

public class LoginAcitivity extends BaseAcitivity {

    TextView tv_back;
    TextView btn_login;
    ImageView btn_back;

    @ViewInject(R.id.edit_email)
    EditText edit_email;                // 邮箱输入
    @ViewInject(R.id.edit_password)
    EditText edit_password;          // 密码输入

    private String username = "";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ViewUtils.inject(this);
        initView();

    }

    private void initView() {
        //edit_email.setText("2411309415@qq.com");
        //edit_password.setText("181818");

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null) {
            username = intent.getExtras().getString("username");
            password = intent.getExtras().getString("password");
            edit_email.setText(username);
            edit_password.setText(password);
        }

        btn_login = (TextView) findViewById(R.id.btn_login);
        tv_back = (TextView) findViewById(R.id.tv_back);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailStr = edit_email.getText().toString();
                final String passwordStr = edit_password.getText().toString();
                if (TextUtils.isEmpty(emailStr)) {
                    Toast.makeText(LoginAcitivity.this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(passwordStr)) {
                    Toast.makeText(LoginAcitivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }

                showLoadingDialog("登录中...");
                // 登录成功后，第二次登录会失败
                new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        boolean isOnline = GsSocketManager.getInstance().gsOnline();
                        Map<String, Object> map = new HashMap();
                        map.put("isOnline", isOnline);
                        map.put("emailStr", emailStr);
                        map.put("passwordStr", passwordStr);

                        username = emailStr;
                        Message message = new Message();
                        message.what = MSG_ONLINE;
                        message.obj = map;
                        mHandler.sendMessage(message);
                    }
                }.start();

            }
        });

        btn_back = (ImageView) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //finish();
                Intent intent = new Intent(LoginAcitivity.this, AccountAcitivity.class);
                startActivity(intent);
                finish();
            }
        });

        String callString = GsSocketManager.getInstance().helloGoonas();
        Log.e("返回了", "返回的字串：" + callString);
    }

    private void startMainActivity() {
        Intent intent = new Intent(LoginAcitivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private final static int MSG_LOGIN = 111;
    private final static int MSG_ONLINE = 112;

    private void afterLogin(boolean loginSuccess) {
        cancelLoadingDialog();
        if (loginSuccess) {
            SharedPreferencesHelper.getInstance(IpeerCloudApplication.getInstance()).setString(Contants.SP_USERNAME, username);
            Intent intent = new Intent(LoginAcitivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(LoginAcitivity.this, "登录失败", Toast.LENGTH_SHORT).show();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Intent intent;
            switch (msg.what) {
                case MSG_LOGIN:

                    break;
                case MSG_ONLINE:
                    Map<String, Object> map = (Map<String, Object>) msg.obj;
                    boolean isOnline = (boolean) map.get("isOnline");
                    final String emailStr = (String) map.get("emailStr");
                    final String passwordStr = (String) map.get("passwordStr");

                    if (isOnline) {
                        cancelLoadingDialog();
                        intent = new Intent(LoginAcitivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        GsJniManager.getInstance().login(GsConfig.serverip, emailStr, passwordStr, new GsCallBack<GsSimpleResponse>() {
                            @Override
                            public void onResult(GsSimpleResponse response) {
                                //存储邮箱密码
                                GsSp.getInstance().putString("email",emailStr);
                                GsSp.getInstance().putString("passWord",passwordStr);
                                afterLogin(response.result);
                            }
                        });
                    }
                    break;
            }
        }
    };


}
