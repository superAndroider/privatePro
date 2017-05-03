package com.ipeercloud.com.view.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.ipeer.imageselect.bean.ImageItem;
import com.ipeer.imageselect.bean.ImageSet;
import com.ipeer.imageselect.data.DataSource;
import com.ipeer.imageselect.data.OnImagesLoadedListener;
import com.ipeer.imageselect.data.impl.LocalDataSource;
import com.ipeer.imageselect.ui.ImageGrideAdapter;
import com.ipeer.imageselect.ui.ImagePreviewActivity;
import com.ipeercloud.com.R;
import com.ipeercloud.com.model.GsFileModule;
import com.ipeercloud.com.store.GsDataManager;
import com.ipeercloud.com.utils.Contants;
import com.ipeercloud.com.widget.CustomPopWindow;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by longhengdong on 2016/11/16.
 * 首页
 */

public class PhotosFragment extends BaseFragment implements OnImagesLoadedListener, AdapterView.OnItemClickListener {

    private static final int ORDER_HOSPITAL = 1;
    private static final int ORDER_FAMILY = 2;
    private static final int ORDER_VISITS = 3;

    private List<ImageItem> list = new ArrayList<>();
    private int currentTabIndex;
    private BaseFragment[] fragments;

    @ViewInject(R.id.gridViewPhoto)
    GridView mGridView;
    @ViewInject(R.id.tvPhotosCount)
    TextView tvPhotosCount;

    ImageGrideAdapter mAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        ViewUtils.inject(this, view);

        mAdapter = new ImageGrideAdapter(getActivity(), mGridView);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);

        DataSource dataSource = new LocalDataSource(getActivity());
        dataSource.provideMediaItems(this);//select all images from local database

        return view;
    }

    @Override
    public void onImagesLoaded(List<ImageSet> imageSetList) {

        if (imageSetList == null || imageSetList.size() == 0 || imageSetList.get(0) == null) return;
        list = imageSetList.get(0).imageItems;
        tvPhotosCount.setText(imageSetList.get(0).imageItems.size() + "照片");
        mAdapter.setImages(imageSetList.get(0).imageItems);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        ImageItem imageItem = mAdapter.getItem(position);

        GsFileModule.FileEntity entity = new GsFileModule.FileEntity();
        entity.FileName = imageItem.name;
        entity.FileSize = 0;
        entity.FileType = 0;

        GsDataManager.getInstance().recentFile.addEntity(entity);
        Intent intent = new Intent(getActivity(), ImagePreviewActivity.class);
        intent.putExtra(ImagePreviewActivity.CURRENT_POSITION, position);
        intent.putExtra(ImagePreviewActivity.LIST_IMAGES, (Serializable) mAdapter.getList());
        startActivity(intent);
    }

    @OnClick({(R.id.main_title_sort)})
    public void onClick(View view) {
        if (list == null || list.size() == 0) return;
        switch (view.getId()) {
            case R.id.main_title_sort:
                final CustomPopWindow popWindow = new CustomPopWindow(getActivity(), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        switch (view.getId()) {
                            case R.id.tvAllPopWindow:
                                mAdapter.setImages(sort(list, 0));
                                break;
                            case R.id.tvDayPopWindow:
                                mAdapter.setImages(sort(list, 1));
                                break;
                            case R.id.tvMonthPopWindow:
                                mAdapter.setImages(sort(list, 2));
                                break;
                            case R.id.tvYearPopWindow:
                                mAdapter.setImages(sort(list, 3));
                                break;
                        }

                    }
                });
                popWindow.showAsDropDown(view);
                break;
        }
    }

    /***
     * @param type: 0:所有  1:一天  2:一月  3:一年
     * */
    private List<ImageItem> sort(List<ImageItem> items, int type) {
        List<ImageItem> sortList = new ArrayList<>();
        //上传N天以前的照片
        long oneDayAgo = 0l;
        switch (type) {
            case 0:
                oneDayAgo = System.currentTimeMillis() - Contants.MILLIS_ONE_DAY;
                break;
            case 1:
                oneDayAgo = System.currentTimeMillis() - Contants.MILLIS_ONE_MONTH;
                break;
            case 2:
                oneDayAgo = System.currentTimeMillis() - Contants.MILLIS_ONE_YEAR;
                break;
            default:
                oneDayAgo = System.currentTimeMillis() - Contants.MILLIS_ONE_DAY;

        }
        oneDayAgo = oneDayAgo / 1000;

        for (final ImageItem item : items) {
            if (item.time > oneDayAgo) {
                sortList.add(item);
            }
        }
        return sortList;
    }
}
