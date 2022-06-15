package com.techsanelab.todo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import com.techsanelab.todo.notifications.NotificationHandler;

public class GeofenceReceiver extends BroadcastReceiver {

    Context context;
    Intent intent;

    private static final String TAG = "Geofence";

    public void onReceive(Context c, Intent i) {
        context = c;
        intent = i;

        GeofencingEvent geofencingEvent = GeofencingEvent.fromIntent(intent);
        if (geofencingEvent.hasError()) {
            String errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.getErrorCode());
            Log.e(TAG, errorMessage);
            return;
        }

        List<Geofence> geofenceList = geofencingEvent.getTriggeringGeofences();
        for (Geofence geofence : geofenceList) {
            Log.d(TAG, "onReceive: " + geofence.getRequestId());
        }

        int transitionType = geofencingEvent.getGeofenceTransition();
        NotificationHandler notificationHandler = new NotificationHandler(Utils.GEOFENCE_NOTIIFICATION_ID,Utils.CHANNEL_GEOFENCE_ID,context);

        switch (transitionType) {
            case Geofence.GEOFENCE_TRANSITION_ENTER:
                Log.d(TAG, "GEOFENCE_TRANSITION_ENTER");
                notificationHandler.buildNotification(intent.getStringExtra("title"),
                        intent.getStringExtra("text"), null, R.drawable.ic_twotone_map_24);
                break;
            case Geofence.GEOFENCE_TRANSITION_DWELL:
                Log.d(TAG, "GEOFENCE_TRANSITION_DWELL");
                notificationHandler.buildNotification(intent.getStringExtra("title"),
                        intent.getStringExtra("text"), null, R.drawable.ic_twotone_map_24);
                break;
            case Geofence.GEOFENCE_TRANSITION_EXIT:
                Log.d(TAG, "GEOFENCE TRANSITION EXIT");
                break;
        }


    }
}
