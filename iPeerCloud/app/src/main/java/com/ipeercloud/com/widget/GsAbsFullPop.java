package com.ipeercloud.com.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.StyleRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.PopupWindow;

import com.ipeercloud.com.R;


/**
 *
 * 全屏菜单可继承此类进行二次开发，需要实现的主要有两个方法. {@link #getMenuLayout()} ()}用来确定菜单部分
 * 布局（可以动画弹出的部分），全屏背景由此抽象类统一添加。{@link #onShowPrepare(View)}会在show()方法执行
 * 后进行调用，在这里可以对菜单内容进行处理，处理后的View将会以PopupWindow的形式展现出来。
 */
public abstract class GsAbsFullPop {
    private final Activity activity;
    protected LayoutInflater inflater;
    protected PopupWindow popupWindow;

    protected FrameLayout rootView;
    private boolean isShowing;
    private boolean autoBack = true;

    private OnDismissListener dismissListener;
    private OnCancelListener cancelListener;
    private OnShowListener showListener;

    private boolean isCallCancelWhenDismiss = true;

    public GsAbsFullPop(Activity activity) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        autoBack = true;
    }

    public GsAbsFullPop(Activity activity, boolean autoBack) {
        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        this.autoBack = autoBack;
    }

    protected Activity getContext() {
        return activity;
    }

    public final void setOnDismissListener(OnDismissListener l) {
        dismissListener = l;
    }

    public final void setOnCancelListener(OnCancelListener l) {
        cancelListener = l;
    }

    public final void setOnShowListener(OnShowListener l) {
        showListener = l;
    }

    public void show() {
        if (activity == null || activity.isFinishing())
            return;
        rootView = new FrameLayout(activity);
        rootView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        View menuView = LayoutInflater.from(activity).inflate(getMenuLayout(), rootView, false);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        ViewGroup.LayoutParams menuLp = menuView.getLayoutParams();
        if (menuLp != null) {
            lp.height = menuLp.height;
        }
        if (menuView.getBackground() == null) {
            menuView.setBackgroundColor(Color.WHITE);
        }
        rootView.addView(menuView, lp);
        menuView.setClickable(true);
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickBg();
            }
        });
        if (!onShowPrepare(rootView))
            return;
        popupWindow = new PopupWindow(rootView, WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT, focusable());

        if (autoBack) {
            if (Build.VERSION.SDK_INT < 23) {
                if (!Build.VERSION.CODENAME.equals("MNC")) {
                    popupWindow.setBackgroundDrawable(new BitmapDrawable());
                }
            }
        }

        if (getAnimStyle() != -1) {
            popupWindow.setAnimationStyle(getAnimStyle());
        }

        // double check
        if (activity == null || activity.isFinishing()) {
            return;
        }

        try {
            popupWindow.showAtLocation(activity.findViewById(Window.ID_ANDROID_CONTENT),
                    Gravity.NO_GRAVITY, 0, 0);
        } catch (Exception e) {
            // catch an inner nullpointer Exception
            return;
        }

        if (showListener != null) {
            showListener.onShow();
        }
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                notifyDismiss();
                isShowing = false;
            }
        });
        View content = popupWindow.getContentView();
        if (!(content.getLayoutParams() instanceof WindowManager.LayoutParams)) {
            content = (View) popupWindow.getContentView().getParent();
        }
        WindowManager.LayoutParams wlp = (WindowManager.LayoutParams) content.getLayoutParams();
        wlp.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        wlp.dimAmount = .40f;
        WindowManager wm = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        wm.updateViewLayout(content, wlp);
        isShowing = true;
    }

    public void clickBg() {
        dismiss();
    }

    public void dismiss() {
        if (popupWindow == null) {
            return;
        }
        if (activity == null || activity.isFinishing()) {
            return;
        }
        try {
            if (popupWindow.isShowing()) {
                popupWindow.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        notifyDismiss();
        isShowing = false;
    }

    private void notifyDismiss() {
        if (dismissListener != null) {
            dismissListener.onDismiss();
            dismissListener = null;
        }
        if (cancelListener != null && isCallCancelWhenDismiss) {
            cancelListener.onCancel();
            cancelListener = null;
        }
    }

    public boolean isShowing() {
        return isShowing;
    }

    protected <T extends View> T findViewById(int id) {
        if (rootView == null)
            return null;
        return (T) rootView.findViewById(id);
    }

    protected abstract boolean onShowPrepare(View rootView);

    @LayoutRes
    protected abstract int getMenuLayout();

    @StyleRes
    protected int getAnimStyle() {
        return R.style.GsFullMenuAnim;
    }

    protected boolean focusable() {
        return true;
    }

    public void setCallCancelWhenDismiss(boolean callCancelWhenDismiss) {
        isCallCancelWhenDismiss = callCancelWhenDismiss;
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    public interface OnCancelListener {
        void onCancel();
    }

    public interface OnShowListener {
        void onShow();
    }
}
