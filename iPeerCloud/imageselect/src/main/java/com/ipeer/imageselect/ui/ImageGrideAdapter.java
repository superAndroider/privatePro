package com.ipeer.imageselect.ui;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.ipeer.imageselect.R;
import com.ipeer.imageselect.bean.ImageItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 李小明 on 17/4/23.
 * 邮箱:287907160@qq.com
 */

public class ImageGrideAdapter extends BaseAdapter {

    List<ImageItem> images = new ArrayList<>();
    Context mContext;


    int itemWidth;  //每个item宽度

    public ImageGrideAdapter(Context ctx, GridView gridView) {
        this.mContext = ctx;

        int screenWidth = ctx.getResources().getDisplayMetrics().widthPixels;

        int width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        //measure一次才能获取到属性值
        gridView.measure(width, height);
        int columnCount = gridView.getNumColumns();

        itemWidth = screenWidth / columnCount;
    }

    public void setImages(List<ImageItem> images) {
        this.images = images;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public ImageItem getItem(int position) {
        return images.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item_camera, null);
            holder = new ViewHolder();
            holder.ivPic = (ImageView) convertView.findViewById(R.id.ivItemGrid);

            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.ivPic.getLayoutParams();
            params.width = itemWidth;
            params.height = itemWidth;
            holder.ivPic.setLayoutParams(params);


            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ImageItem item = getItem(position);

        Glide.with(mContext).load(item.path).into(holder.ivPic);

        return convertView;

    }

    class ViewHolder {
        ImageView ivPic;
    }

}
