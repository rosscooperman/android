package com.knowme.criminalintent;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.File;

public class PhotoPreviewFragment extends DialogFragment {
    private static String ARG_FILE = "file";

    ImageView mImageView;

    public static PhotoPreviewFragment newInstance(File file) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_FILE, file);

        PhotoPreviewFragment fragment = new PhotoPreviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

   @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        File file = (File)getArguments().getSerializable(ARG_FILE);
        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_photo_preview, null);

       if (file != null) {
           mImageView = (ImageView) v.findViewById(R.id.photo_preview_image_view);
           Bitmap bitmap = PictureUtils.getScaledBitmap(file.getPath(), getActivity());
           mImageView.setImageBitmap(bitmap);
       }

       return new AlertDialog.Builder(getActivity())
               .setView(v)
               .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialogInterface, int i) {
                       dismiss();
                   }
               })
               .create();
    }
}
