package com.example.raducanbogdan.shoppingassistant;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import static com.example.raducanbogdan.shoppingassistant.GeofencingManager.kShopRadius;

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

        setTitle("Harta magazine");

        this.shopsList = Shops.all(this);
        setupMapFragment();
    }

    private void setupMapFragment() {
        SupportMapFragment mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        ArrayList<MarkerOptions> markerOptionses = markerOptionsesForShops(this.shopsList);
        final ArrayList<Marker> markers = showMarkers(map, markerOptionses);
        centerMarkers(map, markerOptionses);

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                int pos = markers.indexOf(marker);
                didSelectShopAtPosition(pos);
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
    }

    private void didSelectShopAtPosition(int position) {
        Shop shop = this.shopsList.get(position);
        showDetailsForShop(shop);
    }

    private void showDetailsForShop(Shop shop) {
        Intent i = new Intent(ShopsMapActivity.this, ShopDetailsActivity.class);
        i.putExtra("shop", shop);
        startActivity(i);
    }

    private ArrayList<Marker> showMarkers(GoogleMap map, ArrayList<MarkerOptions> markerOptionses) {
        ArrayList<Marker> markers = new ArrayList<>();
        for (MarkerOptions markerOptions : markerOptionses) {
            Marker marker = map.addMarker(markerOptions);
            markers.add(marker);
            map.addCircle(circleOptionsForMarker(markerOptions));
        }
        return markers;
    }

    private CircleOptions circleOptionsForMarker(MarkerOptions markerOptions) {
        return new CircleOptions()
                .center(new LatLng(markerOptions.getPosition().latitude,
                        markerOptions.getPosition().longitude))
                .radius(kShopRadius);
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
