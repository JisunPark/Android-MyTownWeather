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
import android.widget.TextView;

import com.suminjin.appbase.CustomDialog;
import com.suminjin.appbase.ViewSwitchManager;
import com.suminjin.data.ApiType;

/**
 * Created by parkjisun on 2017. 4. 17..
 */

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final String INTENT_EXTRA_X = "intent_extra_x";
    public static final String INTENT_EXTRA_Y = "intent_extra_y";

    private static final int REQUEST_CODE_SETTINGS = 0;
    private static final int REQUEST_CODE_SEARCH = 1;

    private DrawerLayout drawer;
    private ViewPager viewPager;
    private ForecastViewPagerAdapter pagerAdapter;
    private Toolbar toolbar;

    int x = 0, y = 0;
    private TextView txtUserName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        handleLocationSearchType();

//        setCustomActionBar(); // FIXME jisun-test

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // 메뉴 아이콘 숨김
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        txtUserName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.text_user);

        // top button들 처리
        final ViewSwitchManager viewSwitchManager = new ViewSwitchManager();
        ViewSwitchManager.OnClickViewSwitchListener onTopButtonClickListener = new ViewSwitchManager.OnClickViewSwitchListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem((Integer) v.getTag());
            }
        };
        viewSwitchManager.add(0, findViewById(R.id.btnForecastSpaceData), onTopButtonClickListener);
        viewSwitchManager.add(1, findViewById(R.id.btnForecastGrib), onTopButtonClickListener);
        viewSwitchManager.add(2, findViewById(R.id.btnForecastTimeData), onTopButtonClickListener);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewSwitchManager.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        pagerAdapter = new ForecastViewPagerAdapter(getSupportFragmentManager(), x, y);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(ApiType.MAX.ordinal());

        // 시작은 첫 번째 fragment에서
        viewPager.setCurrentItem(0);
        viewSwitchManager.setSelection(0);
    }

    /**
     * 위치 선택 방식에 따라 처리
     */
    private void handleLocationSearchType() {
        // 직접 위치 선택한 상태이면 저장값 불러오고, 현위치 검색이면 gps 검색 처리.
        // 둘 다 아니면 설정 화면으로 이동한다.
        int type = AppData.get(this, AppData.KEY_LOCATION_TYPE, -1);
        String name = "";
        switch (type) {
            case AppData.LOCATION_TYPE_SELECT:
                x = AppData.get(this, AppData.KEY_X, 0);
                y = AppData.get(this, AppData.KEY_Y, 0);
                name = AppData.get(this, AppData.KEY_LOCATION_NAME, "");
                updateForecastFragment();
                break;
            case AppData.LOCATION_TYPE_SEARCH:
                goToSearch();
                name = getString(R.string.searching_current_location);
                break;
            default:
                final CustomDialog dialog = new CustomDialog(this, "알림", "동네 위치를 설정해주세요.");
                dialog.setPositiveBtn(R.string.confirm, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        goToSetting();
                    }
                });
                dialog.show();
        }

        toolbar.setSubtitle(name);
    }

    /**
     * 검색 상태로 이동
     */
    private void goToSearch() {
        Intent intent = new Intent(this, SearchActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SEARCH);
        overridePendingTransition(0, 0);
    }

    /**
     * customize action bar
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

    /**
     * a button in customized action abr
     *
     * @param v
     */
    public void onClickMenu(View v) {
        drawer.openDrawer(Gravity.START);
    }

    /**
     * a button in customized action abr
     *
     * @param v
     */
    public void onClickAdd(View v) {
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
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                goToSetting();
                break;
            case R.id.action_search:
                toolbar.setSubtitle(R.string.searching_current_location);
                AppData.put(this, AppData.KEY_LOCATION_TYPE, AppData.LOCATION_TYPE_SEARCH);
                goToSearch();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToSetting() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivityForResult(intent, REQUEST_CODE_SETTINGS);
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
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SETTINGS:
                if (resultCode == RESULT_OK) {
                    handleLocationSearchType();
                }
                break;
            case REQUEST_CODE_SEARCH:
                if (resultCode == RESULT_OK) {
                    x = data.getIntExtra(INTENT_EXTRA_X, 0);
                    y = data.getIntExtra(INTENT_EXTRA_Y, 0);
                    updateForecastFragment();
                } else {
                    toolbar.setSubtitle(R.string.failed_location_searching);
                }
                break;
            default:
        }
    }

    private void updateForecastFragment() {
        if (pagerAdapter != null) {
            for (int i = 0; i < pagerAdapter.getCount(); i++) {
                ForecastFragment f = (ForecastFragment) pagerAdapter.getRegisteredFragment(i);
                if (f != null) {
                    f.setForecastData(x, y);
                }
            }

            String name = AppData.get(this, AppData.KEY_LOCATION_NAME, "");
            if (!name.isEmpty()) {
                toolbar.setSubtitle(name);
            }
        }
    }
}

