package com.example.winestastic;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class Map_Fragment extends Fragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map_,container, false);
        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.MY_MAP);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {

                LatLng mapRedonda = new LatLng(20.639977, -99.907046);
                googleMap.addMarker(new MarkerOptions().position(mapRedonda).title("Viñedo laRedonda"));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(mapRedonda));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mapRedonda,16));

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                    @Override
                    public void onMapClick(@NonNull LatLng latLng) {
                        //MarkerOptions markerOptions = new MarkerOptions();
                        //markerOptions.position(latLng);
                        //markerOptions.title(latLng.latitude+" KG " + latLng.longitude);
                        //googleMap.clear();
                        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));
                    }
                });
            }
        });
        return view;
    }
}