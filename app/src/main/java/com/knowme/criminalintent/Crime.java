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

    public ContentValues toContentValues() {
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, getId().toString());
        values.put(CrimeTable.Cols.TITLE, getTitle());
        values.put(CrimeTable.Cols.DATE, getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, isSolved() ? 1 : 0);
        return values;
    }
}
