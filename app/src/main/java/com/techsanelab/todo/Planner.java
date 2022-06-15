package com.techsanelab.todo;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.techsanelab.todo.entity.items.Habit;
import com.techsanelab.todo.entity.items.NoteItem;
import com.techsanelab.todo.entity.items.TodoItem;
import com.techsanelab.todo.notifications.NotificationHandler;

public class Planner {

    private static final String TAG = "Planner";
    Context context;
    private final Activity activity;
    CustomCalendar jc;
    public Long firstTime = -12600000L;
    public Long lastTime = 73740000L;
    public Long morningStart = 9000000L;
    public Long noonStart = 30600000L;
    public Long eveningStart = 48600000L;
    public Long nightStart = 63000000L;
    public Long oneHour = 3600000L;
    public Long oneMin = 60000L;
    Thread plannerThread;
    private final Boolean[] alerts = {false, false, false, false};
    NotificationHandler notificationHandler;

    public Planner(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        notificationHandler = new NotificationHandler(Utils.NOTIIFICATION_ID,Utils.CHANNEL_ID,context);
    }

    public Boolean plan(Habit habit, Long start, Long finish, Long timeDuration, String date, int dayDuration) {


        plannerThread = new Thread() {

            @Override
            public void run() {


                String[] tmp = date.split("-", 5);
                jc = new CustomCalendar(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]), Integer.parseInt(tmp[2]));
                // tomorrow
                jc = jc.getDateByDiff(1);

                DBHelper dbHelper = DBHelper.getInstance(context);
                int dayCounter = dayDuration;
                while (dayCounter > 0) {
                    List<TodoItem> todoItems = dbHelper.selectTodoByStartDate(jc.toString());

                    List<Pair<Long, Long>> slots = new ArrayList<>();
                    Long counter = start;
                    while (counter < finish) {
                        Log.d(TAG, "plan: " + counter);
                        Long acc = Long.sum(counter, timeDuration);
                        if (!hasConflict(todoItems, counter, acc)) {
                            slots.add(new Pair<>(counter, acc));
                        }
                        counter = acc;
                    }


                    Log.d(TAG, "slots: " + slots.toString());

                    // there is no free time
                    if (slots.size() != 0)
                        dayCounter--;
                    else {

                        Looper.prepare();

                        if (dialog4dayPivot(jc.toString())) {
                            // Pivot a day
                            Looper.loop();
                            jc = jc.getDateByDiff(1);
                            continue;
                        } else {
                            Looper.loop();
                            dayCounter--;
                            slots.add(new Pair<>(start, Long.sum(start, timeDuration)));
                        }

                    }

                    createTodoFromSlot(habit, jc.toString(), slots.get(0).first, slots.get(0).second, dbHelper);
                    scheduleNotifications(habit, new Time(slots.get(0).first));

                    // tomorrow
                    jc = jc.getDateByDiff(habit.getPeriod().getValue());
                }


            }
        };

        plannerThread.start();
        DialogHandler.customDialog(context, activity, context.getString(R.string.good_news)
                , context.getString(R.string.habits_created), habit.getDrawableIcon(context));
        return true;
    }

    public void setAlerts(int index) {
        alerts[index] = true;
    }

    public void scheduleNotifications(Habit habit, Time start_time) {
        if (start_time == null)
            return;
        long t = Long.sum(jc.getTimeInMillis(), TimeUnit.HOURS.toMillis(start_time.getHours()));
        t = Long.sum(t, TimeUnit.MINUTES.toMillis(start_time.getMinutes()));

        if (alerts[0])
            notificationHandler.scheduleNotification(t, habit.getTitle(), habit.getDescription());
        if (alerts[1])
            notificationHandler.scheduleNotification(Long.sum(t, -1 * TimeUnit.MINUTES.toMillis(15)), habit.getTitle(), habit.getDescription());
        if (alerts[2])
            notificationHandler.scheduleNotification(Long.sum(t, -1 * TimeUnit.MINUTES.toMillis(30)), habit.getTitle(), habit.getDescription());
        if (alerts[3])
            notificationHandler.scheduleNotification(Long.sum(t, -1 * TimeUnit.DAYS.toMillis(1)), habit.getTitle(), habit.getDescription());
    }

    public void planMidnight(Habit todoItem) {
        String date = new CustomCalendar().toString();
        long temp = Long.sum(firstTime, oneHour);
        temp = Long.sum(temp, oneHour);
        temp = Long.sum(temp, oneHour);
        temp = Long.sum(temp, oneHour); // 4:00 am
        temp = Long.sum(temp, oneHour); // 5:00 am
        plan(todoItem, temp, morningStart, todoItem.getDuration(), date, todoItem.getDayDuration());
    }

    public void planMorning(Habit todoItem) {
        String date = new CustomCalendar().toString();
        plan(todoItem, morningStart, noonStart, todoItem.getDuration(), date, todoItem.getDayDuration());
    }

    public void planNoon(Habit todoItem) {
        String date = new CustomCalendar().toString();
        plan(todoItem, noonStart, eveningStart, todoItem.getDuration(), date, todoItem.getDayDuration());
    }

    public void planEvening(Habit todoItem) {
        String date = new CustomCalendar().toString();
        plan(todoItem, eveningStart, nightStart, todoItem.getDuration(), date, todoItem.getDayDuration());
    }

    public void planNight(Habit todoItem) {
        String date = new CustomCalendar().toString();
        plan(todoItem, nightStart, lastTime, todoItem.getDuration(), date, todoItem.getDayDuration());
    }


    public void createTodoFromSlot(Habit todoItem, String date, Long start, Long finish, DBHelper helper) {
        // create todos into database
        Log.d(TAG, "createTodoFromSlot: " + start);

        // generate new todos id
        SharedPreferences prefs = context.getSharedPreferences(Utils.SPK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        int newId = prefs.getInt(Utils.TODO_COUNT_KEY, 0);

        NoteItem noteItem = new NoteItem(newId, todoItem.getTitle(), todoItem.getContent(), date,
                null, new Time(start), new Time(finish), Utils.loggedInUser, TodoItem.State.INPROGRESS, TodoItem.Type.HABIT);
        helper.createTodo(noteItem);

        editor.putInt(Utils.TODO_COUNT_KEY, newId + 1);
        editor.commit();
    }

    public Boolean hasConflict(List<TodoItem> todoItems, Long start, Long finish) {
        for (TodoItem todos : todoItems) {
            if (finish > todos.getStartTime().getTime() && start < todos.getFinishTime().getTime())
                return true;
        }
        return false;
    }

    private Boolean ans;

    public Boolean dialog4dayPivot(String date) {
        ans = false;

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);

        builder.setTitle(R.string.dialog_warning)
                .setMessage(String.format("Are you ok to add event in \"%s\" ?", date))
                .setPositiveButton(R.string.another_time, (dialogInterface, i) -> ans = true)
                .setNegativeButton(R.string.force_push, (dialogInterface, i) -> Log.d(TAG, "onClick: Force push.")).show();

        return ans;
    }

}
