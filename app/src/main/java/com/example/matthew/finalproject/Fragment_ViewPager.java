package com.example.matthew.finalproject;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;


public class Fragment_ViewPager extends Fragment {

    MyStateFragmentPagerAdapter myPagerAdapter;
    public static ViewPager mViewPager;
    static int page;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    public static Fragment_ViewPager newInstance(int i) {
        page = i;
        Fragment_ViewPager fragment = new Fragment_ViewPager();
        return fragment;
    }

    public Fragment_ViewPager() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_view_pager, container, false);
        myPagerAdapter = new MyStateFragmentPagerAdapter(getFragmentManager(), 2, getActivity());

        mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
        customizeViewPager();

        mViewPager.setAdapter(myPagerAdapter);
        mViewPager.setCurrentItem(page);
        return rootView;
    }

    public void customizeViewPager() {
        mViewPager.setPageTransformer(false, new ViewPager.PageTransformer() {
            @Override
            public void transformPage(View page, float position) {
                final float normalized_position = Math.abs(Math.abs(position) - 1);
                page.setScaleX(normalized_position / 2 + 0.5f);
                page.setScaleY(normalized_position / 2 + 0.5f);

            }
        });

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            public void onPageSelected(int position) {

            }

            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}