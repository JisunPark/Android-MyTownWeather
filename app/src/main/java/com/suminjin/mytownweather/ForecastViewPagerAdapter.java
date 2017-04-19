package com.suminjin.mytownweather;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.suminjin.data.ApiType;
import com.suminjin.mytownweather.ForecastFragment;

/**
 * Created by parkjisun on 2017. 4. 19..
 */

public class ForecastViewPagerAdapter extends FragmentPagerAdapter {

    private int x, y;

    public ForecastViewPagerAdapter(FragmentManager fm, int x, int y) {
        super(fm);
        this.x = x;
        this.y = y;
    }

    @Override
    public Fragment getItem(int position) {
        return ForecastFragment.newInstance(x, y, position);
    }

    @Override
    public int getCount() {
        return ApiType.MAX.ordinal();
    }
}
