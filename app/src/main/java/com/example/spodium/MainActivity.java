package com.example.spodium;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.spodium.home.HomeFrg;
import com.example.spodium.home.HotNewsFrg;
import com.example.spodium.home.LiveFrg;
import com.example.spodium.home.Topfrg;
import com.example.spodium.soccer.SoccerFrg;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;
    NavigationView nav_view;
    DrawerLayout drawer;

    HomeFrg homeFrg;
    SpoToFrg spoToFrg;
    SoccerFrg soccerFrg;

    String[] permission_list = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission_list, 0);
        }

        toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nav_view = (NavigationView)findViewById(R.id.nav_view);
        drawer = (DrawerLayout)findViewById(R.id.drawer_layout);

        View header_view = nav_view.getHeaderView(0);
        header_view

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);

        Topfrg top = new Topfrg();
        LiveFrg live = new LiveFrg();
        HotNewsFrg hotNews = new HotNewsFrg();

        homeFrg = new HomeFrg();
        spoToFrg = new SpoToFrg();
        soccerFrg = new SoccerFrg();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();
        tran.replace(R.id.frg_container, homeFrg);
        tran.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction tran = manager.beginTransaction();

        if(id == R.id.home) {
            tran.replace(R.id.frg_container, homeFrg);
        } else if(id == R.id.sportTogether) {
            tran.replace(R.id.frg_container, spoToFrg);
        } else if(id == R.id.soccer){
            tran.replace(R.id.frg_container, soccerFrg);
        } else if(id == R.id.baseball){

        } else if(id == R.id.basketball) {

        } else if(id == R.id.settings) {

        }
        tran.commit();

        DrawerLayout drawer = (DrawerLayout)findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
