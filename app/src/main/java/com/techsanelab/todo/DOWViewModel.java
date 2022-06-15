package com.techsanelab.todo;

import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import com.techsanelab.todo.fragments.CalendarFragment;

public class DOWViewModel extends ViewModel {

    private List<DayOfWeek> dayOfWeeks;
    public List<DayOfWeek> getDayOfWeeks(CalendarFragment parent) throws InterruptedException {
        if (dayOfWeeks == null) {
            dayOfWeeks = new ArrayList<>();
            for (int i = 0; i < 500; i++) {
                dayOfWeeks.add(null);
            }
            loadUsers(parent);
        }
        return dayOfWeeks;
    }

    private void loadUsers(CalendarFragment parent) throws InterruptedException {
        Thread first = new Thread(){
            @Override
            public void run() {
                super.run();
                for (int i = 0; i < 200; i++) {
                    helper(i,parent);
                }
            }
        };
        Thread second = new Thread(){
            @Override
            public void run() {
                super.run();
                for (int i = 200; i < 500; i++) {
                    helper(i,parent);
                }
            }
        };
        first.start();
        second.start();
        second.join();
    }

    private void helper(int position,CalendarFragment me){
            int count = 500;
            CustomCalendar today = new CustomCalendar();
        if (position == count / 2) {
            CustomCalendar[] days = new CustomCalendar[]{today.getDateByDiff(-3), today.getDateByDiff(-2),
                    today.getDateByDiff(-1), today, today.getDateByDiff(1),today.getDateByDiff(2), today.getDateByDiff(3)};
            DayOfWeek dayOfWeek = new DayOfWeek();
            dayOfWeek.setArgs(days,me);
            dayOfWeeks.set(position,dayOfWeek);
        } else if (position < count / 2) {
            int diff = (position - count / 2) * 7 - 3;
            CustomCalendar[] newDays = new CustomCalendar[7];
            for (int i = 0; i < 7; i++)
                newDays[i] = today.getDateByDiff(diff + i);
            DayOfWeek dayOfWeek = new DayOfWeek();
            dayOfWeek.setArgs(newDays,me);
            dayOfWeeks.set(position,dayOfWeek);
        } else {
            int diff = (position - count / 2) * 7 - 3;
            CustomCalendar[] newDays = new CustomCalendar[7];
            for (int i = 0; i < 7; i++)
                newDays[i] = today.getDateByDiff(diff + i);
            DayOfWeek dayOfWeek = new DayOfWeek();
            dayOfWeek.setArgs(newDays,me);
            dayOfWeeks.set(position,dayOfWeek);
        }
    }

}
