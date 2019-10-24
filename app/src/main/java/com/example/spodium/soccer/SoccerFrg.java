package com.example.spodium.soccer;


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


public class SoccerFrg extends Fragment {

    TabLayout tabLayout;
    ViewPager pager;

    ArrayList<Fragment> frg_list = new ArrayList<Fragment>();
    ArrayList<String> title_list = new ArrayList<String>();

    public SoccerFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soccer_frg, container, false);

        tabLayout = (TabLayout)view.findViewById(R.id.tabs);
        pager = (ViewPager)view.findViewById(R.id.pager);

        if(frg_list.size() == 0 && title_list.size() == 0) {
            for (int i = 0; i < 3; i++) {
                SoccerSubFrg sub = new SoccerSubFrg();
                sub.title = (i + 1) + "번째 프래그먼트";

                frg_list.add(sub);
                title_list.add("tab" + (i + 1));
            }
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
