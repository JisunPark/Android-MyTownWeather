package com.suminjin.mytownweather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.suminjin.data.ApiType;
import com.suminjin.data.Config;
import com.suminjin.data.DataCode;
import com.suminjin.data.Field;
import com.suminjin.data.ServerConfig;
import com.suminjin.data.ServerUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by parkjisun on 2017. 4. 17..
 */

public class MainActivity extends FragmentActivity {

    public static final String INTENT_EXTRA_X = "intent_extra_x";
    public static final String INTENT_EXTRA_Y = "intent_extra_y";

    private TextView textViewResponse;
    private ScrollView scrollView;

    int x, y;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        x = getIntent().getIntExtra(INTENT_EXTRA_X, 0);
        y = getIntent().getIntExtra(INTENT_EXTRA_Y, 0);

        textViewResponse = (TextView) findViewById(R.id.textViewResponse);
        textViewResponse.setText("");

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        setForecastData(ApiType.FORECAST_GRIB);
    }

    private String parseResponse(ApiType apiType, String responseStr) {
        StringBuilder sb = new StringBuilder();

        try {
            JSONObject jsonObject = new JSONObject(responseStr);
            JSONObject response = jsonObject.getJSONObject(Field.RESPONSE.name);
            JSONObject header = response.getJSONObject(Field.HEADER.name);
            String resultCode = header.getString(Field.RESULT_CODE.name);

            if (resultCode != null && resultCode.equals(ServerConfig.VALID_RESULT_CODE)) {
                JSONObject body = response.getJSONObject(Field.BODY.name);
                String totalCount = body.getString(Field.TOTAL_COUNT.name);
                if (totalCount != null && Integer.parseInt(totalCount) > 0) {
                    JSONObject items = body.getJSONObject(Field.ITEMS.name);
                    JSONArray itemArray = items.getJSONArray(Field.ITEM.name);

                    for (int i = 0; i < itemArray.length(); i++) {
                        JSONObject obj = (JSONObject) itemArray.get(i);
                        String category = obj.getString(Field.CATEGORY.name);
                        DataCode dataCode = getDataCode(category);

                        sb.append("[").append(String.format("%02d", i + 1)).append(" ").append(category).append("] ");
                        switch (apiType) {
                            case FORECAST_GRIB:
                                String obsrValue = obj.getString(Field.OBSR_VALUE.name);
                                if (dataCode == null) {
                                    sb.append(category).append(" : ").append(obsrValue).append("\n");
                                } else {
                                    sb.append(dataCode.dataName).append(" : ")
                                            .append(getDataValue(apiType, dataCode, obsrValue))
                                            .append(" ").append(dataCode.unit).append("\n");
                                }
                                break;
                            case FORECAST_TIME_DATA:
                            case FORECAST_SPACE_DATA:
                                String fcstTime = getFormattedTimeString(obj.getString(Field.FCST_TIME.name));
                                String fcstValue = obj.getString(Field.FCST_VALUE.name);
                                if (dataCode == null) {
                                    sb.append(category)
                                            .append("(").append(fcstTime).append(")")
                                            .append(" : ").append(fcstValue).append("\n");
                                } else {
                                    sb.append(dataCode.dataName).append("(").append(fcstTime).append(")").append(" : ")
                                            .append(getDataValue(apiType, dataCode, fcstValue)).append(" ")
                                            .append(dataCode.unit).append("\n");
                                }
                                break;
                            default:
                        }
                    }
                } else {
                    sb.append("no item");
                }
            } else {
                String resultMsg = header.getString(Field.RESULT_MSG.name);
                Toast.makeText(this, resultMsg, Toast.LENGTH_SHORT).show();
                sb.append(resultMsg);
            }
        } catch (JSONException e) {
            Log.e(Config.TAG, "JSONException] " + e.toString());
        }
        return sb.toString();
    }

    /**
     *
     * @param valueStr
     * @param valueArray
     * @return
     */
    private String getValidValueCode(String valueStr, String[] valueArray) {
        String result = valueStr;
        int val = Integer.parseInt(valueStr);
        if (val >= 0 && val < valueArray.length) {
            result = ServerConfig.skyCodes[Integer.parseInt(valueStr)];
        }
        return result;
    }

    /**
     * data code에 따라 값을 표시
     *
     * @param apiType
     * @param dataCode
     * @param valueStr
     * @return
     */
    private String getDataValue(ApiType apiType, DataCode dataCode, String valueStr) {
        String result = valueStr;
        if (dataCode.equals(DataCode.SKY)) {
            int val = Integer.parseInt(valueStr);
            // sky code는 1~4
            if (val > 0 && val <= ServerConfig.skyCodes.length) {
                result = ServerConfig.skyCodes[Integer.parseInt(valueStr) - 1];
            }
        } else if (dataCode.equals(DataCode.PTY)) {
            result = getValidValueCode(valueStr, ServerConfig.ptyCodes);
        } else if (dataCode.equals(DataCode.RN1) || dataCode.equals(DataCode.R06)) {

        } else if (dataCode.equals(DataCode.S06)) {

        } else if (dataCode.equals(DataCode.LGT)) {
            if (apiType.equals(ApiType.FORECAST_GRIB)) { // 초단기 실황
                result = getValidValueCode(valueStr, ServerConfig.gribLgtCodes);
            } else { // 초단기 예보
                result = getValidValueCode(valueStr, ServerConfig.timeLgtCodes);
            }
        } else if (dataCode.equals(DataCode.UUU)) {
            float value = Float.parseFloat(valueStr);
            if (value > 0) {
                result = "동 " + Math.abs(value);
            } else if (value < 0) {
                result = "서 " + Math.abs(value);
            }
        } else if (dataCode.equals(DataCode.VVV)) {
            float value = Float.parseFloat(valueStr);
            if (value > 0) {
                result = "북 " + Math.abs(value);
            } else if (value < 0) {
                result = "남 " + Math.abs(value);
            }
        }
        return result;
    }

    /**
     * 시간
     *
     * @param dateStr
     * @return
     */
    private String getFormattedTimeString(String dateStr) {
        String result = dateStr;
        SimpleDateFormat sdf = new SimpleDateFormat("HHmm", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("a hh:mm", Locale.getDefault());
        try {
            Date date = sdf.parse(dateStr);
            result = sdf2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * @param categoryCode
     * @return
     */
    private DataCode getDataCode(String categoryCode) {
        DataCode dataCode = null;
        for (DataCode d : DataCode.values()) {
            if (d.name().equalsIgnoreCase(categoryCode)) {
                dataCode = d;
                break;
            }
        }
        return dataCode;
    }

    public void onClickForecastGrib(View v) {
        setForecastData(ApiType.FORECAST_GRIB);
    }

    public void onClickForecastTimeData(View v) {
        setForecastData(ApiType.FORECAST_TIME_DATA);
    }

    public void onClickForecastSpaceData(View v) {
        setForecastData(ApiType.FORECAST_SPACE_DATA);
    }

    /**
     * api type별로 url 구성 후 데이타 요청
     *
     * @param apiType
     */
    private void setForecastData(final ApiType apiType) {
        // TODO jisun : convert_xy.c 파일을 이용해 위도, 경도에서 nx, ny값을 구해야 한다.
        // 국회의사당역 37.528375, 126.917907  59/126
        String url = ServerConfig.getUrl(this, apiType, x, y);
//        String url = ServerConfig.getUrl(this, apiType, 59, 126); // FIXME jisun-test x,y
//        Log.e("jisunLog", url);
        new AsyncTask<String, String, String>() {

            @Override
            protected String doInBackground(String... params) {
                return ServerUtils.requestHttpGet(params[0], null);
            }

            @Override
            protected void onPostExecute(String response) {
                StringBuilder sb = new StringBuilder();
                sb.append("[").append(getString(apiType.nameResId)).append("]\n")
                        .append(parseResponse(apiType, response));
                textViewResponse.setText(sb.toString());
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(0, 0);
                    }
                });
                super.onPostExecute(response);
            }
        }.execute(url);
    }

}

