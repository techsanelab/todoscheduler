package com.techsanelab.todo;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class CustomCalendar {

    private GregorianCalendar calendar;
    private int diff = 0;

    public CustomCalendar() {
        calendar = new GregorianCalendar();
    }

    public CustomCalendar(int df){
        calendar = new GregorianCalendar();
        diff = df;
        calendar.add(Calendar.DAY_OF_MONTH,diff);
    }

    public CustomCalendar(GregorianCalendar calendar) {
        this.calendar = calendar;
    }

    public CustomCalendar(int year, int month, int day) {
        calendar = new GregorianCalendar(year, month, day);
    }

    public CustomCalendar getDateByDiff(int df){
        return new CustomCalendar(diff + df);
    }

    public int getDayOfWeek() {
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public long getTimeInMillis(){
        return calendar.getTimeInMillis();
    }

    public String getDayOfWeekString() {
        switch (getDayOfWeek()) {
            case 1:
                return "Sunday";
            case 2:
                return "Monday";
            case 3:
                return "Tuesday";
            case 4:
                return "Wednesday";
            case 5:
                return "Thursday";
            case 6:
                return "Friday";
            case 7:
                return "Saturday";
            default:
                return "Unknown";
        }
    }

    public String getDayOfWeekStringShort() {
        return calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
    }

    public String getMonthString() {
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ENGLISH);
    }

    public int getSeason() {
        if (1 <= getMonth() && getMonth() <= 3)
            return 1;
        else if (4 <= getMonth() && getMonth() <= 6)
            return 2;
        else if (7 <= getMonth() && getMonth() <= 9)
            return 3;
        else if (10 <= getMonth() && getMonth() <= 12)
            return 4;
        return 0;
    }

    public int getDay() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    public int getMonth() {
        return calendar.get(Calendar.MONTH);
    }

    public int getYear() {
        return calendar.get(Calendar.YEAR);
    }

    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d", getYear(), getMonth(), getDay());
    }

}
