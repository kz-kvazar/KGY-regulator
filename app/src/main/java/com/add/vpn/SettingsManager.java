package com.add.vpn;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsManager {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_ALARM = "alarm";
    private static final String KEY_ERROR = "error";
    private static final String KEY_SERVER_TIME = "server";

    public static boolean getAlarmSetting(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_ALARM, true); // Значение по умолчанию true
    }

    public static boolean getErrorSetting(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(KEY_ERROR, true); // Значение по умолчанию false
    }

    public static void setAlarmSetting(Context context, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ALARM, value);
        editor.apply();
    }

    public static void setErrorSetting(Context context, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_ERROR, value);
        editor.apply();
    }
    public static void setServerUnixTime(Context context, Long value){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(KEY_SERVER_TIME, value);
        editor.apply();
    }
    public static Long getServerUnixTime(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(KEY_SERVER_TIME, 0L); // Значение по умолчанию
    }
}
