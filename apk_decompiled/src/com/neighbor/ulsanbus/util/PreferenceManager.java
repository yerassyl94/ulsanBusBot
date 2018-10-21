package com.neighbor.ulsanbus.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.neighbor.ulsanbus.C0258R;

public class PreferenceManager {
    public static final String PREF_KEY_SESSION_TIME = "session_time";
    public static final String PREF_KEY_SESSION_TOKEN = "session_token";
    private static PreferenceManager instance;

    private PreferenceManager() {
    }

    public static PreferenceManager getInstance() {
        if (instance == null) {
            instance = new PreferenceManager();
        }
        return instance;
    }

    public void savePreference(Context context, String[] keyArray, String[] valueArray) {
        Editor editor = context.getApplicationContext().getSharedPreferences(context.getString(C0258R.string.app_name), 0).edit();
        for (int i = 0; i < keyArray.length; i++) {
            editor.putString(keyArray[i], valueArray[i]);
        }
        editor.apply();
        editor.clear();
    }

    public String[] loadPreference(Context context, String[] keyArray) throws NullPointerException {
        if (context == null) {
            return null;
        }
        String[] saResult = new String[keyArray.length];
        SharedPreferences pref = context.getApplicationContext().getSharedPreferences(context.getString(C0258R.string.app_name), 0);
        for (int i = 0; i < keyArray.length; i++) {
            saResult[i] = pref.getString(keyArray[i], "");
        }
        return saResult;
    }

    public String getStringPreference(Context context, String key) {
        return context.getApplicationContext().getSharedPreferences("chat", 0).getString(key, "");
    }

    public void setStringPreference(Context context, String key, String value) {
        Editor editor = context.getSharedPreferences("chat", 0).edit();
        editor.putString(key, value);
        editor.commit();
    }
}
