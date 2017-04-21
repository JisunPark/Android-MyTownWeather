package com.suminjin.mytownweather;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.suminjin.data.ApiType;
import com.suminjin.mytownweather.ForecastFragment;

/**
 * Created by parkjisun on 2017. 4. 19..
 */

public class ForecastViewPagerAdapter extends FragmentPagerAdapter {
    private SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
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

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
