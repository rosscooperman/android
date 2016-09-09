package com.knowme.locatr;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;
import java.util.List;


public class LocatrFragment extends Fragment {
    private static final String TAG = "LocatrFragment";

    private ImageView mImageView;
    private GoogleApiClient mClient;

    public static LocatrFragment newInstance() {
        return new LocatrFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(@Nullable Bundle bundle) {
                        getActivity().invalidateOptionsMenu();
                    }

                    @Override
                    public void onConnectionSuspended(int i) {}
                })
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();
        getActivity().invalidateOptionsMenu();
        mClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        mClient.disconnect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_locatr, container, false);

        mImageView = (ImageView)view.findViewById(R.id.image);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.fragment_locatr, menu);

        MenuItem searchItem = menu.findItem(R.id.action_locate);
        searchItem.setEnabled(mClient.isConnected());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_locate:
                findImage();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void findImage() {
        LocationRequest request = LocationRequest.create();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);
        request.setInterval(0);

        try {
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mClient, request, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            new SearchTask().execute(location);
                        }
                    });
        } catch (SecurityException se) {
            Log.e(TAG, "no location permission", se);
        }
    }

    private class SearchTask extends AsyncTask<Location,Void,Void> {
        private GalleryItem mGalleryItem;
        private Bitmap mBitmap;

        @Override
        protected Void doInBackground(Location... locations) {
            FlickrFetchr fetchr = new FlickrFetchr();
            List<GalleryItem> items = fetchr.searchPhotos(locations[0]);

            if (items.size() > 0) {
                mGalleryItem = items.get(0);
                try {
                    byte[] bytes = fetchr.getUrlBytes(mGalleryItem.getUrl());
                    mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                } catch (IOException ioe) {
                    Log.e(TAG, "unable to decode bitmap", ioe);
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mBitmap != null) {
                mImageView.setImageBitmap(mBitmap);
            }
        }
    }
}