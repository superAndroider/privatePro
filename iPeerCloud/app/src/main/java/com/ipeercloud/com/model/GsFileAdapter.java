package com.ipeercloud.com.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ipeercloud.com.R;
import com.ipeercloud.com.controler.GsJniManager;
import com.ipeercloud.com.controler.GsOpenFileHelper;
import com.ipeercloud.com.utils.GsFile;
import com.ipeercloud.com.utils.GsLog;

import java.util.List;

/**
 * @author 673391138@qq.com
 * @since 17/4/18
 * 主要功能:
 */

public class GsFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<GsFileModule.FileEntity> list;
    private Context context;
    private static final String DERECTORY_TYPE = "files";

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
        View view = LayoutInflater.from(context).inflate(R.layout.item_files, parent, false);
        return new GsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (list == null)
            return;
        GsViewHolder gsholder = (GsViewHolder) holder;
        gsholder.tvName.setText(list.get(position).FileName);
        gsholder.tvSize.setText(list.get(position).FileSize + "");
        gsholder.ivType.setImageResource(getFileIconId(list.get(position).FileType));

        if (DERECTORY_TYPE.equals(gsholder.ivType)) {
            gsholder.BtnPop.setVisibility(View.INVISIBLE);
            return;
        }
        gsholder.BtnPop.setVisibility(View.VISIBLE);
        gsholder.BtnPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fileName = list.get(position).FileName;
                if (!GsFile.isContainsFile(fileName)) {
                    GsJniManager.getInstance().downFile(GsFile.getPath(fileName),
                            "\\" + fileName, new GsCallBack<GsSimpleResponse>() {
                                @Override
                                public void onResult(GsSimpleResponse response) {
                                    GsLog.d("下载的结果  " + response.result);
                                    if (response.result) {
                                        GsOpenFileHelper.startActivity(fileName, "\\" + fileName, context);
                                    }
                                }
                            });
                } else {
                    GsLog.d("文件已经存在，直接打开");
                    GsOpenFileHelper.startActivity(fileName, "\\" + fileName, context);
                }
            }
        });

    }

    private int getFileIconId(int type) {
        return R.drawable.pdf_icon;
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
        ImageView ivType;
        //文件的名字
        TextView tvName;
        //文件大小
        TextView tvSize;
        //按钮图标
        ImageView BtnPop;

        public GsViewHolder(View itemView) {
            super(itemView);
            ivType = (ImageView) itemView.findViewById(R.id.iv_type_icon);
            BtnPop = (ImageView) itemView.findViewById(R.id.iv_file_btn);
            tvName = (TextView) itemView.findViewById(R.id.tv_file_name);
            tvSize = (TextView) itemView.findViewById(R.id.tv_file_size);
        }
    }
}
