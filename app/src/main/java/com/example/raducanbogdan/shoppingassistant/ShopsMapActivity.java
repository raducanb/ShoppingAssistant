package com.example.raducanbogdan.shoppingassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
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
        setContentView(R.layout.activity_add_shopping_item);

        Intent i = getIntent();
        this.shopsList = (ArrayList<Shop>)i.getSerializableExtra("shops");
        setupMapFragment();
    }

    private void setupMapFragment() {
        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.shops_map_fragment));
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        showPinsForShops(map, this.shopsList);
    }

    private void showPinsForShops(GoogleMap map, ArrayList<Shop> shops) {
        ArrayList<MarkerOptions> markerOptionses = markerOptionsesForShops(shops);
        for (MarkerOptions markerOptions : markerOptionses) {
            map.addMarker(markerOptions);
        }
    }

    private ArrayList<MarkerOptions> markerOptionsesForShops(ArrayList<Shop> shops) {
        ArrayList<MarkerOptions> markerOptionses = new ArrayList<>();
        for (Shop shop : shops) {
            markerOptionses.add(markerOptionsForShop(shop));
        }
        return markerOptionses;
    }

    private MarkerOptions markerOptionsForShop(Shop shop) {
        LatLng coord = new LatLng(shop.coordinates["lat"], shop.coordinates["lng"]);
        String title = shop.name;
        String subtitle = shop.categoriesNamesStiched();
        return new MarkerOptions().position(coord)
                .title(title).snippet(subtitle);
    }
}
