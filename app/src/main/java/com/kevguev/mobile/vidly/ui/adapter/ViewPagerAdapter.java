package com.kevguev.mobile.vidly.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import com.kevguev.mobile.vidly.ui.FavoritesFragment;
import com.kevguev.mobile.vidly.ui.MainActivity;
import com.kevguev.mobile.vidly.ui.MainListFragment;
import com.kevguev.mobile.vidly.ui.MapLocationsFragment;

import java.util.ArrayList;
import java.util.List;

import static com.kevguev.mobile.vidly.Constants.EXTRA_LOCATION;

/**
 * Created by Kevin Guevara on 6/19/2017.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    private MainActivity activity;
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    SparseArray<Fragment> registeredFragments = new SparseArray<>();
    private String currentLocation;

    public ViewPagerAdapter(MainActivity activity, FragmentManager manager, String currentLocation) {
        super(manager);
        this.activity = activity;
        this.currentLocation = currentLocation;
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

    @Override
    public Fragment getItem(int position) {
        String location = currentLocation;
        Fragment fragment;
        Bundle bundle;
        switch (position) {
            case 0:
                fragment = new MainListFragment();
                break;
            case 1:
                fragment = new MapLocationsFragment();
                bundle = new Bundle();
                bundle.putString(EXTRA_LOCATION, location);
                fragment.setArguments(bundle);
                break;
            case 2:
                fragment = new FavoritesFragment();
                break;
            default:
                fragment = null;
                break;
        }
        return fragment;
        //return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }
}
