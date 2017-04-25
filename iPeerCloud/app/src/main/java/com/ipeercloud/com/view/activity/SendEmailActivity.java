package com.ipeercloud.com.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.ipeercloud.com.R;

/**
 * Created by 李小明 on 17/4/25.
 * 邮箱:287907160@qq.com
 */

public class SendEmailActivity extends BaseAcitivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_email);

        sendEmail("lixiaoming0314@163.com", "iPeerCloud", "很不错的一款软件,赶紧加入吧!");
    }

    private void sendEmail(String toEmail, String title, String content) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        //设置文本格式
        emailIntent.setType("text/plain");
        //设置对方邮件地址
        emailIntent.putExtra(Intent.EXTRA_EMAIL, toEmail);
        //设置标题内容
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, title);
        //设置邮件文本内容
        emailIntent.putExtra(Intent.EXTRA_TEXT, content);
        startActivity(Intent.createChooser(emailIntent, "Choose Email Client"));

    }
}
