package com.example.matthew.finalproject;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;


import java.util.Locale;

public class MyStateFragmentPagerAdapter extends FragmentStatePagerAdapter {
    int count;
    Context context;

    public MyStateFragmentPagerAdapter(FragmentManager fm, int size, Context c) {
        super(fm);
        count = size;
        this.context = c;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = Fragment_RecyclerView.newInstance(0);
                break;
            case 1:
                fragment = Fragment_CalendarView.newInstance();
                break;

            default:
                fragment = Fragment_RecyclerView.newInstance(0);
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Locale l = Locale.getDefault();
        String name;
        switch (position) {
            case 0:
                name = "Up Coming Events";
                break;
            case 1:
                name = "Calendar";
                break;
            default:
                name = "Glitch";
                break;
        }
        return name.toUpperCase(l);
    }

}
