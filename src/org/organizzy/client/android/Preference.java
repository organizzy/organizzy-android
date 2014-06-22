package org.organizzy.client.android;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preference {
    private static Preference instance = null;


    private SharedPreferences sharedPreferences;
    private Preference(Context ctx) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void initialize(Context ctx) {
        instance = new Preference(ctx);
    }

    public static String getString(String name, String defaultValue) {
        return instance.sharedPreferences.getString(name, defaultValue);
    }

    public static boolean getBoolean(String name, boolean defaultValue) {
        return instance.sharedPreferences.getBoolean(name, defaultValue);
    }

    public static void set(String name, boolean value) {
        instance.sharedPreferences.edit().putBoolean(name, value).commit();
    }


}
