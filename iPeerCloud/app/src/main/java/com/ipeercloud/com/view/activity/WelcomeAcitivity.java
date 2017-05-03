package com.ipeercloud.com.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.TextView;

import com.ipeercloud.com.MainActivity;
import com.ipeercloud.com.R;
import com.ipeercloud.com.controler.GsJniManager;
import com.ipeercloud.com.model.GsCallBack;
import com.ipeercloud.com.model.GsSimpleResponse;
import com.ipeercloud.com.utils.GsConfig;
import com.ipeercloud.com.utils.GsSp;


public class WelcomeAcitivity extends BaseAcitivity {

    TextView tv_signup;
    TextView tv_login;

    private static final int MSG_GO_HOME = 1;
    private static final int MSG_GO_LOGIN = 2;

    public static final int SPLASH_SLEEPTIME = 2000;  // 欢迎界面进入首页停留时间

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent;
            switch (msg.what) {
                case MSG_GO_HOME:
                    intent = new Intent(WelcomeAcitivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case MSG_GO_LOGIN:
                    intent = new Intent(WelcomeAcitivity.this, AccountAcitivity.class);
                    startActivity(intent);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

    }

    private boolean autoLogin() {
        String email = GsSp.getInstance().getString("email");
        String passWord = GsSp.getInstance().getString("passWord");
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(passWord)) {
            //自动登录
            GsJniManager.getInstance().login(GsConfig.serverip, email, passWord, new GsCallBack<GsSimpleResponse>() {
                @Override
                public void onResult(GsSimpleResponse response) {
                    afterLogin(response.result);
                }
            });
            return true;
        }
        return false;
    }

    private void afterLogin(boolean loginSuccess) {
        cancelLoadingDialog();
        if (loginSuccess) {
            Intent intent = new Intent(WelcomeAcitivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(WelcomeAcitivity.this, LoginAcitivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!autoLogin()) {
            mHandler.sendEmptyMessageDelayed(MSG_GO_LOGIN, SPLASH_SLEEPTIME);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeMessages(MSG_GO_LOGIN);
            mHandler.removeMessages(MSG_GO_HOME);
        }
    }


}
