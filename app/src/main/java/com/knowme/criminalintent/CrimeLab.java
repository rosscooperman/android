package com.knowme.criminalintent;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.knowme.criminalintent.CrimeDbSchema.CrimeTable;

import java.util.List;
import java.util.UUID;

public class CrimeLab {
    private static CrimeLab sCrimeLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static CrimeLab get(Context context) {
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    private CrimeLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();
    }

    private Cursor queryCrimes(String whereClause, String[] whereArgs) {
        return mDatabase.query(CrimeTable.NAME, null, whereClause, whereArgs, null, null, null);
    }

    public List<Crime> getCrimes() {
        return null;
    }

    public Crime getCrime(UUID id) {
        return null;
    }

    public void addCrime(Crime crime) {
        mDatabase.insert(CrimeTable.NAME, null, crime.toContentValues());
    }

    public void updateCrime(Crime crime) {
        String uuidString = crime.getId().toString();
        ContentValues values = crime.toContentValues();

        mDatabase.update(CrimeTable.NAME, values, CrimeTable.Cols.UUID + " = ?", new String[] { uuidString });
    }

    public boolean deleteCrime(Crime crime) {
        return false;
    }
}
