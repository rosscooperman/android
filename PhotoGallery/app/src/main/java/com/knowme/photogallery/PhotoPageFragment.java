package com.knowme.photogallery;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


public class PhotoPageFragment extends VisibleFragment {
    private static final String TAG = "PhotoPageFragment";
    private static final String ARG_URI = "photo_page_url";

    private Uri mUri;
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private ActionBar mActionBar;

    public static PhotoPageFragment newInstance(Uri uri) {
        Bundle args = new Bundle();
        args.putParcelable(ARG_URI, uri);

        PhotoPageFragment fragment = new PhotoPageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUri = getArguments().getParcelable(ARG_URI);
    }

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_page, container, false);

        AppCompatActivity activity = (AppCompatActivity)getActivity();
        mActionBar = activity.getSupportActionBar();
        if (mActionBar != null) {
            mActionBar.setSubtitle(R.string.loading_message);
        }

        mProgressBar = (ProgressBar)view.findViewById(R.id.fragment_photo_page_progress_bar);
        mProgressBar.setMax(100);

        mWebView = (WebView)view.findViewById(R.id.fragment_photo_page_web_view);
        mWebView.getSettings().setJavaScriptEnabled(true);

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Uri uri = Uri.parse(url);
                if (!uri.getScheme().toLowerCase().startsWith("http")) {
                    Intent i = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(i);
                    return true;
                }
                return false;
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                if (mActionBar != null) {
                    mActionBar.setSubtitle(title);
                }
            }
        });

        mWebView.loadUrl(mUri.toString());

        return view;
    }

    public boolean goBack() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return true;
        }

        return false;
    }
}
