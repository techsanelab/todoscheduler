package com.techsanelab.todo.notifications;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.techsanelab.todo.R;
import com.techsanelab.todo.Utils;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String TAG = "NotificationReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "A notificationn occured");
//        PendingIntent pendingIntent;

        // Url intent
//        String url = intent.getStringExtra("url");
//
//        Intent openURL = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
//        pendingIntent = PendingIntent.getActivity(context, 0, openURL, Intent.FILL_IN_ACTION);


        NotificationHandler notificationHandler = new NotificationHandler(Utils.NOTIIFICATION_ID, Utils.CHANNEL_ID, context);
        notificationHandler.buildNotification(intent.getStringExtra("title"),
                intent.getStringExtra("text"), null, R.drawable.ic_outline_notifications_24);
    }
}
