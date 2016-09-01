package com.knowme.beatbox;


import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {
    private static final String TAG = "BeatBox";
    private static final String SOUNDS_FOLDER = "sample_sounds";

    private AssetManager mAssets;
    private List<Sound> mSounds = new ArrayList<>();

    public BeatBox(Context context) {
        mAssets = context.getAssets();
        loadSounds();
    }

    private void loadSounds() {
        try {
            for (String filename : mAssets.list(SOUNDS_FOLDER)) {
                String assetPath = SOUNDS_FOLDER + "/" + filename;
                mSounds.add(new Sound(assetPath));
            }
        } catch (IOException e) {
            Log.e(TAG, "Error fetching sound assets: ", e);
        }
    }

    public List<Sound> getSounds() {
        return mSounds;
    }
}
