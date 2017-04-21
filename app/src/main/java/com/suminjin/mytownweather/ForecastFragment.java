package com.suminjin.mytownweather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.TextView;
import android.widget.Toast;

import com.suminjin.data.ApiType;
import com.suminjin.data.AppConfig;
import com.suminjin.data.DataCode;
import com.suminjin.data.DataCodeBuilder;
import com.suminjin.data.JsonField;
import com.suminjin.data.ServerConfig;
import com.suminjin.data.ServerUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by parkjisun on 2017. 4. 19..
 */

public class ForecastFragment extends Fragment {

    private TextView textViewResponse;
    private View subView;
    private ApiType apiType;

    public static ForecastFragment newInstance(int x, int y, int position) {
        ForecastFragment f = new ForecastFragment();
        Bundle args = new Bundle();
        args.putInt("x", x);
        args.putInt("y", y);
        args.putInt("position", position);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_forecast, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewResponse = (TextView) view.findViewById(R.id.textViewResponse);
        textViewResponse.setText("");

        Bundle args = getArguments();
        int x = args.getInt("x");
        int y = args.getInt("y");
        int position = args.getInt("position");

        apiType = ApiType.FORECAST_GRIB; // default
        for (ApiType t : ApiType.values()) {
            if (t.ordinal() == position) {
                apiType = t;
                break;
            }
        }

        setForecastData(x, y);

        if (position == ApiType.FORECAST_GRIB.ordinal()) {
            ViewStub viewStub = (ViewStub) view.findViewById(R.id.viewstubForecastGrib);
            subView = viewStub.inflate();
        }
    }

    /**
     * api type별로 url 구성 후 데이타 요청
     *
     * @param x
     * @param y
     */
    public void setForecastData(int x, int y) {
        // x, y가 0보다 커야 유효한 값
        if (x > 0 && y > 0) {
            String url = ServerConfig.getUrl(getActivity(), apiType, x, y);
//            Log.e("jisunLog", url);

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
                    super.onPostExecute(response);
                }
            }.execute(url);
        }
    }


    /**
     * @param apiType
     * @param responseStr
     * @return
     */
    private String parseResponse(ApiType apiType, String responseStr) {
        StringBuilder sb = new StringBuilder();

        try {
            JSONObject jsonObject = new JSONObject(responseStr);
            JSONObject response = jsonObject.getJSONObject(JsonField.RESPONSE.name);
            JSONObject header = response.getJSONObject(JsonField.HEADER.name);
            String resultCode = header.getString(JsonField.RESULT_CODE.name);

            if (resultCode != null && resultCode.equals(ServerConfig.VALID_RESULT_CODE)) {
                JSONObject body = response.getJSONObject(JsonField.BODY.name);
                String totalCount = body.getString(JsonField.TOTAL_COUNT.name);
                if (totalCount != null && Integer.parseInt(totalCount) > 0) {
                    JSONObject items = body.getJSONObject(JsonField.ITEMS.name);
                    JSONArray itemArray = items.getJSONArray(JsonField.ITEM.name);

                    ArrayList<ForecastItem> list = new ArrayList<>();
                    for (int i = 0; i < itemArray.length(); i++) {
                        JSONObject obj = (JSONObject) itemArray.get(i);
                        String category = obj.getString(JsonField.CATEGORY.name);
                        DataCode dataCode = getDataCode(category);
                        sb.append("[").append(Integer.toString(i + 1)).append(" ").append(category).append("] ");
                        switch (apiType) {
                            case FORECAST_GRIB:
                                String obsrValue = obj.getString(JsonField.OBSR_VALUE.name);
                                if (dataCode == null) {
                                    sb.append(category).append(" : ").append(obsrValue).append("\n");
                                    list.add(new ForecastItem(i, category, category, obsrValue, obsrValue));
                                } else {
                                    sb.append(dataCode.dataName).append(" : ")
                                            .append(DataCodeBuilder.getDataValue(apiType, dataCode, obsrValue)).append("\n");
                                    list.add(new ForecastItem(i, category, dataCode.dataName, obsrValue, DataCodeBuilder.getDataValue(apiType, dataCode, obsrValue)));
                                }
                                break;
                            case FORECAST_TIME_DATA:
                            case FORECAST_SPACE_DATA:
                                String fcstTime = getFormattedTimeString(obj.getString(JsonField.FCST_TIME.name));
                                String fcstValue = obj.getString(JsonField.FCST_VALUE.name);
                                if (dataCode == null) {
                                    sb.append(category)
                                            .append("(").append(fcstTime).append(")")
                                            .append(" : ").append(fcstValue).append("\n");
                                } else {
                                    sb.append(dataCode.dataName).append("(").append(fcstTime).append(")").append(" : ")
                                            .append(DataCodeBuilder.getDataValue(apiType, dataCode, fcstValue)).append("\n");
                                }
                                break;
                            default:
                        }
                    }
                    if (subView != null) {
                        RecyclerView recyclerView = (RecyclerView) subView.findViewById(R.id.recyclerView);
                        recyclerView.setAdapter(new ForecastRecyclerViewAdapter(list, getActivity()));
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
                        recyclerView.setLayoutManager(layoutManager);
                    }
                } else {
                    sb.append("no item");
                }
            } else {
                String resultMsg = header.getString(JsonField.RESULT_MSG.name);
                Toast.makeText(getActivity(), resultMsg, Toast.LENGTH_SHORT).show();
                sb.append(resultMsg);
            }
        } catch (JSONException e) {
            Log.e(AppConfig.TAG, "JSONException] " + e.toString());
        }
        return sb.toString();
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

}
