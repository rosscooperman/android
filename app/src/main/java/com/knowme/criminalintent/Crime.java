package com.knowme.criminalintent;

import android.content.ContentValues;

import com.knowme.criminalintent.CrimeDbSchema.CrimeTable;

import java.util.Date;
import java.util.UUID;


public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    private String mSuspectPhone;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getSuspectPhone() {
        return mSuspectPhone;
    }

    public void setSuspectPhone(String suspectPhone) {
        mSuspectPhone = suspectPhone;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, getId().toString());
        values.put(CrimeTable.Cols.TITLE, getTitle());
        values.put(CrimeTable.Cols.DATE, getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT, getSuspect());
        values.put(CrimeTable.Cols.SUSPECT_PHONE, getSuspectPhone());
        return values;
    }
}
