package com.suminjin.mytownweather;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by parkjisun on 2017. 4. 21..
 */

public class AppData {

    private static final String SHARED_PREF_NAME = "location";

    // 직접선택 or 현위치검색
    public static final String KEY_LOCATION_TYPE = "location_type";
    public static final int LOCATION_TYPE_SELECT = 0;
    public static final int LOCATION_TYPE_SEARCH = 1;

    // 위치 좌표
    public static final String KEY_LOCATION_NAME = "location_name"; // 선택 지역 이름
    public static final String KEY_X = "location_x"; // 직접선택 지역의 x
    public static final String KEY_Y = "location_y"; // 직접선택 지역의 y

    // gps 켜는 것에 대해 이미 물어봤는지 여부
    public static final String KEY_CHECKED_GPS = "checked_gps";

    // 로그인 정보
    public static final String KEY_ID = "id";

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
