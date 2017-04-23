package com.ipeercloud.com.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.ipeer.imageselect.ui.ImagesGridActivity;
import com.ipeercloud.com.R;

/**
 * Created by 李小明 on 17/4/23.
 * 邮箱:287907160@qq.com
 */

public class SelectActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        findViewById(R.id.btSelect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SelectActivity.this, ImagesGridActivity.class));
            }
        });

    }
}
