package com.add.vpn;

import android.content.Context;
import android.content.SharedPreferences;
public class SettingsManager {
    private static final String PREFS_NAME = "MyPrefs";
    private static final String KEY_ALARM = "alarm";
    private static final String KEY_ERROR = "error";

    public static boolean getAlarmSetting(Context context, Boolean getDefaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Проверяем, есть ли ключ KEY_ALARM в файле настроек
        if (!sharedPreferences.contains(KEY_ALARM)) {
            // Если ключ отсутствует, то записываем значение по умолчанию
            setAlarmSetting(context, getDefaultValue); // Значение по умолчанию true
        }
        return sharedPreferences.getBoolean(KEY_ALARM, true);
    }

    public static boolean getErrorSetting(Context context, Boolean getDefaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // Проверяем, есть ли ключ KEY_ERROR в файле настроек
        if (!sharedPreferences.contains(KEY_ERROR)) {
            // Если ключ отсутствует, то записываем значение по умолчанию
            setErrorSetting(context, getDefaultValue); // Значение по умолчанию false
        }
        return sharedPreferences.getBoolean(KEY_ERROR, true);
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
}

