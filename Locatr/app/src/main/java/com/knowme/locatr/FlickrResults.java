package com.knowme.locatr;


import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FlickrResults {
    private String page, pages, perpage, total;
    @SerializedName("photo") private List<GalleryItem> photos;

    public List<GalleryItem> getPhotos() {
        return photos;
    }
}
