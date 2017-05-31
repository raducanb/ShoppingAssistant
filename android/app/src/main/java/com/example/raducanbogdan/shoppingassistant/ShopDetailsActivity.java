package com.example.raducanbogdan.shoppingassistant;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class ShopDetailsActivity extends AppCompatActivity
implements OnMapReadyCallback, ShoppingListAdapterProtocol,
        GoogleMap.OnMapLoadedCallback {
    Shop shop;
    ShoppingList shoppingList;
    ArrayList<ShoppingItem> itemsForCurrentShop;
    ShoppingListAdapter shoppingListAdapter;
    GeofencingManager geofencingManager;
    GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_details);

        setTitle("Detalii magazin");

        Intent intent = getIntent();
        this.shop = (Shop) intent.getSerializableExtra("shop");
        this.shoppingList = new ShoppingList();

        requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        this.geofencingManager = new GeofencingManager();

        setupItemsForCurrentShop();
        setupMapFragment();
        setupListView();
    }

    private void setupItemsForCurrentShop() {
        ArrayList<ShoppingItem> items = new ArrayList<>();
        for (ShoppingItem item : this.shoppingList.items()) {
            if (!shop.categories.contains(item.category())) {
                continue;
            }
            items.add(item);
        }
        this.itemsForCurrentShop = items;
    }

    private void setupMapFragment() {
        SupportMapFragment mMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mMapFragment.getMapAsync(this);
    }

    private void setupListView() {
        this.shoppingListAdapter = new ShoppingListAdapter(this, this.itemsForCurrentShop);
        this.shoppingListAdapter.delegate = this;
        ListView listView = (ListView) findViewById(R.id.shopping_items_list_view);
        listView.setAdapter(this.shoppingListAdapter);
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        this.map = map;
        MarkerOptions markerOptions = markerOptionsForShop(this.shop);
        Marker marker = map.addMarker(markerOptions);
        marker.showInfoWindow();

        map.setOnMapLoadedCallback(this);

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        map.setMyLocationEnabled(true);
    }

    @Override
    public void onMapLoaded() {
        centerMarkers(this.map, new ArrayList<>(Arrays.asList(markerOptionsForShop(this.shop))));
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

    private MarkerOptions markerOptionsForShop(Shop shop) {
        LatLng coord = new LatLng(shop.coordinates.get("lat").doubleValue(), shop.coordinates.get("lng").doubleValue());
        String title = shop.name;
        String subtitle = shop.categoriesNamesStiched();
        return new MarkerOptions().position(coord)
                .title(title).snippet(subtitle);
    }

    @Override
    public void didCheckItem(ShoppingItem item) {
        removeShoppingItem(item);
        checkIfNoItemsLeft();
    }

    private void removeShoppingItem(ShoppingItem item) {
        this.shoppingList.removeItem(item);
        this.itemsForCurrentShop.remove(item);
        this.shoppingListAdapter.notifyDataSetChanged();

        ArrayList<Category> remainingCategories = this.shoppingList.categories();
        boolean didRemoveLastItemWithThisCategory = !remainingCategories.contains(item.category());
        if (!didRemoveLastItemWithThisCategory) {
            return;
        }

        removeGeofencesForNeededShopsAfterItemDeleted(item, remainingCategories);
    }

    private void checkIfNoItemsLeft() {
        if (this.itemsForCurrentShop.size() > 0) { return; }

        boolean isOpenFromOnlyOneShopNotification = (this.getCallingActivity() == null);
        if (isOpenFromOnlyOneShopNotification) {
            Intent i = new Intent(ShopDetailsActivity.this, ShoppingListActivity.class);
            startActivity(i);
            this.finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra("removedShop", (Serializable)this.shop);
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    }

    private void removeGeofencesForNeededShopsAfterItemDeleted(ShoppingItem item,
                                                               ArrayList<Category> remainingCategories) {
        ArrayList<String> shopIdsToRemoveGeofence = new ArrayList<>();
        for (Shop shop : Shops.all(this)) {
            boolean shopDoesntHaveDeletedItemCategory = !shop.categories.contains(item.category());
            if (shopDoesntHaveDeletedItemCategory) {
                continue;
            }
            boolean shopHasOtherItemsCategories =
                    !Collections.disjoint(shop.categories, remainingCategories);
            if (shopHasOtherItemsCategories) {
                continue;
            }

            shopIdsToRemoveGeofence.add(shop.id);
        }

        if (shopIdsToRemoveGeofence.size() == 0) { return; }
        this.geofencingManager.removeGeofenceForShopIds(shopIdsToRemoveGeofence);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            this.geofencingManager.connectGoogleApi(this);
        }
    }
}