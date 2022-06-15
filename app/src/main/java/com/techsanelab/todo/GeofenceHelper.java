package com.techsanelab.todo;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceHelper extends ContextWrapper {

    private static final String TAG = "GeofenceHelper";
    public static float GEOFENCE_RADIUS = 140;
    PendingIntent pendingIntent;

    public GeofenceHelper(Context base) {
        super(base);
    }

    public Geofence getGeofence(String id, LatLng latLng) {
        Log.d(TAG, "getGeofence");
        return new Geofence.Builder().setRequestId(id)
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .setCircularRegion(latLng.latitude, latLng.longitude, GEOFENCE_RADIUS)
                .setNotificationResponsiveness(1000)
                .setLoiteringDelay(5000)
                .setTransitionTypes(Geofence.GEOFENCE_TRANSITION_ENTER | Geofence.GEOFENCE_TRANSITION_EXIT | Geofence.GEOFENCE_TRANSITION_DWELL)
                .build();
    }

    public PendingIntent getGeofencePI(String title, String text) {
        Log.d(TAG, "getGeofencePI");
        Intent intent = new Intent(this, GeofenceReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("text", text);
        if (pendingIntent != null) {
            return pendingIntent;
        }

        pendingIntent = PendingIntent.getBroadcast(this, 007, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence) {
        Log.d(TAG, "getGeofencingRequest");
        GeofencingRequest.Builder builder = new GeofencingRequest.Builder();
        builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
        builder.addGeofence(geofence);
        return builder.build();
    }

    public String getErrorString(Exception e) {
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            switch (apiException.getStatusCode()) {
                case GeofenceStatusCodes
                        .GEOFENCE_NOT_AVAILABLE:
                    return "GEOFENCE_NOT_AVAILABLE";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_GEOFENCES:
                    return "GEOFENCE_TOO_MANY_GEOFENCES";
                case GeofenceStatusCodes
                        .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                    return "GEOFENCE_TOO_MANY_PENDING_INTENTS";
            }
        }
        return e.getLocalizedMessage();
    }

}
