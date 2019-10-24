package com.example.spodium;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class SpoToFrg extends Fragment {

    String[] permission_list = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    LocationManager locationManager;
    Context context;
    GoogleMap map;

    ArrayList<Double> lat_list = new ArrayList<Double>();
    ArrayList<Double> lng_list = new ArrayList<Double>();
    ArrayList<String> name_list = new ArrayList<String>();
    ArrayList<String> vicinity_list = new ArrayList<String>();

    ArrayList<Marker> marker_list = new ArrayList<Marker>();

    public SpoToFrg() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spo_to_frg, container, false);

        MainActivity activity = (MainActivity)getActivity();
        context = activity;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permission_list, 0);
        } else {
            init();
        }

        return  view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }
        init();
    }

    public void init() {
        SupportMapFragment mapFragment = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.map);

        SpoToFrg.MapReadyCallback callback1 = new SpoToFrg.MapReadyCallback();
        mapFragment.getMapAsync(callback1);
    }

    class MapReadyCallback implements OnMapReadyCallback {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            map = googleMap;
            getMyLocation();
        }
    }

    public void getMyLocation() {
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }

        Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location location2 = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        if (location1 != null) {
            setMyLocation(location1);
        } else {
            if (location2 != null) {
                setMyLocation(location2);
            }
        }

        SpoToFrg.GetMyLocationListener listener = new SpoToFrg.GetMyLocationListener();

        if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == true) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10f, listener);
        }
        if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true) {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10f, listener);
        }

    }

    public void setMyLocation(Location location) {
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());

        CameraUpdate update1 = CameraUpdateFactory.newLatLng(position);
        CameraUpdate update2 = CameraUpdateFactory.zoomTo(13f);

        map.moveCamera(update1);
        map.animateCamera(update2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED) {
                return;
            }
        }

        map.setMyLocationEnabled(true);

        SpoToFrg.NetworkThread thread = new SpoToFrg.NetworkThread(location.getLatitude(), location.getLongitude());
        thread.start();
    }

    class GetMyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            setMyLocation(location);
            locationManager.removeUpdates(this);
        }

        @Override
        public void onProviderDisabled(String s) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }
    }

    class NetworkThread extends Thread{
        double lat, lng;

        NetworkThread(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }

        @Override
        public void run() {
            super.run();

            OkHttpClient client = new OkHttpClient();
            Request.Builder builder = new Request.Builder();

            String site = "https://maps.googleapis.com/maps/api/place/nearbysearch/json"
                    + "?key=AIzaSyA5_A-GqGBy30sLqDxRvGPgLNAJA5N9oF0"
                    + "&location=" + lat + "," + lng
                    + "&radius=5000"
                    + "&language=ko"
                    + "&type=stadium";

            builder = builder.url(site);
            Request request = builder.build();

            SpoToFrg.Callback1 callback1 = new SpoToFrg.Callback1();
            Call call = client.newCall(request);
            call.enqueue(callback1);
        }
    }

    class Callback1 implements Callback {
        @Override
        public void onFailure(Call call, IOException e) {

        }

        @Override
        public void onResponse(Call call, Response response){
            try{
                String result = response.body().string();

                JSONObject obj = new JSONObject(result);

                String status = obj.getString("status");

                if(status.equals("OK")){
                    JSONArray results = obj.getJSONArray("results");

                    lat_list.clear();
                    lng_list.clear();
                    name_list.clear();
                    vicinity_list.clear();

                    for(int i = 0; i < results.length(); i++){
                        JSONObject obj2 = results.getJSONObject(i);

                        JSONObject geometry = obj2.getJSONObject("geometry");
                        JSONObject location = geometry.getJSONObject("location");
                        double lat2 = location.getDouble("lat");
                        double lng2 = location.getDouble("lng");

                        String name = obj2.getString("name");
                        String vicinity = obj2.getString("vicinity");

                        lat_list.add(lat2);
                        lng_list.add(lng2);
                        name_list.add(name);
                        vicinity_list.add(vicinity);
                    }
                    /*
                    for(int i = 0; i<lat_list.size(); i++){
                        double a1 = lat_list.get(i);
                        double a2 = lng_list.get(i);
                        String a3 = name_list.get(i);
                        String a4 = vicinity_list.get(i);

                        Log.d("tes123", a1 + "," + a2 + "," + a3 + "," + a4);
                    }
                     */

                    getActivity().runOnUiThread(new Runnable(){
                        public void run(){
                            for(Marker marker : marker_list){
                                marker.remove();
                            }
                            marker_list.clear();

                            for(int i = 0; i < lat_list.size(); i++){
                                double lat3 = lat_list.get(i);
                                double lng3 = lng_list.get(i);
                                String name3 = name_list.get(i);
                                String vicinity3 = vicinity_list.get(i);

                                LatLng position = new LatLng(lat3, lng3);

                                MarkerOptions option = new MarkerOptions();
                                option.position(position);

                                option.title(name3);
                                option.snippet(vicinity3);

                                /*
                                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation);
                                option.icon(bitmap);
                                 */

                                Marker marker = map.addMarker(option);
                                marker_list.add(marker);

                            }
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
