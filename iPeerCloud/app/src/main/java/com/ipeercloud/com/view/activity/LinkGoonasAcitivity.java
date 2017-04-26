package com.ipeercloud.com.view.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ipeercloud.com.R;
import com.ipeercloud.com.controler.GsSocketManager;
import com.ipeercloud.com.utils.GsLog;
import com.ipeercloud.com.zxing.ToolbarCaptureActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.HashMap;
import java.util.Map;

public class LinkGoonasAcitivity extends BaseAcitivity {

    @ViewInject(R.id.btn_back)
    PercentRelativeLayout btn_back;       //返回键
    @ViewInject(R.id.tv_show_linkstate)
    TextView tv_show_linkstate;                // 显示连接状态
    @ViewInject(R.id.edit_linkstate)
    EditText edit_linkstate;                // 手动输入编号
    @ViewInject(R.id.btn_create)
    TextView btn_create;                        // Link按钮

    @ViewInject(R.id.ll_linkstate)
    LinearLayout ll_linkstate;                //连接状态

    @ViewInject(R.id.rl_popuview)             //弹出框
            RelativeLayout rl_popuview;
    @ViewInject(R.id.btn_scancode)
    TextView btn_scancode;                    // 点击扫描二维码
    @ViewInject(R.id.btn_enteruuid)
    TextView btn_enteruuid;                  // 点击手动输入uuid
    @ViewInject(R.id.btn_cancell)
    TextView btn_cancell;                      // 弹出框取消

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linkgoonas);
        ViewUtils.inject(this);
        initView();

    }

    private void initView() {
        btn_back = (PercentRelativeLayout) findViewById(R.id.btn_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rl_popuview.setVisibility(View.GONE);
        new Thread() {
            @Override
            public void run() {
                super.run();
                boolean islink = GsSocketManager.getInstance().gsLinked();
                Map<String, Object> map = new HashMap();
                map.put("islink", islink);

                GsLog.d("LinkGoonasActivity isLink = " + islink);
//                map.put("emailStr", emailStr);
//                map.put("passwordStr", passwordStr);

                Message message = new Message();
                message.what = MSG_ISLINK;
                message.obj = map;
                mHandler.sendMessage(message);
            }
        }.start();

    }

    //    boolean islink = false;
    private final static int MSG_ISLINK = 111;
    private final static int MSG_LINK = 112;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_ISLINK:
                    cancelLoadingDialog();
                    Map<String, Object> map1 = (Map<String, Object>) msg.obj;
                    boolean islink = (boolean) map1.get("islink");

                    if (islink) { //已綁定
                        tv_show_linkstate.setText(LinkGoonasAcitivity.this.getResources().getString(R.string.linked));
                        btn_create.setTextColor(getResources().getColor(R.color.btg_global_gray));

                    } else {//未綁定
                        tv_show_linkstate.setText(LinkGoonasAcitivity.this.getResources().getString(R.string.unlinked));
                        ll_linkstate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                rl_popuview.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                    break;
                case MSG_LINK:
                    cancelLoadingDialog();
                    Map<String, Object> map2 = (Map<String, Object>) msg.obj;
                    boolean linkcloud = (boolean) map2.get("linkcloud");
                    GsLog.d("MSG_LINK = " + linkcloud);
                    if (linkcloud) {
                        Toast.makeText(LinkGoonasAcitivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(LinkGoonasAcitivity.this, "连接失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @OnClick({(R.id.btn_scancode), (R.id.btn_enteruuid), (R.id.btn_cancell), (R.id.btn_create)})
    public void onClick(View view) {
        rl_popuview.setVisibility(View.GONE);
        switch (view.getId()) {
            case R.id.btn_scancode:                     // 扫描二维码
                edit_linkstate.setVisibility(View.GONE);
                new IntentIntegrator(LinkGoonasAcitivity.this).setCaptureActivity(ToolbarCaptureActivity.class).initiateScan();
                break;
            case R.id.btn_enteruuid:                    // 手动输入uuid
//                intent = new Intent(LinkGoonasAcitivity.this, ToolbarCaptureActivity.class);
//                startActivity(intent);
//                edit_linkstate.setVisibility(View.VISIBLE);
//                edit_linkstate.setFocusable(true);
//                edit_linkstate.setFocusableInTouchMode(true);
                showSoftInputFromWindow(this,edit_linkstate);
                break;
            case R.id.btn_cancell:                      // 取消
                edit_linkstate.setVisibility(View.GONE);
                break;

            case R.id.btn_create:                      // 连接
                String subString;
                if (edit_linkstate.getVisibility() == View.VISIBLE) {
                    subString = edit_linkstate.getText().toString();
                } else {
                    subString = tv_show_linkstate.getText().toString();
                }

                link(subString);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                GsLog.d("Cancelled scan");
            } else {
                GsLog.d("Scanned");
                final String subString = result.getContents();
                // 验证
                Toast.makeText(LinkGoonasAcitivity.this, "扫描结果：" + subString, Toast.LENGTH_SHORT).show();
                tv_show_linkstate.setText(subString);
            }
        } else {
            GsLog.d("Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    //连接模块驱动
    private void link(final String subString) {
        showLoadingDialog("正在绑定...");
        new Thread() {
            @Override
            public void run() {
                super.run();
                boolean linkcloud = GsSocketManager.getInstance().gsLinkCloudServer(subString);
                Message message = new Message();

                Map<String, Object> map = new HashMap();
                map.put("linkcloud", linkcloud);
//                        map.put("emailStr", emailStr);
//                        map.put("passwordStr", passwordStr);

                message.what = MSG_LINK;
                message.obj = map;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.setVisibility(View.VISIBLE);
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
    }

}
