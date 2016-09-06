package com.knowme.photogallery;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;

public class PhotoGalleryActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return PhotoGalleryFragment.newInstance();
    }
}
