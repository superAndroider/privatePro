package com.ipeercloud.com.view.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.ipeercloud.com.R;
import com.ipeercloud.com.utils.GsLog;

/**
 * @author 673391138@qq.com
 * @since 17/5/10
 * 主要功能:
 */

public class TempActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        Intent intent = new Intent(this,VideoViewActivity.class);
        intent.putExtra("path",getIntent().getStringExtra("path"));
        startActivityForResult(intent,100);

    }
    public static void startActivity(Context context, String fileName){
        Intent intent = new Intent(context,TempActivity.class);
        intent.putExtra("path",fileName);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        GsLog.d("onActivityResult");
        finish();
    }
}
