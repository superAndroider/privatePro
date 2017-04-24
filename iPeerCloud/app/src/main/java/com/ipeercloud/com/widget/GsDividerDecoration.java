package com.ipeercloud.com.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * @since 16/12/22
 * 主要功能: recyclerview的分割线
 */

public class GsDividerDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "IMDividerDecoration";

    public static final int HORIZONTAL_LIST = LinearLayoutManager.HORIZONTAL;

    public static final int VERTICAL_LIST = LinearLayoutManager.VERTICAL;


    private int mOrientation;
    //分割线的颜色
    private int mColor = Color.RED;
    //分割线的宽度
    private int mWidth = 2;
    //最后一个item后面是否需要显示分割线,默认不展示
    private boolean mLastItemShowDivider;

    public GsDividerDecoration(Context context) {
    }

    public void setDividerWidth(int width) {
        if (width < 0) {
            throw new IllegalArgumentException("divider width " + width + " is not valid!");
        }
        mWidth = width;
    }

    /**
     * @param color 分割线的颜色
     */
    public void setDividerColor(int color) {
        mColor = color;
    }

    /**
     * @param show 最后一个item后面是否有分割线,默认是没有分割线
     */
    public void isLastItemShowDivider(boolean show) {
        mLastItemShowDivider = show;
    }
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if(!(manager instanceof LinearLayoutManager)){
            throw new IllegalArgumentException("recyclerview's manager is not LinearLayoutManager,can't use IMDividerDecoration");
        }
        mOrientation = ((LinearLayoutManager)manager).getOrientation();
        Log.d(TAG, "onDraw: direction "+mOrientation);
        if (mOrientation == VERTICAL_LIST) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }

    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        if(parent == null){
            return;
        }
        int childCount = parent.getChildCount();
        //最后一个item是否需要展示分割线
        if (!mLastItemShowDivider) {
            childCount--;
        }
        if (childCount < 1) {
            return;
        }

        // 分割线的四个坐标
        float startX, startY, endX, endY;
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStrokeWidth(mWidth);
        paint.setAntiAlias(true);
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            startX = child.getX();
            startY = child.getY() + child.getHeight();
            endX = child.getX() + child.getWidth();
            endY = child.getY() + child.getHeight();
            canvas.drawLine(startX, startY, endX, endY, paint);
        }
    }
    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        int childCount = parent.getChildCount();
        //最后一个item是否需要展示分割线
        if (!mLastItemShowDivider) {
            childCount--;
        }
        if (childCount < 1) {
            return;
        }
        // 分割线的四个坐标
        float startX, startY, endX, endY;
        Paint paint = new Paint();
        paint.setColor(mColor);
        paint.setStrokeWidth(mWidth);
        paint.setAntiAlias(true);
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);
            startX = child.getX()+child.getWidth();
            startY = child.getY();
            endX = child.getX() + child.getWidth();
            endY = child.getY() + child.getHeight();
            canvas.drawLine(startX, startY, endX, endY, paint);
        }
    }
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL_LIST) {
            outRect.set(0, 0, 0, mWidth);
        } else {
            outRect.set(0, 0, mWidth, 0);
        }
    }

}
