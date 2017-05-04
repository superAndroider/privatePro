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
import com.ipeercloud.com.controler.GsSocketManager;
import com.ipeercloud.com.model.GsFileAdapter;
import com.ipeercloud.com.store.GsDataManager;
import com.ipeercloud.com.utils.GsLog;
import com.ipeercloud.com.widget.GsDividerDecoration;


/**
 * Created by longhengdong on 2016/11/16.
 * 文件页面
 */

public class FilesFragment extends BaseFragment {
    private RecyclerView mRecyclerView;
    private GsFileAdapter mAdapter;
    private ImageView mBtnBack;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_files, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        GsLog.d("onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        GsLog.d("onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        GsLog.d("onStop");
    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_file);
        mBtnBack = (ImageView) view.findViewById(R.id.btn_back_iv);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        GsDividerDecoration divider = new GsDividerDecoration(getContext());
        divider.isLastItemShowDivider(true);
        divider.setDividerColor(getResources().getColor(R.color.color_devider_line));
        mRecyclerView.addItemDecoration(divider);
        mAdapter = new GsFileAdapter(GsDataManager.getInstance().files.fileList, getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnBack.setImageResource(R.drawable.back);
            }
        });

        int connMode = GsSocketManager.getInstance().gsReturnConnectedMode();//1->直连;2->中转;3->局域网
        if ( connMode == 1 ){
            mBtnBack = (ImageView) view.findViewById(R.id.connP2pState);
        }
        else if (connMode == 2 ){
            mBtnBack = (ImageView) view.findViewById(R.id.btn_back_iv);
        }
        else {
            mBtnBack = (ImageView) view.findViewById(R.id.connLanState);
        }
        mBtnBack.setVisibility(View.VISIBLE);

        mRecyclerView.setAdapter(mAdapter);
        mAdapter.onCreate();
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void notifyData() {
        mAdapter.setData(GsDataManager.getInstance().files.fileList);
    }

    public void onBackPressed() {
        if (!mAdapter.onBackPressed()) {
            mBtnBack.setImageResource(R.drawable.ok);
        }
    }

    @Override
    public void resetData() {
        if (mAdapter != null) {
            mAdapter.resetData();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.onDestory();
    }
}
