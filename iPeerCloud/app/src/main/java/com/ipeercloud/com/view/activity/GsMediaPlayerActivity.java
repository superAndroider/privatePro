package com.ipeercloud.com.view.activity;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.MediaController;
import android.widget.VideoView;

import com.ipeercloud.com.R;
import com.ipeercloud.com.utils.GsFile;
import com.ipeercloud.com.utils.GsLog;

/**
 * @author 673391138@qq.com
 * @since 17/4/29
 * 主要功能:
 */

public class GsMediaPlayerActivity extends Activity {
    private MediaPlayer mMp;
    private VideoView mVideoView;
    private MediaController mMc;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_play);
        mVideoView = (VideoView) findViewById(R.id.gs_video_view);
        mMc = new MediaController(this);
        mVideoView.setVideoPath(GsFile.getDir().getPath()+"/少女时代.mp4");
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                GsLog.d("发生了错误");
                return true;
            }
        });
        mVideoView.setMediaController(mMc);
        mVideoView.start();
    }
}
