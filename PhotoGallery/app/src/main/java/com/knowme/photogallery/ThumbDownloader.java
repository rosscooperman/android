package com.knowme.photogallery;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ThumbDownloader<T> extends HandlerThread {
    private static final String TAG = "ThumbnailDownloader";
    private static final int MESSAGE_DOWNLOAD = 0;

    private Handler mRequestHandler;
    private ConcurrentMap<T,String> mRequestMap = new ConcurrentHashMap<>();
    private Handler mResponseHandler;
    private ThumbDownloadListener<T> mThumbDownloadListener;
    private LruCache<String,Bitmap> mCache = new LruCache<>(50);

    public interface ThumbDownloadListener<T> {
        void onThumbnailDownloaded(T target, Bitmap thumbnail);
    }

    public void setThumbDownloadListener(ThumbDownloadListener<T> listener) {
        mThumbDownloadListener = listener;
    }

    public ThumbDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
    }

    public void queueThumbnail(T target, String url) {
        if (url == null) {
            mRequestMap.remove(target);
        } else {
            mRequestMap.put(target, url);
            mRequestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget();
        }
    }

    public void clearQueue() {
        mRequestHandler.removeMessages(MESSAGE_DOWNLOAD);
    }

    @Override
    protected void onLooperPrepared() {
        mRequestHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    T target = (T)msg.obj;
                    handleRequest(target);
                }
            }
        };
    }

    private void postThumbnail(final String url, final T target, final Bitmap bitmap) {
        mResponseHandler.post(new Runnable() {
            public void run() {
                if (mRequestMap.get(target) != url) {
                    return;
                }
                mRequestMap.remove(target);
                if (mThumbDownloadListener != null) {
                    mThumbDownloadListener.onThumbnailDownloaded(target, bitmap);
                }
            }
        });
    }

    private void handleRequest(final T target) {
        try {
            String url = mRequestMap.get(target);
            if (url == null) {
                return;
            }

            Bitmap bitmap = mCache.get(url);
            if (bitmap == null) {
                byte[] bitmapBytes = new FlickrFetchr().getUrlBytes(url);
                bitmap = BitmapFactory.decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                mCache.put(url, bitmap);
            }

            postThumbnail(url, target, bitmap);
        } catch (IOException ioe) {
            Log.e(TAG, "error downloading image", ioe);
        }
    }
}
