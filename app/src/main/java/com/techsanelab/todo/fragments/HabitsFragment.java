package com.techsanelab.todo.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.techsanelab.todo.HabitActivity;
import com.techsanelab.todo.HabitsRecyclerAdapter;
import com.techsanelab.todo.ItemClickSupport;
import com.techsanelab.todo.CustomCalendar;
import com.techsanelab.todo.R;
import com.techsanelab.todo.Utils;
import com.techsanelab.todo.entity.items.Habit;

public class HabitsFragment extends Fragment {

    private static final String TAG = "HabitsFragment";
    View view;
    Gson gson = new Gson();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_habits, container, false);
        final Context context = getContext();
        final Activity activity = getActivity();
        RecyclerView habitsRecycler = view.findViewById(R.id.habits_recycler);
        habitsRecycler.setLayoutManager(new GridLayoutManager(context, 2));
        List<Habit> habits = new ArrayList<>();

        try {
            InputStreamReader is = null;
            if (context != null) {
                is = new InputStreamReader(context.getAssets()
                        .open("habits.csv"));
            }

            BufferedReader reader = new BufferedReader(is);
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split("`");

                Habit habit = new Habit(columns[0], new CustomCalendar().toString());
                habit.setContent(new Pair<>(columns[1], 0));
                habit.setDayDuration(Integer.parseInt(columns[2]));


                String[] pods = columns[3].split("\\|");
                for (String pod : pods)
                    habit.addPodByValue(Integer.parseInt(pod));


                habit.setColor("#" + columns[5]);

                Log.d(TAG, "onCreateView: " + columns[6]);
                habit.setPeriod(habit.getPeriodByValue(Integer.parseInt(columns[6])));

                habit.setIcon(columns[7]);
                habit.setImage(columns[8]);
                habit.setDuration(Long.parseLong(columns[9]));

                habits.add(habit);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "onCreateView: " + e.getMessage());
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        final HabitsRecyclerAdapter habitsRecyclerAdapter = new HabitsRecyclerAdapter(habits,context, activity);
        habitsRecycler.setAdapter(habitsRecyclerAdapter);

        ItemClickSupport.addTo(habitsRecycler).setOnItemClickListener((recyclerView, position, v) -> {
            Habit habit = habitsRecyclerAdapter.getItem(position);
            Intent intent = new Intent(context, HabitActivity.class);

            intent.putExtra("habit", gson.toJson(habit));
            startActivity(intent);
        });

        assert activity != null;
        Utils.showCases(activity,Utils.HABITS_FIRST_KEY
                ,new View[]{habitsRecycler.getChildAt(0)}
                ,new int[]{R.string.habits_sc});

        return view;
    }


}
