package com.knowme.photogallery;

import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class FlickrFetchr {
    private static final String TAG = "FlickrFetchr";
    private static final String API_KEY = "393f42d3cd3708736989701f042b3949";


    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();

            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }

            int bytesRead;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }

            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String url) throws IOException {
        return new String(getUrlBytes(url));
    }

    public List<GalleryItem> fetchItems() {
        List<GalleryItem> items = new ArrayList<>();

        try {
            String url = Uri.parse("https://api.flickr.com/services/rest/")
                    .buildUpon()
                    .appendQueryParameter("method", "flickr.photos.getRecent")
                    .appendQueryParameter("api_key", API_KEY)
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build()
                    .toString();

            String jsonString = getUrlString(url);
            Gson gson = new Gson();

            JsonObject jsonObject = gson.fromJson(jsonString, JsonObject.class);
            FlickrResults results = gson.fromJson(jsonObject.get("photos"), FlickrResults.class);
            return results.getPhotos();
        } catch (IOException ioe) {
            Log.e(TAG, "error fetching flickr results", ioe);
        } catch (Exception e) {
            Log.e(TAG, "some other error", e);
        }

        return items;
    }
}
