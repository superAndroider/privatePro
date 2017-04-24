package com.ipeercloud.com.widget;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.ipeercloud.com.R;

/**
 * @author 673391138@qq.com
 * @since 17/4/21
 * 主要功能:
 */

public class GsFullPop extends GsAbsFullPop {
    private TextView mDownloadBtn;
    private TextView mCancelBtn;
    private View.OnClickListener mDownloadListener;

    public GsFullPop(Activity activity) {
        super(activity);
    }

    @Override
    protected boolean onShowPrepare(View rootView) {
        mDownloadBtn = (TextView) rootView.findViewById(R.id.btn_down);
        mCancelBtn = (TextView) rootView.findViewById(R.id.btn_cancel_down);
        if (mDownloadListener != null) {
            mDownloadBtn.setOnClickListener(mDownloadListener);
        }
        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return true;
    }

    @Override
    protected int getMenuLayout() {
        return R.layout.pop_download;
    }

    public void setDownloadClickListener(View.OnClickListener listener) {
        this.mDownloadListener = listener;
    }
}
