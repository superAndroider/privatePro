package com.ipeercloud.com.view.fragment;

import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


public abstract class BaseFragment extends Fragment {

    public LayoutInflater mInflater;
    private final static String TAG = "FragmentBase";
    private Handler handler = new Handler();

    public void runOnWorkThread(Runnable action) {
        new Thread(action).start();
    }

    public void runOnUiThread(Runnable action) {
        handler.post(action);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = LayoutInflater.from(getActivity());

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public BaseFragment() { }

    /**
     * 弹出软键盘
     */
    public void showSoftInput(EditText view) {
        view.requestFocus();
        //弹出软键盘
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(view, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 隐藏软键盘
     */
    //隐藏选项栏
    public void hideSoftInput(EditText view) {
        //隐藏软键盘和评论栏
        InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    /**
     * 描述：对话框dialog （确认，取消）.
     *
     * @param title 对话框标题内容
     * @param msg  对话框提示内容
     * @param mOkOnClickListener  点击确认按钮的事件监听
     */
    public void showDialog(String title, String msg, DialogInterface.OnClickListener mOkOnClickListener) {
        Builder builder = new Builder(getActivity());
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("确认", mOkOnClickListener);
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    /**
     * 动画启动页面 startAnimActivity
     *
     * @throws
     */
    public void startAnimActivity(Intent intent) {
		this.startActivity(intent);
	}

    public void startAnimActivity(Class<?> cla) {
        getActivity().startActivity(new Intent(getActivity(), cla));
    }

    protected void startAnimActivity(Class<?> pClass, Bundle pBundle) {
        Intent intent = new Intent(getActivity(), pClass);
        if (pBundle != null) {
            intent.putExtras(pBundle);
        }
        getActivity().startActivity(intent);
    }

    /**
     * 描述：对话框dialog （确认，取消）.
     */
    public void showDialog(String title, String msg, DialogInterface.OnClickListener mOkOnClickListener, DialogInterface.OnClickListener onCancelClickListener) {
        Builder builder = new Builder(getActivity());
        builder.setMessage(msg);
        builder.setTitle(title);
        builder.setPositiveButton("确认", mOkOnClickListener);
        builder.setNegativeButton("取消", onCancelClickListener);
        builder.create().show();
    }

    public void showDialog(String title, String[] item, DialogInterface.OnClickListener mOkOnClickListener, int selectPosition) {
        Builder builder = new Builder(getActivity());
        builder.setTitle(title);
        builder.setSingleChoiceItems(item, selectPosition, mOkOnClickListener);
        builder.setNegativeButton("取消", null);
        builder.create().show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
    public void resetData(){
    }
}

