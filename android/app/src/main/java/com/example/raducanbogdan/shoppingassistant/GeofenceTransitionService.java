package com.example.raducanbogdan.shoppingassistant;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by raducanbogdan on 1/18/17.
 */

public class GeofenceTransitionService extends IntentService {
    private static final String TAG = GeofenceTransitionService.class.getSimpleName();
    public static final int GEOFENCE_NOTIFICATION_ID = 0;

    public GeofenceTransitionService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);

        if ( geofencingEvent.hasError() ) {
            String errorMsg = getErrorString(geofencingEvent.getErrorCode() );
            Log.e(TAG, errorMsg);
            return;
        }

        int geoFenceTransition = geofencingEvent.getGeofenceTransition();
        if ( geoFenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER ||
                geoFenceTransition == Geofence.GEOFENCE_TRANSITION_EXIT ) {
            List<Geofence> triggeringGeofences = geofencingEvent.getTriggeringGeofences();
            ArrayList<String> ids = geofencesIds(triggeringGeofences);
            sendNotification(ids, messageFromGeofencesIds(ids));
        }
    }

    private ArrayList<String> geofencesIds(List<Geofence> geofences) {
        ArrayList<String> ids = new ArrayList<>();
        for (Geofence geofence : geofences) {
            ids.add(geofence.getRequestId());
        }
        return ids;
    }

    private String messageFromGeofencesIds(ArrayList<String> ids) {
        ArrayList<Shop> shops = Shops.all(getApplicationContext());
        ArrayList<String> filteredShopsNames = new ArrayList<>();
        for (Shop shop : shops) {
            if (!ids.contains(shop.id)) { continue; }
            filteredShopsNames.add(shop.name);
        }
        StringBuilder message = new StringBuilder();
        message.append("Esti aproape de ");
        message.append(filteredShopsNames.size() == 1 ? "magazinul" : "magazinele");
        message.append(" ");
        message.append(TextUtils.join(", ", filteredShopsNames) + ".");
        return message.toString();
    }

    private void sendNotification(ArrayList<String> shopIds, String msg) {
        Intent notificationIntent = NearShopsActivity.makeNotificationIntent(getApplicationContext());

        notificationIntent.putExtra("shops_ids", shopIds);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(NearShopsActivity.class);
        stackBuilder.addNextIntent(notificationIntent);
        PendingIntent notificationPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationManager notificationMng =
                (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        notificationMng.notify(
                GEOFENCE_NOTIFICATION_ID,
                createNotification(msg, notificationPendingIntent));
    }

    private Notification createNotification(String msg, PendingIntent notificationPendingIntent) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        notificationBuilder
                .setSmallIcon(R.drawable.notification_bell)
                .setColor(Color.RED)
                .setContentTitle(msg)
                .setContentText("Aici sunt produse din lista ta de shopping.")
                .setContentIntent(notificationPendingIntent)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_SOUND)
                .setAutoCancel(true);
        return notificationBuilder.build();
    }

    private static String getErrorString(int errorCode) {
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return "GeoFence not available";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return "Too many GeoFences";
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return "Too many pending intents";
            default:
                return "Unknown error.";
        }
    }
}
