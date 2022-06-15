package com.techsanelab.todo;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.techsanelab.todo.fragments.AnalysisFragment;
import com.techsanelab.todo.fragments.CalendarFragment;
import com.techsanelab.todo.fragments.HabitsFragment;
import com.techsanelab.todo.fragments.SubsFragment;
import com.techsanelab.todo.fragments.AboutUsFragment2;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    BottomNavigationView bottomNavigationView;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    FragmentTransaction transaction;
    private boolean isFirstTime = false;
    private ColorStateList tempCsl;
    private Boolean isPremium = false;
    private int currentItem;

    int[][] states = new int[][]{
            new int[]{android.R.attr.state_enabled}, // enabled
            new int[]{-android.R.attr.state_enabled}, // disabled
            new int[]{-android.R.attr.state_checked}, // unchecked
            new int[]{android.R.attr.state_pressed}  // pressed
    };

    int[] whites = new int[]{
            Color.WHITE,
            Color.WHITE,
            Color.WHITE,
            Color.WHITE
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.permissionHandler(this);


        // first time settings
        SharedPreferences prefs = getSharedPreferences(Utils.SPK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        isFirstTime = prefs.getBoolean(Utils.FIRSTTIME_KEY, true);

        // first time settings
        if (isFirstTime) {

            // creating a notification channel
            createNotificationChannel(Utils.CHANNEL_ID, Utils.CHANNEL_NAME, Utils.CHANNEL_DESCRIPTION);
            createNotificationChannel(Utils.CHANNEL_GEOFENCE_ID, Utils.CHANNEL_GEOFENCE_NAME, Utils.CHANNEL_GEOFENCE_DESCRIPTION);
            createNotificationChannel(Utils.CHANNEL_HABITS_ID, Utils.CHANNEL_HABITS_NAME, Utils.CHANNEL_HABITS_DESCRIPTION);

            // creating logged in User
            DBHelper dbHelper = DBHelper.getInstance(this);
            dbHelper.createUser(Utils.loggedInUser);
            Log.d(TAG, "onCreate: Logged in user created.");
            Log.d(TAG, "onCreate: Testing Logged in user -> " + dbHelper.selectUserById(Utils.loggedInUser.getId()));
            editor.putBoolean(Utils.FIRSTTIME_KEY, false);
            editor.commit();
        }


        // declarations
        fragmentManager = getSupportFragmentManager();

        fragment = new CalendarFragment();
        currentItem = R.id.calendar;
        transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.replace(R.id.main_container, fragment).commit();

        // onNavigationItemSelectedListener for bottom navigation
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_navigation);
        tempCsl = bottomNavigationView.getItemIconTintList();
        bottomNavigationView.setSelectedItemId(R.id.calendar);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    if (currentItem == item.getItemId())
                        return true;
                    currentItem = item.getItemId();
                    switch (item.getItemId()) {
                        case R.id.we:
                            fragment = new AboutUsFragment2();
                            changeCls(1);
                            break;
                        case R.id.analysis:
                            fragment = new AnalysisFragment();
                            changeCls(1);
                            break;
                        case R.id.calendar:
                            fragment = new CalendarFragment();
                            changeCls(1);
                            break;
                        case R.id.subscribe:
                            fragment = new SubsFragment();
                            changeCls(0);
                            break;
                        case R.id.habits:
                            fragment = new HabitsFragment();
                            changeCls(1);
                            break;
                    }
                    transaction = fragmentManager.beginTransaction();
                    transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
                    transaction.replace(R.id.main_container, fragment).commit();
                    return true;
                });


        /*
         * Setup IABHelper by SubsHelper class, to user is premium or not.
         * You can add some elements to your Calendar layout to show the user state.
         * */

//        try {
//            if (SubsHelper.getmHelper() == null) {
//                SubsHelper.setup(this);
//            }
//        }catch (Exception e) {
//            Log.e(TAG, "onCreate: ", e);
//        }

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        fragmentManager.executePendingTransactions();
        super.onPause();
    }

    private void createNotificationChannel(String id, String name, String description) {

        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(Utils.CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
            Log.d(TAG, "createNotificationChannel: channel created.");
        }
    }

    private void changeCls(int flag) {
        if (isPremium && flag == 1) {
            bottomNavigationView.setItemIconTintList(tempCsl);
            bottomNavigationView.setItemTextColor(tempCsl);
            bottomNavigationView.setBackgroundColor(getColor(R.color.white));
            isPremium = false;
        } else if (!isPremium && flag == 0) {
            bottomNavigationView.setBackgroundColor(getColor(R.color.premiumColor));
            bottomNavigationView.setItemIconTintList(new ColorStateList(states, whites));
            bottomNavigationView.setItemTextColor(new ColorStateList(states, whites));
            isPremium = true;
        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult(" + requestCode + "," + resultCode + "," + data);

        // Pass on the activity result to the helper for handling
        if (!SubsHelper.getmHelper().handleActivityResult(requestCode, resultCode, data)) {
            super.onActivityResult(requestCode, resultCode, data);
        } else {
            Log.d(TAG, "onActivityResult handled by IABUtil.");
        }
    }

}