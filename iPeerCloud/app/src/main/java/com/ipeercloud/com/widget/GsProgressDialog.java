package com.ipeercloud.com.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ipeercloud.com.R;

/**
 * @author 673391138@qq.com
 * @since 17/5/6
 * 主要功能:
 */

public class GsProgressDialog extends AlertDialog {
    private ProgressBar mPb;
    private TextView mMessageTv;
    private String message;

    public GsProgressDialog(Context context) {
        super(context);
    }
    public GsProgressDialog(Context context,String message) {
        super(context);
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_progress);
        mPb = (ProgressBar) findViewById(R.id.dialog_pb);
        mMessageTv = (TextView) findViewById(R.id.pb_message);
        mMessageTv.setText(message);
        this.setCancelable(false);

    }

    @Override
    public void show() {
//        mPb.setProgress(0);
//        mMessageTv.setText(message+0+"%");
        super.show();
    }

    public void setText(String s){
        mMessageTv.setText(s);
    }
    public void setProgress(int progress) {
        mPb.setProgress(progress);
        mMessageTv.setText(message+progress+"%");
    }
}
