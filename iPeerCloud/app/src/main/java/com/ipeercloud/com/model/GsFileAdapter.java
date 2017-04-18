package com.ipeercloud.com.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * @author 673391138@qq.com
 * @since 17/4/18
 * 主要功能:
 */

public class GsFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<GsFileModule.FileEntity> list;
    private Context context;

    public GsFileAdapter(List<GsFileModule.FileEntity> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setData(List<GsFileModule.FileEntity> list) {
        if (list == null || list.size() == 0) {
            return;
        }
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(0, null);
        return new GsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (list == null)
            return;
        GsViewHolder gsholder = (GsViewHolder) holder;
        gsholder.tvName.setText(list.get(position).FileName);
        gsholder.tvSize.setText(list.get(position).FileSize + "");
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
    }

    private class GsViewHolder extends RecyclerView.ViewHolder {
        //类型图标
        ImageView imageView;
        //文件的名字
        TextView tvName;
        //文件大小
        TextView tvSize;

        public GsViewHolder(View itemView) {
            super(itemView);
        }
    }
}
