package com.knowme.criminalintent;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat.IntentBuilder;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;


public class CrimeFragment extends Fragment {
    private static final String ARG_CRIME_ID = "crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOG_PREVIEW = "DialogPreview";

    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT = 3;
    private static final int REQUEST_PERMISSIONS = 4;
    private static final int REQUEST_PHOTO = 5;

    private Crime mCrime;
    private File mPhotoFile;

    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mSuspectButton;
    private Button mSendReportButton;
    private Button mCallSuspectButton;
    private Button mDeleteButton;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;

    private Intent pickContactIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);


    private void updateDate() {
        java.text.DateFormat dateFormatter = DateFormat.getMediumDateFormat(getActivity().getApplicationContext());
        java.text.DateFormat timeFormatter = DateFormat.getTimeFormat(getActivity().getApplicationContext());

        mDateButton.setText(dateFormatter.format(mCrime.getDate()));
        mTimeButton.setText(timeFormatter.format(mCrime.getDate()));
    }

    private String getCrimeReport() {
        String solvedString = null;
        if (mCrime.isSolved()) {
            solvedString = getString(R.string.crime_report_solved);
        } else {
            solvedString = getString(R.string.crime_report_unsolved);
        }

        String dateString = DateFormat.format("EEE, MMM dd", mCrime.getDate()).toString();

        String suspect= mCrime.getSuspect();
        if (suspect == null) {
            suspect = getString(R.string.crime_report_no_suspect);
        } else {
            suspect = getString(R.string.crime_report_suspect, suspect);
        }

        return getString(R.string.crime_report, mCrime.getTitle(), dateString, solvedString, suspect);
    }

    private void updatePhotoView() {
        if (mPhotoFile == null || !mPhotoFile.exists()) {
            mPhotoView.setImageDrawable(null);
            mPhotoView.setEnabled(false);
        } else {
            Bitmap bitmap = PictureUtils.getScaledBitmap(mPhotoFile.getPath(), getActivity());
            mPhotoView.setImageBitmap(bitmap);
            mPhotoView.setEnabled(true);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UUID crimeId = (UUID)getArguments().getSerializable(ARG_CRIME_ID);

        mCrime = CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile = CrimeLab.get(getActivity()).getPhotoFile(mCrime);
    }

    @Override
    public void onPause() {
        super.onPause();
        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        mTitleField = (EditText)v.findViewById(R.id.crime_title);
        mDateButton = (Button)v.findViewById(R.id.crime_date);
        mTimeButton = (Button)v.findViewById(R.id.crime_time);
        mSolvedCheckBox = (CheckBox)v.findViewById(R.id.crime_solved);
        mSuspectButton = (Button)v.findViewById(R.id.choose_suspect_button);
        mSendReportButton = (Button)v.findViewById(R.id.send_report_button);
        mCallSuspectButton = (Button)v.findViewById(R.id.call_suspect_button);
        mDeleteButton = (Button)v.findViewById(R.id.delete_crime_button);
        mPhotoButton = (ImageButton)v.findViewById(R.id.crime_camera);
        mPhotoView = (ImageView)v.findViewById(R.id.crime_photo);

        mTitleField.setText(mCrime.getTitle());
        mSolvedCheckBox.setChecked(mCrime.isSolved());

        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment picker = DatePickerFragment.newInstance(mCrime.getDate());
                picker.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                picker.show(getFragmentManager(), DIALOG_DATE);
            }
        });

        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TimePickerFragment picker = TimePickerFragment.newInstance(mCrime.getDate());
                picker.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
                picker.show(getFragmentManager(), DIALOG_TIME);
            }
        });

        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        PackageManager manager = getActivity().getPackageManager();
        if (manager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY) == null) {
            mSuspectButton.setEnabled(false);
        }

        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int permissionCheck = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_CONTACTS);
                if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[] { Manifest.permission.READ_CONTACTS }, REQUEST_PERMISSIONS);
                } else {
                    pickContact();
                }

            }
        });

        boolean hasSuspect = (mCrime.getSuspect() != null);
        mCallSuspectButton.setEnabled(hasSuspect);
        if (hasSuspect) {
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mSendReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentBuilder builder = IntentBuilder.from(getActivity());
                builder.setType("text/plain");
                builder.setText(getCrimeReport());
                builder.setSubject(getString(R.string.crime_report_subject));

                Intent c = Intent.createChooser(builder.getIntent(), getString(R.string.send_report));
                startActivity(c);
            }
        });

        mCallSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = mCrime.getSuspectPhone();
                if (phone == null) { return; }

                Intent i = new Intent(Intent.ACTION_DIAL);
                i.setData(Uri.parse("tel:" + phone));
                startActivity(i);
            }
        });

        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (CrimeLab.get(getActivity()).deleteCrime(mCrime)) {
                    getActivity().finish();
                }
            }
        });

        final Intent captureImage = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        boolean canTakePhoto = mPhotoFile != null && captureImage.resolveActivity(manager) != null;
        mPhotoButton.setEnabled(canTakePhoto);

        if (canTakePhoto) {
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mPhotoFile));
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(captureImage, REQUEST_PHOTO);
            }
        });
        updatePhotoView();

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PhotoPreviewFragment preview = PhotoPreviewFragment.newInstance(mPhotoFile);
                preview.show(getFragmentManager(), DIALOG_PREVIEW);
//                DatePickerFragment picker = DatePickerFragment.newInstance(mCrime.getDate());
//                picker.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
//                picker.show(getFragmentManager(), DIALOG_DATE);
            }
        });

        return v;
    }

    private void pickContact() {
        startActivityForResult(pickContactIntent, REQUEST_CONTACT);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickContact();
            }
        }
    }

    private void handleDate(Intent data) {
        Date date = (Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
        mCrime.setDate(date);
    }

    private void handleTime(Intent data) {
        Calendar c = GregorianCalendar.getInstance();

        c.setTime(mCrime.getDate());
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        c.setTime((Date)data.getSerializableExtra(DatePickerFragment.EXTRA_DATE));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        Date newDate = new GregorianCalendar(year, month, day, hour, minute).getTime();
        mCrime.setDate(newDate);
    }

    private void handleContact(Intent data) {
        String colId = ContactsContract.Contacts._ID;
        String colName = ContactsContract.Contacts.DISPLAY_NAME;
        String colHasNumber = ContactsContract.Contacts.HAS_PHONE_NUMBER;
        String colPhone = ContactsContract.CommonDataKinds.Phone.NUMBER;

        Uri uri = data.getData();
        String[] projection = new String[] { colName, colId, colHasNumber };
        ContentResolver resolver = getActivity().getContentResolver();

        Cursor c = resolver.query(uri, projection, null, null, null);

        try {
            if (c.getCount() == 0) {
                return;
            }

            c.moveToFirst();
            String name = c.getString(c.getColumnIndex(colName));
            String id = c.getString(c.getColumnIndex(colId));
            boolean hasNumber = Integer.parseInt(c.getString(c.getColumnIndex(colHasNumber))) > 0;

            String contactNumber = null;
            if (hasNumber) {
                uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                projection = new String[] { colPhone };
                String where = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
                String[] whereArgs = new String[] { id };

                Cursor nc = resolver.query(uri, projection, where, whereArgs, null);
                if (nc.getCount() > 0) {
                    nc.moveToFirst();
                    contactNumber = nc.getString(nc.getColumnIndex(colPhone));
                    mCallSuspectButton.setEnabled(true);
                }
            }

            mCrime.setSuspect(name);
            mCrime.setSuspectPhone(contactNumber);
            mSuspectButton.setText(mCrime.getSuspect());
        } finally {
            c.close();
        }
    }

    private void handlePhoto(Intent data) {
        updatePhotoView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) { return; }

        if (data != null) {
            switch (requestCode) {
                case REQUEST_DATE:
                    handleDate(data); break;
                case REQUEST_TIME:
                    handleTime(data); break;
                case REQUEST_CONTACT:
                    handleContact(data); break;
                case REQUEST_PHOTO:
                    handlePhoto(data); break;
            }
        }

       updateDate();
    }

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_CRIME_ID, crimeId);

        CrimeFragment fragment = new CrimeFragment();
        fragment.setArguments(args);

        return fragment;
    }
}
