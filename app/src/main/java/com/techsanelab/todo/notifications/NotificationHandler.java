package com.techsanelab.todo.notifications;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class NotificationHandler {

    private static final String TAG = "NotificationHandler";
    private int id;
    private String channelId;
    private Context context;
    private NotificationManager manager;

    public static enum NotificationType {
        ON_EVENT(0),
        BEFORE_15(1),
        BEFORE_30(2),
        BEFORE_DAY(3),
        LOCATION_TYPE(4);

        private int value;

        NotificationType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public NotificationHandler(int notificationId, String channelId, Context context) {
        this.id = notificationId;
        this.channelId = channelId;
        this.context = context;
        manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public NotificationHandler(int id) {
        this.id = id;
    }

    public void buildNotification(String title, String text, PendingIntent contentIntent, int icon) {

        // Build a notification
        Notification notification = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(contentIntent)
                .build();

        // Show notification
        manager.notify(id, notification);

    }

    public void cancelNotification() {
        manager.cancel(id);
    }

    public void scheduleNotification(long time, String title, String text) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("title", title);
        intent.putExtra("text", text);// TODO: 3/1/21
//        intent.putExtra("url", url);
        PendingIntent pending = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_IMMUTABLE);

        // Schdedule notification
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Log.d(TAG, "sent  " + System.currentTimeMillis());
        Log.d(TAG, "our time  " + time);
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending);
    }

    public void changeId(int id) {
        this.id = id;
    }

}
