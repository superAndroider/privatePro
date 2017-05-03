package com.ipeercloud.com.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ipeercloud.com.R;


/**
 * Created by lixiaoming on 16/4/27.
 */
public class CustomPopWindow extends PopupWindow implements View.OnClickListener {

    private TextView tvAllPopWindow;
    private TextView tvDayPopWindow;
    private TextView tvMonthPopWindow;
    private TextView tvYearPopWindow;
    private View mMenuView;

    public CustomPopWindow(Activity context, View.OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.custom_popwindow, null);
        mMenuView.setOnClickListener(this);
        tvAllPopWindow = (TextView) mMenuView.findViewById(R.id.tvAllPopWindow);
        tvDayPopWindow = (TextView) mMenuView.findViewById(R.id.tvDayPopWindow);
        tvMonthPopWindow = (TextView) mMenuView.findViewById(R.id.tvMonthPopWindow);
        tvYearPopWindow = (TextView) mMenuView.findViewById(R.id.tvYearPopWindow);
        tvAllPopWindow.setOnClickListener(itemsOnClick);
        tvDayPopWindow.setOnClickListener(itemsOnClick);
        tvMonthPopWindow.setOnClickListener(itemsOnClick);
        tvYearPopWindow.setOnClickListener(itemsOnClick);

        //设置SelectPicPopupWindow的View
        setContentView(mMenuView);
        //设置SelectPicPopupWindow弹出窗体的宽
        setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
//        //设置SelectPicPopupWindow弹出窗体的高
        setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        ColorDrawable dw = new ColorDrawable(0x00000000);
        setBackgroundDrawable(dw);
        setFocusable(true);
        setAnimationStyle(R.style.popwindow_anim_style);
    }

    @Override
    public void onClick(View v) {
        dismiss();
    }
}
