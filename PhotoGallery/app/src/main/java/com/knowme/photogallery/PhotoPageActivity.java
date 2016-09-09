package com.knowme.photogallery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

public class PhotoPageActivity extends SingleFragmentActivity {

    @Nullable
    private PhotoPageFragment mFragment;

    public static Intent newIntent(Context context, Uri photoPageUri) {
        Intent i = new Intent(context, PhotoPageActivity.class);
        i.setData(photoPageUri);
        return i;
    }

    @Override
    protected Fragment createFragment() {
        mFragment = PhotoPageFragment.newInstance(getIntent().getData());
        return mFragment;
    }

    @Override
    public void onBackPressed() {
        if (mFragment == null || !mFragment.goBack()) {
            super.onBackPressed();
        }
    }
}
