package com.example.raducanbogdan.shoppingassistant;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.Arrays;

import static com.google.android.gms.location.Geofence.NEVER_EXPIRE;

/**
 * Created by raducanbogdan on 1/17/17.
 */

public class GeofencingManager
        implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        ResultCallback {
    static PendingIntent mGeofencePendingIntent;
    private GoogleApiClient googleApiClient;
    public static int kShopRadius = 300;

    public void removeGeofenceForShopIds(ArrayList<String> shopIds) {
        LocationServices.GeofencingApi.removeGeofences(this.googleApiClient, shopIds)
                .setResultCallback(this);
    }

    public void addGeofencesForShopsThatHaveCategory(Context context, ArrayList<Shop> shops,
                                                     Category category) {
        ArrayList<Shop> shopsWithCategory = shopsThatHaveCategory(shops, category);
        ArrayList<Geofence> geofences = geofencesForShops(shopsWithCategory);

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.GeofencingApi.addGeofences(
                this.googleApiClient,
                geofencingRequestForGeofences(geofences),
                getGeofencePendingIntent(context))
                .setResultCallback(this);
    }

    public void connectGoogleApi(Context context) {
        createGoogleApi(context);
        this.googleApiClient.connect();
    }

    public void stopGoogleApi() {
        this.googleApiClient.disconnect();
    }

    private ArrayList<Shop> shopsThatHaveCategory(ArrayList<Shop> shops, Category category) {
        ArrayList<Shop> shopsWithCategory = new ArrayList<>();
        for (Shop shop : shops) {
            if (!shop.categories.contains(category)) { continue; }
            shopsWithCategory.add(shop);
        }
        return shopsWithCategory;
    }


    private void createGoogleApi(Context context) {
        if (this.googleApiClient != null) { return; }
        this.googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private GeofencingRequest geofencingRequestForGeofences(ArrayList<Geofence> geofences) {
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofences(geofences);
        return builder.build();
    }

    private ArrayList<Geofence> geofencesForShops(ArrayList<Shop> shops) {
        ArrayList<Geofence> geofences = new ArrayList<>();
        for (Shop shop : shops) {
            geofences.add(geofenceForShop(shop));
        }
        return geofences;
    }

    private Geofence geofenceForShop(Shop shop) {
        return new Geofence.Builder()
                .setRequestId(shop.id)
                .setCircularRegion(
                        shop.coordinates.get("lat").doubleValue(),
                        shop.coordinates.get("lng").doubleValue(),
                        kShopRadius
                )
                .setExpirationDuration(NEVER_EXPIRE)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER)
                .build();
    }

    private PendingIntent getGeofencePendingIntent(Context context) {
        if (mGeofencePendingIntent != null) {
            return mGeofencePendingIntent;
        }

        Intent intent = new Intent(context, GeofenceTransitionService.class);
        mGeofencePendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.
                    FLAG_UPDATE_CURRENT);
        return mGeofencePendingIntent;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i("1", "onConnected()");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("1", "onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i("1", "onConnectionFailed()");
    }

    @Override
    public void onResult(@NonNull Result result) {
        Log.i("1", "results() " + result);
    }
}
