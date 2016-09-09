package com.knowme.sunset;


import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;


public class SunsetFragment extends Fragment {

    private View mSceneView;
    private View mSunView;
    private View mSkyView;

    private int mBlueSkyColor;
    private int mSunsetSkyColor;
    private int mNightSkyColor;

    public static SunsetFragment newInstance() {
        return new SunsetFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sunset, container, false);

        Resources res = getResources();
        mBlueSkyColor = res.getColor(R.color.blue_sky);
        mSunsetSkyColor = res.getColor(R.color.sunset_sky);
        mNightSkyColor = res.getColor(R.color.night_sky);

        mSceneView = view;
        mSunView = view.findViewById(R.id.sun);
        mSkyView = view.findViewById(R.id.sky);

        mSceneView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnimation();
            }
        });

        return view;
    }

    private void startAnimation() {
        float sunYStart = mSunView.getTop();
        float sunYEnd = mSkyView.getHeight();

        ObjectAnimator heightAnim = ObjectAnimator.ofFloat(mSunView, "y", sunYStart, sunYEnd);
        heightAnim.setInterpolator(new AccelerateInterpolator());
        heightAnim.setDuration(3000);

        ObjectAnimator sunsetAnim = ObjectAnimator.ofInt(mSkyView, "backgroundColor", mBlueSkyColor, mSunsetSkyColor);
        sunsetAnim.setEvaluator(new ArgbEvaluator());
        sunsetAnim.setDuration(3000);

        ObjectAnimator nightAnim = ObjectAnimator.ofInt(mSkyView, "backgroundColor", mSunsetSkyColor, mNightSkyColor);
        nightAnim.setEvaluator(new ArgbEvaluator());
        nightAnim.setDuration(1500);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet
                .play(heightAnim)
                .with(sunsetAnim)
                .before(nightAnim);
        animatorSet.start();
    }
}
