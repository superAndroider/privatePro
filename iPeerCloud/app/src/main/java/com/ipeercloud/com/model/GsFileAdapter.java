package com.ipeercloud.com.model;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ipeercloud.com.R;
import com.ipeercloud.com.controler.GsFileHelper;
import com.ipeercloud.com.controler.GsJniManager;
import com.ipeercloud.com.controler.GsLifeCycle;
import com.ipeercloud.com.model.EventBusEvent.GsProgressEvent;
import com.ipeercloud.com.store.GsDataManager;
import com.ipeercloud.com.utils.GsFile;
import com.ipeercloud.com.utils.GsLog;
import com.ipeercloud.com.widget.GsFullPop;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.DecimalFormat;
import java.util.List;

import static android.R.attr.path;

/**
 * @author 673391138@qq.com
 * @since 17/4/18
 * 主要功能:
 */

public class GsFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements GsLifeCycle {
    private List<GsFileModule.FileEntity> mList;
    private Context context;
    private static final String DERECTORY_TYPE = "files";
    private StringBuilder mCurrentPath = new StringBuilder("\\");
    private StringBuilder mNewPath = new StringBuilder();
    //0 表示最近 1 表示视频 2表示文件
    private int mType;
    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            notifyDataSetChanged();
        }
    };
    public GsFileAdapter(List<GsFileModule.FileEntity> list, Context context) {
        this.mList = list;
        this.context = context;

    }

    public void setData(List<GsFileModule.FileEntity> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_files, parent, false);
        return new GsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (mList == null)
            return;
        final GsViewHolder gsholder = (GsViewHolder) holder;
        //
        /*if (GsFile.isContainsFile(mList.get(position).FileName)) {
            mList.get(position).loadingProgress = 100;
        }*/
        //控制进度与下载是否完成图标的显示
        if (mList.get(position).loadingProgress == -1) {
            gsholder.progressBar.setVisibility(View.INVISIBLE);
            gsholder.mHasDownIv.setVisibility(View.INVISIBLE);
        } else if (mList.get(position).loadingProgress == 100) {
            gsholder.progressBar.setVisibility(View.INVISIBLE);
            gsholder.mHasDownIv.setVisibility(View.VISIBLE);
        } else {
            gsholder.progressBar.setVisibility(View.VISIBLE);
            gsholder.mHasDownIv.setVisibility(View.INVISIBLE);
            gsholder.progressBar.setProgress(mList.get(position).loadingProgress);
            GsLog.d("onBindViewHolder 进度");
        }

        gsholder.tvName.setText(mList.get(position).FileName);
        gsholder.tvSize.setText(getStringSize(mList.get(position).FileSize));
        gsholder.ivType.setImageResource(getFileIconId(mList.get(position).FileName));
        // 是一个目录
        if (GsFileType.TYPE_DIRECTORY.equals(GsFileHelper.getFileNameType(mList.get(position).FileName))) {
            gsholder.BtnPop.setVisibility(View.INVISIBLE);
            gsholder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mNewPath = new StringBuilder(mCurrentPath);
                    if (!mCurrentPath.equals("\\")) {
                        mNewPath = mNewPath.append("\\");
                    }
                    mNewPath = mNewPath.append(mList.get(position).FileName);
                    GsJniManager.getInstance().getPathFile(mNewPath.toString(), false, new GsCallBack<GsSimpleResponse>() {
                        @Override
                        public void onResult(GsSimpleResponse response) {
                            if (response.result) {
                                mCurrentPath = new StringBuilder(mNewPath);
                                GsLog.d("向下目录 ： " + mCurrentPath.toString());
                                updateData(mCurrentPath.toString());
                                notifyDataSetChanged();
                            } else {
                                Toast.makeText(context, context.getResources().getString(R.string.net_wrong), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
            return;
        }
        // 是文件
        gsholder.BtnPop.setVisibility(View.VISIBLE);
        final String fileName = mList.get(position).FileName;
        gsholder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!GsFile.isContainsFile(fileName)) {
                    //点击条目，但是条目并没有下载
                } else {
                    GsLog.d("文件已经存在，直接打开");
                    GsFileHelper.startActivity(fileName, GsFile.getPath(fileName), context);
                }
                GsDataManager.getInstance().recentFile.addEntity(mList.get(position));
            }

        });
        gsholder.BtnPop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final boolean hasDown = mList.get(position).loadingProgress == 100;
                //弹窗
                final GsFullPop pop = new GsFullPop((Activity) context, hasDown);
                pop.setDownloadClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pop.dismiss();
                        if (hasDown) {
                            //删除
                        } else {
                            downLoadFile(fileName);
                            gsholder.progressBar.setVisibility(View.VISIBLE);
                            mList.get(position).loadingProgress = 0;
                        }
                    }
                });
                pop.show();
            }
        });


    }


    private void downLoadFile(final String fileName) {
        String remotePath;
        if (mCurrentPath.equals("\\")) {
            remotePath = mCurrentPath + fileName;
        } else {
            remotePath = mCurrentPath + "\\" + fileName;
        }
        GsJniManager.getInstance().downFile(GsFile.getPath(fileName),
                remotePath, new GsCallBack<GsSimpleResponse>() {
                    @Override
                    public void onResult(GsSimpleResponse response) {
                        GsLog.d("下载的结果  " + response.result);
                        if (response.result) {
//                            GsFileHelper.startActivity(FileName, GsFile.getPath(FileName), context);
                            Toast.makeText(context, "文件" + fileName + "下载成功", Toast.LENGTH_LONG).show();
                            downSuccess(fileName);
                        } else {
                            Toast.makeText(context, "文件" + fileName + "下载失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /**
     * @param name
     */
    private void downSuccess(String name) {
        if (TextUtils.isEmpty(name)) {
            return;
        }
        if (mList == null || mList.size() == 0) {
            return;
        }
        int size = mList.size();
        for (int i = 0; i < size; i++) {
            if (name.equals(mList.get(i).FileName)) {
                mList.get(i).loadingProgress = 100;
                break;
            }
        }


    }

    private int getFileIconId(String fileName) {
        int id = 0;
        switch (GsFileHelper.getFileNameType(fileName)) {
            case GsFileType.TYPE_BMP:
            case GsFileType.TYPE_JPG:
            case GsFileType.TYPE_JPG_S:
            case GsFileType.TYPE_PNG:
                id = R.drawable.photo_no_down;
                break;
            case GsFileType.TYPE_PDF:
                id = R.drawable.pdf_icon;
                break;
            case GsFileType.TYPE_MP3:
                id = R.drawable.music;
                break;
            case GsFileType.TYPE_MP4:
                id = R.drawable.media_no_down;
                break;
            case GsFileType.TYPE_TEXT:
            case GsFileType.TYPE_TXT:
            case GsFileType.TYPE_DOC:
            case GsFileType.TYPE_DOCX:
                id = R.drawable.word_no_down;
                break;
            case GsFileType.TYPE_DIRECTORY:
                id = R.drawable.file_dir;
                break;
            case GsFileType.TYPE_XLS:
                id = R.drawable.excel_no_down;
                break;
            case GsFileType.TYPE_PPT:
                id = R.drawable.ppt;
                break;

        }
        if (id == 0) {
            id = R.drawable.unkown_type;
        }

        return id;
    }

    private String getStringSize(long size) {
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        if (size / 1024 < 1) {
            return size + "B";
        }
        if (size / (1024 * 1024) < 1) {
            return (decimalFormat.format((size / (float) 1024))) + "KB";
        }
        if (size / (1024 * 1024 * 1024) < 1) {
            return (decimalFormat.format((size / (float) (1024 * 1024)))) + "MB";
        }
        return size + "";
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        }
        return 0;
    }


    public void onBackPressed() {
        if (mCurrentPath.equals("\\")) {
            return;
        }

        int index = mCurrentPath.lastIndexOf("\\");
        if (index == -1) {
            return;
        }

        mCurrentPath.delete(index, mCurrentPath.length());
        updateData(mCurrentPath.toString());

    }

    public void resetData() {
        mCurrentPath = new StringBuilder("\\");
    }

    /**
     * 更新数据
     */
    private void updateData(String path) {
        if ("\\".equals(path)) {
            mList = GsDataManager.getInstance().files.fileList;
            notifyDataSetChanged();
            return;
        }
        if (GsDataManager.getInstance().fileMaps.get(path) != null) {
            mList = GsDataManager.getInstance().fileMaps.get(path).fileList;
            notifyDataSetChanged();
        }
    }

    /**
     * 更新数据
     */
    private void updateData() {
        if ("\\".equals(path)) {
            mList = GsDataManager.getInstance().files.fileList;
            notifyDataSetChanged();
            return;
        }
        if (GsDataManager.getInstance().fileMaps.get(mCurrentPath.toString()) != null) {
            mList = GsDataManager.getInstance().fileMaps.get(mCurrentPath.toString()).fileList;
            notifyDataSetChanged();
        }
    }

    /**
     * @param event 通过eventBus更新下载进度
     */
    @Subscribe
    public void updateDownLoadProgress(GsProgressEvent event) {
        if (event == null || TextUtils.isEmpty(event.remotePath)) {
            return;
        }
        if (mList == null || mList.size() == 0) {
            return;
        }
        String remoteFolderName = GsFileHelper.getFolderNameFromRemotePath(event.remotePath);
        if (TextUtils.isEmpty(remoteFolderName)) {
            return;
        }
        if (!remoteFolderName.contains(mCurrentPath.toString())) {
            // 要更新下载进度的文件不在本adapter中
            return;
        }
        String fileName = GsFileHelper.getFileNameFromRemotePath(event.remotePath);
        if (TextUtils.isEmpty(fileName)) {
            return;
        }
        int size = mList.size();
        for (int i = 0; i < size; i++) {
            if (fileName.equals(mList.get(i).FileName)) {
                final int index = i;
                mList.get(i).loadingProgress = event.progress;
                mHandler.sendEmptyMessage(index);
                return;
            }
        }
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestory() {
        EventBus.getDefault().unregister(this);
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
        //标志是否已经下载的图标
        ImageView mHasDownIv;
        View itemView;
        ProgressBar progressBar;

        public GsViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            ivType = (ImageView) itemView.findViewById(R.id.iv_type_icon);
            BtnPop = (ImageView) itemView.findViewById(R.id.iv_file_btn);
            tvName = (TextView) itemView.findViewById(R.id.tv_file_name);
            tvSize = (TextView) itemView.findViewById(R.id.tv_file_size);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
            mHasDownIv = (ImageView) itemView.findViewById(R.id.iv_has_down);
        }
    }
}
