package com.ipeercloud.com.view.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ipeercloud.com.R;
import com.ipeercloud.com.model.GsFileAdapter;
import com.ipeercloud.com.store.GsDataManager;
import com.ipeercloud.com.widget.GsDividerDecoration;


/**
 * Created by longhengdong on 2016/11/16.
 * 个人中心
 */

public class MediasFragment extends BaseFragment{
    private RecyclerView mRecyclerView;
    private GsFileAdapter mAdapter;
    private ImageView mBtnBack;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_medias, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_file);
        mBtnBack = (ImageView) view.findViewById(R.id.btn_back_iv);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        GsDividerDecoration divider = new GsDividerDecoration(getContext());
        divider.setDividerColor(getResources().getColor(R.color.color_devider_line));
        divider.isLastItemShowDivider(true);
        mRecyclerView.addItemDecoration(divider);
        mAdapter = new GsFileAdapter(GsDataManager.getInstance().medias.fileList, getContext());
        mRecyclerView.setAdapter(mAdapter);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mAdapter.onCreate();
    }

    public void notifyData() {
        mAdapter.setData(GsDataManager.getInstance().medias != null ? GsDataManager.getInstance().medias.fileList : null);
    }
    public void onBackPressed() {
        mAdapter.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.onDestory();
    }
}
