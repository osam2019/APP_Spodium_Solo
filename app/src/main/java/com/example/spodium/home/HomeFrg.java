package com.example.spodium.home;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.spodium.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;


public class HomeFrg extends Fragment {

    TabLayout tabLayout;
    ViewPager pager;

    ArrayList<Fragment> frg_list = new ArrayList<Fragment>();
    ArrayList<String> title_list = new ArrayList<String>();

    public HomeFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_frg, container, false);

        tabLayout = (TabLayout)view.findViewById(R.id.tabs);
        pager = (ViewPager)view.findViewById(R.id.pager);

        Topfrg top = new Topfrg();
        LiveFrg live = new LiveFrg();
        HotNewsFrg hotNews = new HotNewsFrg();


        if(frg_list.size() == 0) {
            frg_list.add(top);
            frg_list.add(live);
            frg_list.add(hotNews);
        }

        if(title_list.size() == 0) {
            title_list.add("Top20");
            title_list.add("Live");
            title_list.add("Hot News");
        }

        FragmentManager manager = getFragmentManager();
        PagerAdapter adapter = new PagerAdapter(manager);
        pager.setAdapter(adapter);
        tabLayout.setupWithViewPager(pager);

        return view;
    }

    class PagerAdapter extends FragmentPagerAdapter {
        public PagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public int getCount() {
            return frg_list.size();
        }

        @Override
        public Fragment getItem(int position) {
            return frg_list.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return title_list.get(position);
        }
    }

}
