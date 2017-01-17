package com.example.raducanbogdan.shoppingassistant;

import android.app.PendingIntent;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by raducanbogdan on 1/17/17.
 */

public class GeofencingManager {
    public void addGeofencesForShopsThatHaveCategory(String prodId, ArrayList<Shop> shops, Category category) {
        ArrayList<Shop> shopsWithCategory = shopsThatHaveCategory(shops, category);

    }

    private ArrayList<Shop> shopsThatHaveCategory(ArrayList<Shop> shops, Category category) {
        ArrayList<Shop> shopsWithCategory = new ArrayList<>();
        for (Shop shop : shops) {
            if (!shop.categories.contains(category)) { continue; }
            shopsWithCategory.add(shop);
        }
        return shopsWithCategory;
    }

    private void addGeofencesForShops(String prodId, ArrayList<Shop> shops) {
        ArrayList<Geofence> geofences = geofencesForShops(prodId, shops);

    }

    private GeofencingRequest geofencingRequestForGeofences(ArrayList<Geofence> geofences) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofences);
        return builder.build();
    }

    private ArrayList<Geofence> geofencesForShops(String prodId, ArrayList<Shop> shops) {
        ArrayList<Geofence> geofences = new ArrayList<>();
        for (Shop shop : shops) {
            geofences.add(geofenceForShop(prodId, shop));
        }
        return geofences;
    }

    private Geofence geofenceForShop(String prodId, Shop shop) {
        return new Geofence.Builder()
                .setRequestId(prodId)
                .setCircularRegion(
                        shop.coordinates["lat"],
                        shop.coordinates["lng"],
                        100
                )
                .setExpirationDuration(Constants.GEOFENCE_EXPIRATION_IN_MILLISECONDS)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();
    }

    private PendingIntent getGeofencePendingIntent() {
        Intent intent = new Intent(this, GeofenceTransitionsIntentService.class);
        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when
        // calling addGeofences() and removeGeofences().
        return PendingIntent.getService(this, 0, intent, PendingIntent.
                FLAG_UPDATE_CURRENT);
    }
}
