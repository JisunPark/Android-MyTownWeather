package com.suminjin.data;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by parkjisun on 2017. 4. 17..
 */

public class ServerConfig {
    private static final String SCHEME = "http";
    public static final String URL = "newsky2.kma.go.kr";
    public static final String BASE_PATH = "service/SecndSrtpdFrcstInfoService2/";

    private static final String SERVICE_KEY = "ServiceKey";
    private static final String BASE_DATE = "base_date";
    private static final String BASE_TIME = "base_time";
    private static final String NX = "nx";
    private static final String NY = "ny";
    private static final String PAGE_NO = "pageNo";
    private static final String NUM_OF_ROWS = "numOfRows";
    private static final String TYPE = "_type";

    private static final String TYPE_JSON = "json";
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final int DEFAULT_PAGE = 1; // 1 page부터 시작
    private static final int DEFAULT_NUM_OF_ROWS = 100;

    public static final String VALID_RESULT_CODE = "0000";

    private static String serviceKey = null;

    public static String[] validSpaceBaseTime = new String[]{"0200", "0500", "0800", "1100", "1400", "1700", "2000", "2300"};
    public static String[] skyCodes = new String[]{"맑음", "구름조금", "구름많음", "흐림"};
    public static String[] ptyCodes = new String[]{"없음", "비", "비/눈", "눈"};
    public static String[] gribLgtCodes = new String[]{"없음", "있음"};
    public static String[] timeLgtCodes = new String[]{"확률없음", "낮음", "보통", "높음"};

    /**
     * api 종류에 따른 url 가져오기
     *
     * @param context
     * @param apiType
     * @param nx
     * @param ny
     * @return
     */
    public static String getUrl(Context context, ApiType apiType, int nx, int ny) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HHmm", Locale.getDefault());
        String[] temp = sdf.format(new Date()).split(" ");
        String baseDate = temp[0];
        String baseTime = temp[1];

        if (apiType == ApiType.FORECAST_GRIB) {
            // 초단기실황은 정각에서 40분이후에 api 제공
            int baseTimeValue = Integer.parseInt(baseTime);
            int hour = baseTimeValue / 100; // 앞쪽 두 자리가 시간
            if ((baseTimeValue % 100) < 40) {
                // 40분이전이면 1시간 전의 정보를
                if (hour > 0) {
                    --hour;
                } else {
                    // TODO jisun : 0시 이전에는, 전날의 마지막 정보를 이용해야 하지만 일단 보류
                }
            }
            baseTime = String.format("%02d00", hour);
        } else if (apiType == ApiType.FORECAST_TIME_DATA) {
            // 초단기예보는 45분 이후 api 제공
            int baseTimeValue = Integer.parseInt(baseTime);
            int hour = baseTimeValue / 100; // 앞쪽 두 자리가 시간
            if ((baseTimeValue % 100) < 45) {
                // 40분이전이면 1시간 전의 정보를
                if (hour > 0) {
                    --hour;
                } else {
                    // TODO jisun : 0시 이전에는, 전날의 마지막 정보를 이용해야 하지만 일단 보류
                }
            }
            baseTime = String.format("%02d00", hour);
        } else if (apiType == ApiType.FORECAST_SPACE_DATA) {
            // 동네예보조회는 특정 시간값으로만 조회 가능함
            String result = null;
            int baseTimeValue = Integer.parseInt(baseTime);
            for (String t : validSpaceBaseTime) {
                int tValue = Integer.parseInt(t) + 10; // 특정 시간의 10분 이후에 api 제공
                if (baseTimeValue > tValue) {
                    result = t;
                } else {
                    break;
                }
            }
            if (result != null) {
                baseTime = result;
            }
        }
        android.util.Log.e("jisunLog", apiType.name() + " 호출 시간 ] " + baseDate + " " + baseTime);
        return buildUrl(context, apiType.path, baseDate, baseTime, nx, ny, DEFAULT_PAGE, DEFAULT_NUM_OF_ROWS);
    }

    /**
     * parameter 값으로 url을 생성한다.
     *
     * @param path
     * @param date
     * @param time
     * @param nx
     * @param ny
     * @param pageNo
     * @param numOfRows
     * @return
     */
    private static String buildUrl(Context context, String path, String date, String time, int nx, int ny, int pageNo, int numOfRows) {
        Uri uri = new Uri.Builder()
                .scheme(SCHEME)
                .authority(URL)
                .path(path)
                .appendQueryParameter(SERVICE_KEY, getServiceKey(context))
                .appendQueryParameter(BASE_DATE, date) //yyyyMMdd
                .appendQueryParameter(BASE_TIME, time) // hhmm
                .appendQueryParameter(NX, Integer.toString(nx))
                .appendQueryParameter(NY, Integer.toString(ny))
                .appendQueryParameter(PAGE_NO, Integer.toString(pageNo))
                .appendQueryParameter(NUM_OF_ROWS, Integer.toString(numOfRows))
                .appendQueryParameter(TYPE, TYPE_JSON)
                .build();
        return uri.toString();
    }

    /**
     * 공공데이타 api 서비스키 가져오기
     * TODO jisun : url encoding을 하라더니 decoding을 해야 제대로 응답한다.
     *
     * @param context
     * @return
     */
    private static String getServiceKey(Context context) {
        if (serviceKey == null) {
            serviceKey = context.getString(R.string.service_key);

//            try {
//                serviceKey = URLEncoder.encode(context.getString(R.string.service_key), DEFAULT_CHARSET);
//            } catch (UnsupportedEncodingException e) {
//                Log.d(Config.TAG, "UnsupportedEncodingException] " + e.toString());
//            }
//            Log.e(Config.TAG, "Encoded serviceKey] " + serviceKey);

            try {
                serviceKey = URLDecoder.decode(context.getString(R.string.service_key), DEFAULT_CHARSET);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return serviceKey;
    }
}
