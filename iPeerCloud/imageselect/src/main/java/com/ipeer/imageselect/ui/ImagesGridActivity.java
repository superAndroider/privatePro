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
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.GridView;

import com.ipeer.imageselect.R;
import com.ipeer.imageselect.bean.ImageSet;
import com.ipeer.imageselect.data.DataSource;
import com.ipeer.imageselect.data.OnImagesLoadedListener;
import com.ipeer.imageselect.data.impl.LocalDataSource;

import java.util.List;

public class ImagesGridActivity extends FragmentActivity implements OnImagesLoadedListener {
    private static final String TAG = "lxm";

    GridView mGridView;
    ImageGrideAdapter mAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_images_grid);

        mGridView = (GridView) findViewById(R.id.gridview);
        mAdapter = new ImageGrideAdapter(this);
        mGridView.setAdapter(mAdapter);

        DataSource dataSource = new LocalDataSource(this);
        dataSource.provideMediaItems(this);//select all images from local database

    }

    @Override
    public void onImagesLoaded(List<ImageSet> imageSetList) {

        Log.i("lxm", "load = ==" + imageSetList.size());

        mAdapter.setImages(imageSetList.get(0).imageItems);

    }

}
