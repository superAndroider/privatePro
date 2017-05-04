package com.ipeercloud.com.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.ipeercloud.com.R;
import com.ipeercloud.com.controler.GsSocketManager;
import com.ipeercloud.com.model.GsFileAdapter;
import com.ipeercloud.com.model.GsFileModule;
import com.ipeercloud.com.store.GsDataManager;
import com.ipeercloud.com.widget.GsDividerDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by longhengdong on 2016/11/16.
 * 首页
 */

public class HomeFragment extends BaseFragment {
    private static final String TAG = "HomeFragment";
    private RecyclerView mRecyclerView;
    private GsFileAdapter mAdapter;
    private ImageView mBtnBack;
    private EditText mSearchEt;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initView(View view) {
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rv_recent);
        mSearchEt = (EditText) view.findViewById(R.id.et_search);
        mSearchEt.setOnKeyListener(mOnKeyListener);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(manager);
        GsDividerDecoration divider = new GsDividerDecoration(getContext());
        divider.setDividerColor(getResources().getColor(R.color.color_devider_line));
        divider.isLastItemShowDivider(true);
        mRecyclerView.addItemDecoration(divider);
        mAdapter = new GsFileAdapter(GsDataManager.getInstance().recentFile.fileList, getContext(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBtnBack.setImageResource(R.drawable.back);
            }
        });
        mRecyclerView.setAdapter(mAdapter);

        int connMode = GsSocketManager.getInstance().gsReturnConnectedMode();//1->直连;2->中转;3->局域网
        if ( connMode == 1 ){
            mBtnBack = (ImageView) view.findViewById(R.id.btn);
        }
        else if (connMode == 2 ){
            mBtnBack = (ImageView) view.findViewById(R.id.btn_back_iv);
        }
        else {
            mBtnBack = (ImageView) view.findViewById(R.id.btn_back_iv);
        }
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mAdapter.onCreate();

    }

    public void notifyData() {
        mAdapter.setData(GsDataManager.getInstance().recentFile.fileList);
    }

    @Override
    public void resetData() {
        mAdapter.setData(GsDataManager.getInstance().recentFile.fileList);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mAdapter.onDestory();
    }

    private View.OnKeyListener mOnKeyListener = new View.OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                /*隐藏软键盘*/
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (inputMethodManager.isActive()) {
                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
                search();
                return true;
            }
            return false;
        }
    };

    private void search() {
        String inputString = mSearchEt.getText().toString();
        if (TextUtils.isEmpty(inputString)) {
            return;
        }
        List<GsFileModule.FileEntity> list = GsDataManager.getInstance().recentFile.fileList;
        if (list == null || list.size() == 0) {
            return;
        }
        List<GsFileModule.FileEntity> matchedList = new ArrayList<>();
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (inputString.equals(list.get(i).FileName) ||
                    (list.get(i).FileName != null && list.get(i).FileName.contains(inputString))) {
                matchedList.add(list.get(i));
            }
        }
        mAdapter.setData(matchedList);
    }
    public void onBackPressed() {
        if (!mAdapter.onBackPressed()) {
            mBtnBack.setImageResource(R.drawable.ok);
        }
    }
}
