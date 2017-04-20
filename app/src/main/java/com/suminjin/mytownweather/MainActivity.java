package com.suminjin.mytownweather;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.suminjin.data.ApiType;

import java.util.ArrayList;

/**
 * Created by parkjisun on 2017. 4. 17..
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String INTENT_EXTRA_X = "intent_extra_x";
    public static final String INTENT_EXTRA_Y = "intent_extra_y";
    public static final String INTENT_EXTRA_NEW_SETTING = "new_setting";

    private static final int REQUEST_CODE_SETTINGS = 0;

    private DrawerLayout drawer;
    private View selectedTopButton;

    int x, y;
    private ViewPager viewPager;

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

        final ArrayList<View> topButtonList = new ArrayList<>();
        topButtonList.add(findViewById(R.id.btnForecastGrib));
        topButtonList.add(findViewById(R.id.btnForecastTimeData));
        topButtonList.add(findViewById(R.id.btnForecastSpaceData));

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setSelectedTopButton(topButtonList.get(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(new ForecastViewPagerAdapter(getSupportFragmentManager(), x, y));
        viewPager.setOffscreenPageLimit(ApiType.MAX.ordinal());
        onClickTopButton(findViewById(R.id.btnForecastGrib));
    }


    /**
     * 상단 버튼 처리
     *
     * @param v
     */
    public void onClickTopButton(View v) {
        switch (v.getId()) {
            case R.id.btnForecastGrib:
                viewPager.setCurrentItem(ApiType.FORECAST_GRIB.ordinal());
                break;
            case R.id.btnForecastTimeData:
                viewPager.setCurrentItem(ApiType.FORECAST_TIME_DATA.ordinal());
                break;
            case R.id.btnForecastSpaceData:
                viewPager.setCurrentItem(ApiType.FORECAST_SPACE_DATA.ordinal());
                break;
            default:
        }

        // 선택 버튼 상태 변경
        setSelectedTopButton(v);
    }

    private void setSelectedTopButton(View v) {
        if (v != null) {
            if (selectedTopButton != null) {
                selectedTopButton.setSelected(false);
            }
            selectedTopButton = v;
            v.setSelected(true);
        }
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
            // TODO jisun - setting 화면으로 이동
//            Intent intent = new Intent(this, SettingActivity.class);
//            intent.putExtra(INTENT_EXTRA_NEW_SETTING, true);
//            startActivityForResult(intent, REQUEST_CODE_SETTINGS);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SETTINGS:
                if (resultCode == RESULT_OK) {
                    // TODO jisun 변경된 지역으로 다시 api 호출하고 화면 갱신하기
                    Toast.makeText(this, "위치 변경됨", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "위치 안 변경됨", Toast.LENGTH_SHORT).show();
                }
        }
    }
}

