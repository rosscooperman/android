package com.knowme.photogallery;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "searchQuery";

    public static String getStoredQuery(Context context) {
        return manager(context).getString(PREF_SEARCH_QUERY, null);
    }

    public static void setStoredQuery(Context context, String query) {
        manager(context).edit().putString(PREF_SEARCH_QUERY, query).apply();
    }

    private static SharedPreferences manager(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
