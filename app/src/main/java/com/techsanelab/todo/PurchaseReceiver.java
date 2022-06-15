package com.techsanelab.todo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

public class PurchaseReceiver extends BroadcastReceiver {
    private static final String TAG = "PurchaseReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences prefs = context.getSharedPreferences(Utils.SPK, Context.MODE_PRIVATE);
        TodoPurchase todoPurchase = null;
        try {
            todoPurchase = new TodoPurchaseIC(context).createInstance();
            todoPurchase.setAcc(prefs.getInt(Utils.TODO_PURCHASE_KEY + "acc", 0));
            todoPurchase.setPeriodAcc(prefs.getInt(Utils.TODO_PURCHASE_KEY + "periodacc", 0));
            todoPurchase.setIndex(prefs.getInt(Utils.TODO_PURCHASE_KEY + "index", 0));
            todoPurchase.setPeriodItems(prefs.getStringSet(Utils.TODO_PURCHASE_KEY + "list", null));
            todoPurchase.update();
        } catch (Exception e) {
            Log.e(TAG, "onReceive: ", e);
        }

        Intent i = new Intent(context, PurchaseReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 12131, i, PendingIntent.FLAG_IMMUTABLE);
        Log.d(TAG, "onReceive: Happening");
        Log.d(TAG, "onReceive: " + todoPurchase.getAcc());
        Log.d(TAG, "onReceive: " + todoPurchase.getPeriodAcc());
        Log.d(TAG, "onReceive: " + todoPurchase.getScope());
        // set alarm for next 24 hours
        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Long time = Long.sum(System.currentTimeMillis(), 86400000L); // Every 24 hours
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            manager.setExact(AlarmManager.RTC_WAKEUP, time, pending);
        }

    }

}
