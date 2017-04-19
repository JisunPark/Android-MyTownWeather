package com.suminjin.mytownweather;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.suminjin.data.ApiType;
import com.suminjin.data.AppConfig;
import com.suminjin.data.DataCode;
import com.suminjin.data.DataCodeBuilder;
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

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String INTENT_EXTRA_X = "intent_extra_x";
    public static final String INTENT_EXTRA_Y = "intent_extra_y";

    private TextView textViewResponse;
    private ScrollView scrollView;
    private DrawerLayout drawer;

    int x, y;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        setCustomActionBar(); // FIXME jisun-test

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        x = getIntent().getIntExtra(INTENT_EXTRA_X, 0);
        y = getIntent().getIntExtra(INTENT_EXTRA_Y, 0);

        textViewResponse = (TextView) findViewById(R.id.textViewResponse);
        textViewResponse.setText("");

        scrollView = (ScrollView) findViewById(R.id.scrollView);

        setForecastData(ApiType.FORECAST_GRIB);
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
                        sb.append("[").append(category).append("] ");
                        switch (apiType) {
                            case FORECAST_GRIB:
                                String obsrValue = obj.getString(Field.OBSR_VALUE.name);
                                if (dataCode == null) {
                                    sb.append(category).append(" : ").append(obsrValue).append("\n");
                                } else {
                                    sb.append(dataCode.dataName).append(" : ")
                                            .append(DataCodeBuilder.getDataValue(apiType, dataCode, obsrValue)).append("\n");
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
                                            .append(DataCodeBuilder.getDataValue(apiType, dataCode, fcstValue)).append("\n");
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

    /**
     *
     */
    private void setCustomActionBar() {
        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);

        // set custom view layout
        View customView = LayoutInflater.from(this).inflate(R.layout.layout_action_bar, null);
        actionBar.setCustomView(customView);

        // set no padding both side
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);

        // set action bar background image
//        actionBar.setBackgroundDrawable(new ColorDrawable(Color.rgb(0xea, 0xbd, 0x00)));

        // set action bar layout params
        ActionBar.LayoutParams p = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(customView, p);
    }

    public void onClickMenu(View v) {
        drawer.openDrawer(Gravity.START);
    }

    public void onClickAdd(View v) {
        // TODO jisun : search wifi-direct devices
        Toast.makeText(this, "search wifi-direct devices", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

