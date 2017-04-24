/*
 *
 *  * Copyright (C) 2015 Eason.Lai (easonline7@gmail.com)
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *      http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package com.ipeer.imageselect.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.widget.TextView;

import com.ipeer.imageselect.R;
import com.ipeer.imageselect.bean.ImageItem;

import java.util.List;

import uk.co.senab.photoview.HackyViewPager;

public class ImagePreviewActivity extends FragmentActivity implements ViewPager.OnPageChangeListener {

    public static final String CURRENT_POSITION = "currentPositionPreviewActivity";
    public static final String LIST_IMAGES = "listImagesPreviewActivity";

    HackyViewPager mViewPager;
    TextView tvIndicatorPreViewActivity;

    PreViewAdapter mAdapter;

    List<ImageItem> mImageList;
    private int mCurrentItemPosition = 0;  //选中的position


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        mCurrentItemPosition = getIntent().getIntExtra(CURRENT_POSITION, 0);
        mImageList = (List<ImageItem>) getIntent().getSerializableExtra(LIST_IMAGES);
        initView();
    }

    private void initView() {
        mViewPager = (HackyViewPager) findViewById(R.id.viewPagerPreViewActivity);
        mAdapter = new PreViewAdapter(this, mImageList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentItemPosition, false);
        mViewPager.setOnPageChangeListener(this);

        tvIndicatorPreViewActivity = (TextView) findViewById(R.id.tvIndicatorPreViewActivity);

        tvIndicatorPreViewActivity.setText((mCurrentItemPosition + 1) + " / " + mImageList.size());

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        tvIndicatorPreViewActivity.setText((position + 1) + " / " + mImageList.size());

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
