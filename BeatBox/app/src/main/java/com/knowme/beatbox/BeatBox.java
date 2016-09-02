package com.knowme.beatbox;


import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BeatBox {
    private static final String TAG = "BeatBox";
    private static final String SOUNDS_FOLDER = "sample_sounds";
    private static final int MAX_SOUNDS = 5;

    private AssetManager mAssets;
    private List<Sound> mSounds = new ArrayList<>();
    private SoundPool mSoundPool;

    public BeatBox(Context context) {
        mAssets = context.getAssets();
        mSoundPool = new SoundPool(MAX_SOUNDS, AudioManager.STREAM_MUSIC, 0);
        loadSounds();
    }

    private void loadSounds() {
        try {
            for (String filename : mAssets.list(SOUNDS_FOLDER)) {
                String assetPath = SOUNDS_FOLDER + "/" + filename;
                load(new Sound(assetPath));
            }
        } catch (IOException e) {
            Log.e(TAG, "Error loading sound assets: ", e);
        }
    }

    private void load(Sound sound) throws IOException {
        AssetFileDescriptor fd = mAssets.openFd(sound.getAssetPath());
        int soundId = mSoundPool.load(fd, 1);
        sound.setSoundId(soundId);
        mSounds.add(sound);
    }

    public List<Sound> getSounds() {
        return mSounds;
    }

    public void play(Sound sound) {
        Integer soundId = sound.getSoundId();
        if (soundId == null) {
            return;
        }
        mSoundPool.play(soundId, 1.0f, 10.f, 1, 0, 1.0f);
    }

    public void release() {
        mSoundPool.release();
    }
}
