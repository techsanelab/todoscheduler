package com.techsanelab.todo.entity.items;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.util.Pair;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;

import com.techsanelab.todo.Utils;

public class Habit extends TodoItem {

    private static final String TAG = "Habit";
    Pair<String, Integer> content;
    private String color;
    private String image;
    private String icon;
    private Intent intent;
    private ArrayList<PartOfDay> partsOfDay = new ArrayList<>();
    private Period period;
    private Long duration;
    private int dayDuration;

    public enum PartOfDay {
        MIDNIGHT(0),
        MORNING(1),
        NOON(2),
        EVENING(3),
        NIGHT(4);

        private final int value;

        PartOfDay(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum Period {
        EVERYDAY(1),
        EVERY_TWO_DAYS(2),
        EVERY_WEEK(7),
        EVERY_MONTH(30);

        private int value;

        Period(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }


    public Habit(String title, String startDate) {
        super(title, startDate, "", new Time(0), new Time(0), Utils.loggedInUser, State.COMPLETE, Type.HABIT);
    }

    public ArrayList<PartOfDay> getPartsOfDay() {
        return partsOfDay;
    }

    public void addPod(PartOfDay partOfDay) {
        if (!partsOfDay.contains(partOfDay))
            partsOfDay.add(partOfDay);
    }

    public void addPodByValue(int value) {
        PartOfDay partOfDay = getPodByValue(value);
        if (!partsOfDay.contains(partOfDay))
            partsOfDay.add(partOfDay);
    }

    public PartOfDay getPodByValue(int value) {
        switch (value) {
            case 0:
                return PartOfDay.MIDNIGHT;
            case 1:
                return PartOfDay.MORNING;
            case 2:
                return PartOfDay.NOON;
            case 3:
                return PartOfDay.EVENING;
            case 4:
                return PartOfDay.NIGHT;
        }
        return null;
    }

    public Period getPeriodByValue(int value) {
        switch (value) {
            case 1:
                return Period.EVERYDAY;
            case 2:
                return Period.EVERY_TWO_DAYS;
            case 7:
                return Period.EVERY_WEEK;
            case 30:
                return Period.EVERY_MONTH;
        }
        return null;
    }

    public int getDayDuration() {
        return dayDuration;
    }

    public void setDayDuration(int dayDuration) {
        this.dayDuration = dayDuration;
    }

    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public String getIcon() {
        return icon;
    }

    public Drawable getDrawableIcon(Context context) {
        Drawable icon = null;
        try {
            icon = Drawable.createFromStream(context.getAssets().open("icons/" + getIcon()), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean isActive(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Utils.HABITS_KEY, Context.MODE_PRIVATE);
        Boolean temp = prefs.getBoolean(getTitle(), true);
        return temp;
    }

    public void active(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Utils.HABITS_KEY, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(getTitle(), true);
        editor.commit();
    }

    public void deactive(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(Utils.HABITS_KEY, Context.MODE_PRIVATE);
        @SuppressLint("CommitPrefEdits") SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(getTitle(), false);
        editor.commit();
    }


    public Intent getIntent() {
        return intent;
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public Pair<String, Integer> getContent() {
        return content;
    }

    @Override
    public void setContent(Object content) {
        this.content = (Pair<String, Integer>) content;
    }

    @Override
    public String getDescription() {
        return content.first;
    }

}
