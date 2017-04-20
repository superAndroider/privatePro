package com.ipeercloud.com.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ipeercloud.com.R;
import com.ipeercloud.com.model.GsFileAdapter;
import com.ipeercloud.com.store.GsDataManager;


/**
 * Created by longhengdong on 2016/11/16.
 * 文件页面
 */

public class FilesFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private GsFileAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_file);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new GsFileAdapter(GsDataManager.getInstance().files != null ? GsDataManager.getInstance().files.fileList : null, getContext());
        mRecyclerView.setAdapter(mAdapter);
    }

    public void notifyData() {
        mAdapter.setData(GsDataManager.getInstance().files != null ? GsDataManager.getInstance().files.fileList : null);
    }
}
