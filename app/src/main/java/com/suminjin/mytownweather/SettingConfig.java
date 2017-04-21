package com.suminjin.mytownweather;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by parkjisun on 2017. 4. 21..
 */

public class SettingConfig {
    private static final String SHARED_PREF_NAME = "location";

    public static final String KEY_TYPE = "location_type"; // 직접선택 or 현위치검색
    public static final int TYPE_SELECT = 0;
    public static final int TYPE_SEARCH = 1;

    public static final String KEY_NAME = "location_name"; // 선택 지역 이름
    public static final String KEY_X = "location_x"; // 직접선택 지역의 x
    public static final String KEY_Y = "location_y"; // 직접선택 지역의 y

    public static final String KEY_CHECKED_GPS = "checked_gps"; // gps 켜는 것에 대해 이미 물어봤는지


    public static boolean get(Context context, String name, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(name, defaultValue);
    }

    public static String get(Context context, String name, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(name, defaultValue);
    }

    public static int get(Context context, String name, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(name, defaultValue);
    }

    public static void put(Context context, String name, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    public static void put(Context context, String name, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(name, value);
        editor.commit();
    }

    public static void put(Context context, String name, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(name, value);
        editor.commit();
    }
}
