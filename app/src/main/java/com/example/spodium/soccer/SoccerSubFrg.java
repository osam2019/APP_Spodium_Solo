package com.example.spodium.soccer;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.spodium.R;


public class SoccerSubFrg extends Fragment {

    String title;

    public SoccerSubFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_soccer_sub_frg, container, false);

        TextView textView = (TextView)view.findViewById(R.id.soccer_title);
        textView.setText(title);

        return view;
    }

}
