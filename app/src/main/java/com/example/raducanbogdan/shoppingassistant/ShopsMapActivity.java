package com.example.raducanbogdan.shoppingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by raducanbogdan on 1/17/17.
 */

public class ShopsMapActivity
        extends AppCompatActivity
        implements OnMapReadyCallback {
    private ArrayList<Shop> shopsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shops_map);

        this.shopsList = Shops.all(this);
        setupMapFragment();
    }

    private void setupMapFragment() {
        SupportMapFragment mMapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        ArrayList<MarkerOptions> markerOptionses = markerOptionsesForShops(this.shopsList);
        showMarkers(map, markerOptionses);
        centerMarkers(map, markerOptionses);
    }

    private void showMarkers(GoogleMap map, ArrayList<MarkerOptions> markerOptionses) {
        for (MarkerOptions markerOptions : markerOptionses) {
            map.addMarker(markerOptions);
        }
    }

    private void centerMarkers(GoogleMap map, ArrayList<MarkerOptions> markerOptionses) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (MarkerOptions markerOptions : markerOptionses) {
            builder.include(markerOptions.getPosition());
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        map.animateCamera(cu);
    }

    private ArrayList<MarkerOptions> markerOptionsesForShops(ArrayList<Shop> shops) {
        ArrayList<MarkerOptions> markerOptionses = new ArrayList<>();
        for (Shop shop : shops) {
            markerOptionses.add(markerOptionsForShop(shop));
        }
        return markerOptionses;
    }

    private MarkerOptions markerOptionsForShop(Shop shop) {
        LatLng coord = new LatLng(shop.coordinates.get("lat").doubleValue(), shop.coordinates.get("lng").doubleValue());
        String title = shop.name;
        String subtitle = shop.categoriesNamesStiched();
        return new MarkerOptions().position(coord)
                .title(title).snippet(subtitle);
    }
}
