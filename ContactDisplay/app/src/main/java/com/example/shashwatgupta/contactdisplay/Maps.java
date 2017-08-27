package com.example.shashwatgupta.contactdisplay;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * Created by Shashwat Gupta on 27-Aug-17.
 */

public class Maps extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String name, email;
    private double locationx=0,locationy=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        name=getIntent().getStringExtra("name");
        email=getIntent().getStringExtra("email");
        String[] location=getIntent().getStringExtra("location").split(" ");
        locationx=Double.parseDouble(location[0])*0.000001;
        locationy=Double.parseDouble(location[1])*0.000001;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng city = new LatLng(locationx, locationy);
        mMap.addMarker(new MarkerOptions().position(city).title(name).snippet("( "+locationx+", "+locationy+" )"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(city));
    }
}