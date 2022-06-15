package com.techsanelab.todo;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;

import java.io.IOException;

import com.techsanelab.todo.bottomsheet.BottomSheetHandler;
import com.techsanelab.todo.bottomsheet.HasSheets;
import com.techsanelab.todo.entity.items.Habit;
import com.techsanelab.todo.notifications.NotificationHandler;

public class HabitActivity extends AppCompatActivity implements HasSheets {

    private static final String TAG = "HabitActivity";
    TextView title, description, podText, periodText;
    MaterialButton activateButton, alertButton;
    View gradient;
    ImageView habitImage;
    Habit currentHabit;
    Gson gson = new Gson();
    CoordinatorLayout parent;
    EasyFont easyFont;
    Planner planner;
    RadioGroup radioGroup;
    int currentPod;
    BottomSheetHandler bottomSheetAlert;
    View blackBack;
    NotificationHandler notificationHandler;
    CheckBox onTime, min15Before, min30Before, oneDayBefore;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_habit);
        currentHabit = gson.fromJson(getIntent().getStringExtra("habit"), Habit.class);
        getWindow().setStatusBarColor(Utils.manipulateColor(Color.parseColor(currentHabit.getColor()), 0.9F));
        notificationHandler = new NotificationHandler(Utils.NOTIIFICATION_ID);

        // checking check boxes
        onTime = findViewById(R.id.on_time);
        min15Before = findViewById(R.id.min_15_before);
        min30Before = findViewById(R.id.min_30_before);
        oneDayBefore = findViewById(R.id.one_day_before);

        MaterialToolbar topAppBar = findViewById(R.id.top_app_bar);
        topAppBar.setTitle("");
        setSupportActionBar(topAppBar);
        topAppBar.setNavigationOnClickListener(view -> onBackPressed());

        blackBack = findViewById(R.id.black_back);
        blackBack.setOnClickListener(view -> {
            closeAllSheets();
            blackBack.setVisibility(View.GONE);
        });


        easyFont = new EasyFont(this);
        easyFont.changeAllFonts();
        easyFont.tfBold(R.id.activate_button);
        easyFont.tfBold(R.id.procedure_title);
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        gradient = findViewById(R.id.gradient);
        habitImage = findViewById(R.id.image);
        activateButton = findViewById(R.id.activate_button);
        alertButton = findViewById(R.id.alert_button);
        parent = findViewById(R.id.parent);
        podText = findViewById(R.id.part_of_day);
        periodText = findViewById(R.id.periods);
        radioGroup = findViewById(R.id.radio_group);

        title.setText(currentHabit.getTitle());
        description.setText(currentHabit.getDescription());
        Drawable image = null;
        try {
            image = Drawable.createFromStream(this.getAssets().open("images/" + currentHabit.getImage()), null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        habitImage.setImageDrawable(image);
        gradient.setBackground(Utils.getGradientDrawable("#00000000", currentHabit.getColor()));
        parent.setBackgroundColor(Color.parseColor(currentHabit.getColor()));

        podText.setText(this.getString(R.string.part_of_day));
        easyFont.tfBold(R.id.part_of_day);

        int index = 0;
        for (Habit.PartOfDay pod : currentHabit.getPartsOfDay()) {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setText(partOfDay2string(pod.getValue()));
            radioButton.setHighlightColor(Color.parseColor(currentHabit.getColor()));
            radioButton.setTypeface(easyFont.getTypeface());
            if (index == 0) {
                radioButton.setChecked(true);
                currentPod = pod.getValue();
                index++;
            }
            radioButton.setId(pod.getValue());
            radioGroup.addView(radioButton);
        }

        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            currentPod = i;
            Log.d(TAG, "onCheckedChanged: " + currentPod);
        });

        ((RadioButton) radioGroup.getChildAt(0)).setChecked(true);

        periodText.setText(String.format("%s %s %s %s", period2string(currentHabit.getPeriod().getValue()), "به مدت"
                , currentHabit.getDayDuration(), "روز"));

        planner = new Planner(this, this);
        prefs = getSharedPreferences(Utils.SPK, Context.MODE_PRIVATE);
        editor = prefs.edit();

        activateButton.setBackgroundColor(Color.parseColor(currentHabit.getColor()));
        activateButton.setOnClickListener(view -> {
            if (prefs.getBoolean(Utils.HABITS_ONE_TRIAL, true) || SubsHelper.prm) {
                setAlerts();
                callPlanner();
                activateButton.setAlpha(0.8F);
                activateButton.setEnabled(false);
                activateButton.setText(getString(R.string.habits_created));
                editor.putBoolean(Utils.HABITS_ONE_TRIAL, false);
                editor.commit();
            } else {
                DialogHandler.customDialog(this, this, getString(R.string.attention),
                        getString(R.string.submit_subs), null);
            }
        });

        bottomSheetAlert = new

                BottomSheetHandler(R.id.sheet_set_alert, this, blackBack, 200);
        bottomSheetAlert.boldTitle(R.id.alert_title);
        alertButton.setOnClickListener(view -> bottomSheetAlert.toggle());

        Utils.showCases(this, Utils.HABITS_ACT_FIRST_KEY
                , new View[]

                        {
                                radioGroup.getChildAt(0), alertButton
                        }
                , new int[]

                        {
                                R.string.habits_times, R.string.habits_alerts
                        });

    }

    public void setAlerts() {
        if (onTime.isChecked())
            planner.setAlerts(0);
        if (min15Before.isChecked())
            planner.setAlerts(1);
        if (min30Before.isChecked())
            planner.setAlerts(2);
        if (oneDayBefore.isChecked())
            planner.setAlerts(3);
    }


    public void callPlanner() {
        switch (currentPod) {
            case 0:
                planner.planMidnight(currentHabit);
                break;
            case 1:
                planner.planMorning(currentHabit);
                break;
            case 2:
                planner.planNoon(currentHabit);
                break;
            case 3:
                planner.planEvening(currentHabit);
                break;
            case 4:
                planner.planNight(currentHabit);
        }
    }

    public String partOfDay2string(int value) {
        switch (value) {
            case 0:
                return getApplicationContext().getString(R.string.midnight);
            case 1:
                return getApplicationContext().getString(R.string.morning);
            case 2:
                return getApplicationContext().getString(R.string.noon);
            case 3:
                return getApplicationContext().getString(R.string.evening);
            case 4:
                return getApplicationContext().getString(R.string.night);
        }
        return "";
    }

    public String period2string(int value) {
        switch (value) {
            case 1:
                return getApplicationContext().getString(R.string.everyday);
            case 2:
                return getApplicationContext().getString(R.string.every_two_days);
            case 7:
                return getApplicationContext().getString(R.string.every_week);
            case 30:
                return getApplicationContext().getString(R.string.every_month);
        }
        return "";
    }

    @Override
    public void closeAllSheets() {
        bottomSheetAlert.close();
    }


}
