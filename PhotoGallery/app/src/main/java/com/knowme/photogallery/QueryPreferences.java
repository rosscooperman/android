package com.knowme.photogallery;


import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class QueryPreferences {
    private static final String PREF_SEARCH_QUERY = "searchQuery";
    private static final String PREF_LAST_RESULT_ID = "lastResultId";

    public static String getStoredQuery(Context context) {
        return getString(context, PREF_SEARCH_QUERY);
    }

    public static void setStoredQuery(Context context, String query) {
        putString(context, PREF_SEARCH_QUERY, query);
    }

    public static String getLastResultId(Context context) {
        return getString(context, PREF_LAST_RESULT_ID);
    }

    public static void setLastResultId(Context context, String lastResultId) {
        putString(context, PREF_LAST_RESULT_ID, lastResultId);
    }

    private static String getString(Context context, String key) {
        return manager(context).getString(key, null);
    }

    private static void putString(Context context, String key, String value) {
        manager(context).edit().putString(key, value).apply();
    }

    private static SharedPreferences manager(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
