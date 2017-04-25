package com.ipeercloud.com.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.ipeercloud.com.R;
import com.ipeercloud.com.controler.GsSocketManager;
import com.ipeercloud.com.zxing.ToolbarCaptureActivity;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zhy.android.percent.support.PercentRelativeLayout;

import java.util.HashMap;
import java.util.Map;

public class LinkGoonasAcitivity extends BaseAcitivity {

    @ViewInject(R.id.btn_back)
    PercentRelativeLayout btn_back;
    @ViewInject(R.id.edit_linkstate)
    TextView edit_linkstate;                // 连接状态
    @ViewInject(R.id.btn_create)
    TextView btn_create;                        // 修改

    @ViewInject(R.id.ll_linkstate)
    LinearLayout ll_linkstate;                //

    @ViewInject(R.id.rl_popuview)
    RelativeLayout rl_popuview;                //
    @ViewInject(R.id.btn_scancode)
    TextView btn_scancode;                    // 扫描二维码
    @ViewInject(R.id.btn_enteruuid)
    TextView btn_enteruuid;                  // 手动输入uuid
    @ViewInject(R.id.btn_cancell)
    TextView btn_cancell;                      // 取消

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

                Log.i("lxm", "LinkGoonasActivity isLink = " + islink);
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
                        edit_linkstate.setText(LinkGoonasAcitivity.this.getResources().getString(R.string.linked));
                        btn_create.setTextColor(getResources().getColor(R.color.btg_global_gray));

                    } else {//未綁定
                        edit_linkstate.setText(LinkGoonasAcitivity.this.getResources().getString(R.string.unlinked));
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
                    Log.i("lxm", "MSG_LINK = " + linkcloud);
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

    @OnClick({(R.id.btn_scancode), (R.id.btn_enteruuid), (R.id.btn_cancell)})
    public void onClick(View view) {
        rl_popuview.setVisibility(View.GONE);
        Intent intent;
        switch (view.getId()) {
            case R.id.btn_scancode:                     // 扫描二维码
                new IntentIntegrator(LinkGoonasAcitivity.this).setCaptureActivity(ToolbarCaptureActivity.class).initiateScan();
                break;
            case R.id.btn_enteruuid:                    // 手动输入uuid
                intent = new Intent(LinkGoonasAcitivity.this, ToolbarCaptureActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_cancell:                      // 取消
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
                Log.d("MainActivity", "Cancelled scan");
            } else {
                Log.d("MainActivity", "Scanned");
                final String subString = result.getContents();
                // 验证
                Toast.makeText(LinkGoonasAcitivity.this, "扫描结果：" + subString, Toast.LENGTH_SHORT).show();

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
        } else {
            Log.d("MainActivity", "Weird");
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


}
