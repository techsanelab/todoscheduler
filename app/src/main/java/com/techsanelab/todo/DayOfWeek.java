package com.techsanelab.todo;

import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;

import com.techsanelab.todo.fragments.CalendarFragment;

public class DayOfWeek extends Fragment {
    private static final String TAG = "DayOfWeek";
    public CustomCalendar[] days = new CustomCalendar[]{};
    private MaterialButtonToggleGroup group;
    private ConstraintLayout layout;
    private CustomCalendar current_date;
    private CalendarFragment parent;
    CustomCalendar today = new CustomCalendar();
    private boolean thisWeek;

    public void setArgs(CustomCalendar[] days, CalendarFragment parent) {
        this.days = days;
        this.parent = parent;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        layout = (ConstraintLayout) inflater.inflate(R.layout.day_of_weeks, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        group = (MaterialButtonToggleGroup) layout.getChildAt(0);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int width = displayMetrics.widthPixels;

        final EasyFont easyFont = new EasyFont(getActivity(), layout);
        group.setSingleSelection(true);
        group.setGravity(Gravity.CENTER);
        int index = 0;
        final int w = (width / 7) - 18;
        for (CustomCalendar d : days) {
            MaterialButton day = new MaterialButton(getContext(), null, R.attr.materialButtonOutlinedStyle);
            day.setId(index);
            day.setTypeface(easyFont.getTypeface());
            ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(w, ViewGroup.LayoutParams.WRAP_CONTENT);
            day.setLayoutParams(lp);
            day.setSingleLine(false);
            day.setText(String.valueOf(d.getDay()));
            day.setTextSize(12);
            day.setCornerRadius(50);
            group.addView(day);
            index++;

            if (d.toString().equals(today.toString()))
                thisWeek = true;

        }

        if (thisWeek)
            parent.setTodayGroup(group);

        group.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            try {

                current_date = days[group.getCheckedButtonId()];

                if (current_date != null && parent != null) {
                    parent.currentDateListener(current_date, group);
                }
            } catch (Exception e) {
                Log.d("TAG", "DayOfWeek: " + e.getMessage());
            }

        });

    }

    public CustomCalendar[] getDays() {
        return days;
    }

}
