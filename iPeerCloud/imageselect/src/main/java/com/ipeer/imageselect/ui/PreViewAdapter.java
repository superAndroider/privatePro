package com.ipeer.imageselect.ui;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ipeer.imageselect.R;
import com.ipeer.imageselect.bean.ImageItem;
import com.ipeer.imageselect.bean.ImageSet;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoView;

/**
 * Created by 李小明 on 17/4/24.
 * 邮箱:287907160@qq.com
 */

public class PreViewAdapter extends PagerAdapter {

    private List<ImageItem> resourceList;

    private Activity activity;

    public PreViewAdapter(Activity activity, List<ImageItem> list) {
        this.activity = activity;
        resourceList = list;
    }

    @Override
    public int getCount() {
        return resourceList.size();
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        final PhotoView photoView = new PhotoView(container.getContext());
        String picRecourse = resourceList.get(position).path;
        Glide.with(activity).load(picRecourse).into(photoView);
        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
