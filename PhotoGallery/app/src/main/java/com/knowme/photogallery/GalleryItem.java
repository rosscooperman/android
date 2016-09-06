package com.knowme.photogallery;

import java.net.URL;
import java.util.UUID;


public class GalleryItem {
    String mId;
    String mCaption;
    String mUrl;

    @Override
    public String toString() {
        return mCaption;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public String getCaption() {
        return mCaption;
    }

    public void setCaption(String caption) {
        mCaption = caption;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String url) {
        mUrl = url;
    }
}
