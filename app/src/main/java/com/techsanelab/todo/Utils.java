package com.techsanelab.todo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.blushine.android.ui.showcase.MaterialShowcaseSequence;
import io.blushine.android.ui.showcase.ShowcaseConfig;
import com.techsanelab.todo.entity.LoggedInUser;

/*
* This class containing any utility function that used to develop Done application.
* */

public class Utils {

    private static final String TAG = "Utils";

    // Permissions
    public static String[] requiredPermissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_BACKGROUND_LOCATION
    };

    public static void permissionHandler(Activity activity) {

        if (ContextCompat.checkSelfPermission(activity, requiredPermissions[0])
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(activity, requiredPermissions, 0);
        }
    }

    // Notifications
    public static final int NOTIIFICATION_ID = 4323;
    public static final String CHANNEL_ID = "6895";
    public static final String CHANNEL_NAME = "Alerts";
    public static final String CHANNEL_DESCRIPTION = "Notification channel for events alerts.";

    // Geofence notifications
    public static final int GEOFENCE_NOTIIFICATION_ID = 2134;
    public static final String CHANNEL_GEOFENCE_ID = "5761";
    public static final String CHANNEL_GEOFENCE_NAME = "Geofence channel";
    public static final String CHANNEL_GEOFENCE_DESCRIPTION = "Notification channel for geofence events.";

    // Habits notification
    public static final int HABIT_NOTIFICATION_ID = 8462;
    public static final String CHANNEL_HABITS_ID = "7687";
    public static final String CHANNEL_HABITS_NAME = "Habits channel";
    public static final String CHANNEL_HABITS_DESCRIPTION = "Notification channel for habits part";

    // Shared preferences
    public static final String SPK = "spkey";
    public static final String FIRSTTIME_KEY = "firsttime";
    public static final String LOCATION_FIRST_KEY = "locationfirst";
    public static final String TODO_COUNT_KEY = "countKey";
    public static final String HABITS_KEY = "habitskey";
    public static final String CALENDAR_FIRST_KEY = "calendarkey";
    public static final String HABITS_FIRST_KEY = "habitsfirstkey";
    public static final String HABITS_ACT_FIRST_KEY = "habitsactfirstkey";
    public static final String ADD_TODO_FIRST_KEY = "addtodofirstkey";
    public static final String FIRST_COMMENT = "firstcomment";
    public static final String SECOND_COMMENT = "secondcomment";
    public static final String PREMIUM_KEY = "premiumkey";
    public static final String PURCHASE_KEY = "purchasekey";
    public static final String TODO_PURCHASE_KEY = "todopurchasekey";
    public static final String HABITS_ONE_TRIAL = "habitsonetrial";

    // Database
    public static final String DATABASE_NAME = "donedb.sqlite";
    public static final int DATABASE_VERSION = 3;

    // Logged in user
    public static final LoggedInUser loggedInUser = new LoggedInUser(12, "Nima", "Nima iji", "1ab12");

    // For replicating todos activity
    public enum ActivityMode {
        ADD(0),
        EDIT(1);

        private int value;

        ActivityMode(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    // hh:mm
    public static String formatTime(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    public static void changeStatus(Activity activity, Context context, int color) {
        activity.getWindow().setStatusBarColor(manipulateColor(context.getColor(color), 0.9F));
    }

    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

    public static void showCases(Activity activity, String key, View[] views, int[] titlesId) {
        ShowcaseConfig config = new ShowcaseConfig(activity.getBaseContext());
        config.setDelay(300); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(activity, key);
        sequence.setConfig(config);
        for (int i = 0; i < views.length; i++) {
            sequence.addSequenceItem(views[i],
                    activity.getString(titlesId[i]), activity.getString(R.string.ok));
        }

        sequence.show();
    }

    public static Drawable getGradientDrawable(String hexColor, float diff) {

        int startColor = Color.parseColor(hexColor);
        int endColor = manipulateColor(startColor, diff);

        GradientDrawable mDrawable = new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{startColor, endColor});
        return mDrawable;
    }

    public static Drawable getGradientDrawable(String start, String end) {

        int startColor = Color.parseColor(start);
        int endColor = Color.parseColor(end);

        GradientDrawable mDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{startColor, endColor});
        return mDrawable;
    }

}
