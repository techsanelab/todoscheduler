package com.techsanelab.todo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.gson.Gson;

import java.sql.Time;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import com.techsanelab.todo.bottomsheet.BottomSheetHandler;
import com.techsanelab.todo.bottomsheet.HasSheets;
import com.techsanelab.todo.entity.items.CheckBoxItem;
import com.techsanelab.todo.entity.items.NoteItem;
import com.techsanelab.todo.entity.items.TodoItem;
import com.techsanelab.todo.notifications.NotificationHandler;

public class AddTodoActivity extends AppCompatActivity implements HasSheets {

    private static final String TAG = "TodoActivity";

    ConstraintLayout layout;
    LinearLayout checkBoxLayout;
    TextView dateTitle;
    EditText title, description, alertContent;
    TextInputEditText start_time_et, end_time_et;
    MaterialTimePicker start_picker, end_picker;
    Time start_time, end_time;
    String current_date;
    Context context;
    Activity activity;
    CheckBox onTime, min15Before, min30Before, oneDayBefore;
    CustomCalendar jc;
    View blackBack;
    int mode;
    Gson gson;
    TodoItem currentTodo;
    BottomAppBar bottomAppBar;
    BottomSheetHandler bottomSheetTime, bottomSheetAlert, bottomSheetState;
    NotificationHandler notificationHandler;
    TodoItem.State currentState = TodoItem.State.INCOMPLETE;
    TodoItem.Type currentType = TodoItem.Type.NOTEITEM;
    ArrayList<EditCheckBox> boxes = new ArrayList<>();
    TodoPurchase todoPurchase;
    DBHelper dbHelper;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_todo);

        layout = findViewById(R.id.layout);
        checkBoxLayout = findViewById(R.id.checkbox_layout);

        dbHelper = DBHelper.getInstance(this);

        overridePendingTransition(R.anim.slide_down, R.anim.slide_up);

        SharedPreferences prefs = getSharedPreferences(Utils.SPK, Context.MODE_PRIVATE);

        todoPurchase = new TodoPurchaseIC(this).createInstance();
        todoPurchase.setAcc(prefs.getInt(Utils.TODO_PURCHASE_KEY + "acc", 0));
        todoPurchase.setPeriodAcc(prefs.getInt(Utils.TODO_PURCHASE_KEY + "periodacc", 0));
        todoPurchase.setIndex(prefs.getInt(Utils.TODO_PURCHASE_KEY + "index", 0));
        todoPurchase.setPeriodItems(prefs.getStringSet(Utils.TODO_PURCHASE_KEY + "list", null));
        todoPurchase.updateAvg();


        context = this;
        activity = this;
        gson = new Gson();

        EasyFont easyFont = new EasyFont(activity);
        easyFont.changeAllFonts();
        easyFont.tf(R.id.description);

        blackBack = findViewById(R.id.black_back);
        blackBack.setOnClickListener(view -> {
            closeAllSheets();
            blackBack.setVisibility(View.GONE);
        });


        // Elements
        title = findViewById(R.id.title);
        description = findViewById(R.id.description);
        alertContent = findViewById(R.id.alert_content);
        start_time_et = findViewById(R.id.start_time);
        end_time_et = findViewById(R.id.end_time);
        bottomAppBar = findViewById(R.id.bottom_appbar);

        // checking check boxes
        onTime = findViewById(R.id.on_time);
        min15Before = findViewById(R.id.min_15_before);
        min30Before = findViewById(R.id.min_30_before);
        oneDayBefore = findViewById(R.id.one_day_before);

        // Receiving extras from intent
        mode = getIntent().getIntExtra("mode", 0);
        current_date = getIntent().getStringExtra("current_date");
        Log.d(TAG, "Add Todo: " + current_date);
        Log.d(TAG, "mode: " + mode);

        if (mode == Utils.ActivityMode.EDIT.getValue()) {
            int typeTmp = getIntent().getIntExtra("type", 0);
            if (typeTmp == 0)
                currentTodo = gson.fromJson(getIntent().getStringExtra("todo_item"), NoteItem.class);
            else if (typeTmp == 1)
                currentTodo = gson.fromJson(getIntent().getStringExtra("todo_item"), CheckBoxItem.class);

            title.setText(currentTodo.getTitle());
            bottomAppBar.replaceMenu(R.menu.todo_edit_menu);
            alertsUI(dbHelper.selectNotifications(currentTodo.getId()), bottomAppBar.getMenu());

            currentType = currentTodo.getType();
            if (currentTodo.getType() == TodoItem.Type.NOTEITEM) {
                description.setText(((Pair<String, Integer>) currentTodo.getContent()).first);
                description.setVisibility(View.VISIBLE);
                checkBoxLayout.setVisibility(View.GONE);
            } else if (currentTodo.getType() == TodoItem.Type.CHECKBOX) {
                checkBoxLayout.setVisibility(View.VISIBLE);
                description.setVisibility(View.GONE);
                int index = 0;
                for (Pair<String, Integer> content : (ArrayList<Pair>) currentTodo.getContent()) {
                    Log.d(TAG, "In for: " + content.first);
                    EditCheckBox editCheckBox = new EditCheckBox(context, activity);
                    editCheckBox.setValue(index);
                    editCheckBox.setText(content.first);
                    if (content.second == 1)
                        editCheckBox.setCheck(true);
                    else if (content.second == 0)
                        editCheckBox.setCheck(false);
                    checkBoxLayout.addView(editCheckBox);
                    boxes.add(editCheckBox);
                    index++;
                }

            }
            start_time = new Time(currentTodo.getStartTime().getTime());
            end_time = new Time(currentTodo.getFinishTime().getTime());
            currentState = currentTodo.getState();

            start_time_et.setText(Utils.formatTime(currentTodo.getStartTime().getHours(), currentTodo.getStartTime().getMinutes()));
            end_time_et.setText(Utils.formatTime(currentTodo.getFinishTime().getHours(), currentTodo.getFinishTime().getMinutes()));
        }

        String[] tmp = current_date.split("-", 5);
        jc = new CustomCalendar(Integer.parseInt(tmp[0]), Integer.parseInt(tmp[1]), Integer.parseInt(tmp[2]));


        MaterialToolbar topAppBar = findViewById(R.id.top_app_bar);
        topAppBar.setTitle("");

        setSupportActionBar(topAppBar);
        topAppBar.setNavigationOnClickListener(view -> activity.onBackPressed());

        dateTitle = findViewById(R.id.date_title);
        dateTitle.setText(String.format("%s\n%s", jc.getDayOfWeekString(), jc.toString().replace('-', '/')));

        // set time bottom sheet
        FloatingActionButton fab = findViewById(R.id.fab);
        bottomSheetTime = new BottomSheetHandler(R.id.sheet_set_time, activity, blackBack, 250);
        bottomSheetTime.boldTitle(R.id.times_title);
        fab.setOnClickListener(view -> bottomSheetTime.toggle());

        bottomSheetAlert = new BottomSheetHandler(R.id.sheet_set_alert, activity, blackBack, 300);
        bottomSheetAlert.boldTitle(R.id.alert_title);
        bottomSheetState = new BottomSheetHandler(R.id.sheet_set_state, activity, blackBack, 250);
        bottomSheetState.boldTitle(R.id.state_title);
        bottomAppBar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.share:
                    shareTodo();
                    break;
                case R.id.alert:
                    bottomSheetAlert.toggle();
                    break;
                case R.id.state:
                    bottomSheetState.toggle();
                    break;
                case R.id.add_location:
                    startActivity(new Intent(this, MapActivity.class));
                    break;
                case R.id.change_type:
                    if (currentType == TodoItem.Type.NOTEITEM) {
                        currentType = TodoItem.Type.CHECKBOX;
                        description.setVisibility(View.GONE);
                        checkBoxLayout.setVisibility(View.VISIBLE);

                        if (boxes.size() == 0) {
                            EditCheckBox editCheckBox = new EditCheckBox(context, activity);
                            editCheckBox.setValue(0);
                            boxes.add(editCheckBox);
                            checkBoxLayout.addView(editCheckBox);
                        }
                    } else {
                        currentType = TodoItem.Type.NOTEITEM;
                        description.setVisibility(View.VISIBLE);
                        checkBoxLayout.setVisibility(View.GONE);
                    }
                    break;
                case R.id.delete:
                    deleteTodo();
                    break;
            }
            return false;
        });


        start_time_et.setOnClickListener(view -> {
            start_picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(0)
                    .setMinute(0)
                    .setTitleText(R.string.start_time)
                    .build();
            start_picker.addOnPositiveButtonClickListener(view1 -> {
                start_time_et.setText(Utils.formatTime(start_picker.getHour(), start_picker.getMinute()));
//                start_time = new Time(TimeUnit.HOURS.toMillis(start_picker.getHour()) + TimeUnit.MINUTES.toMillis(start_picker.getMinute()));
                start_time = new Time(start_picker.getHour(), start_picker.getMinute(), 0);

            });
            start_picker.show(getSupportFragmentManager(), "start_timer_tag");

        });


        end_time_et.setOnClickListener(view -> {
            end_picker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setHour(0)
                    .setMinute(0)
                    .setTitleText(R.string.end_time)
                    .build();
            end_picker.addOnPositiveButtonClickListener(view12 -> {
                end_time_et.setText(Utils.formatTime(end_picker.getHour(), end_picker.getMinute()));
//                end_time = new Time(TimeUnit.HOURS.toMillis(end_picker.getHour()) + TimeUnit.MINUTES.toMillis(end_picker.getMinute()));
                end_time = new Time(end_picker.getHour(), end_picker.getMinute(), 0);
            });

            end_picker.show(getSupportFragmentManager(), "end_timer_tag");

        });


        // check todos state
        RadioGroup radioGroup = findViewById(R.id.state_group);
        if (currentTodo != null) {
            ((RadioButton) findViewById(R.id.complete_state)).setChecked(currentTodo.getState() == TodoItem.State.COMPLETE);
            ((RadioButton) findViewById(R.id.incomplete_state)).setChecked(currentTodo.getState() == TodoItem.State.INCOMPLETE);
            ((RadioButton) findViewById(R.id.inprogress_state)).setChecked(currentTodo.getState() == TodoItem.State.INPROGRESS);
        }

        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            switch (i) {
                case R.id.complete_state:
                    currentState = TodoItem.State.COMPLETE;
                    break;
                case R.id.inprogress_state:
                    currentState = TodoItem.State.INPROGRESS;
                    break;
                default:
                    currentState = TodoItem.State.INCOMPLETE;
            }
        });

        findViewById(R.id.save).setOnClickListener(view -> {
            if (saveTodo()) {
                todoPurchase.query(1, String.valueOf(currentTodo.getId()));
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public boolean saveTodo() {
        if (end_time == null || start_time == null) {
            bottomSheetTime.expand();
            Toast.makeText(context, getResources().getString(R.string.no_time_warning), Toast.LENGTH_LONG).show();
            return false;
        }


        if (mode == Utils.ActivityMode.EDIT.getValue()) {
            // Edit current todo

            try {
                if (currentType == TodoItem.Type.NOTEITEM)
                    currentTodo = new NoteItem(currentTodo.getId(), title.getText().toString(), new Pair<>(description.getText().toString(), 0), current_date, null, new Time(start_time.getTime()), new Time(end_time.getTime())
                            , Utils.loggedInUser, currentState, TodoItem.Type.NOTEITEM);
                else if (currentType == TodoItem.Type.CHECKBOX) {
                    ArrayList<Pair<String, Integer>> contents = new ArrayList<>();
                    for (EditCheckBox box : boxes) {
                        contents.add(new Pair<>(box.getText(), box.isChecked() ? 1 : 0));
                    }
                    currentTodo = new CheckBoxItem(currentTodo.getId(), title.getText().toString(), contents, current_date, null, new Time(start_time.getTime()), new Time(end_time.getTime())
                            , Utils.loggedInUser, currentState, TodoItem.Type.CHECKBOX);
                }
                currentTodo.setDayOfWeek(jc.getDayOfWeek());
                currentTodo.setMonth(jc.getMonth());

                dbHelper.updateTodo(currentTodo);
                Log.d(TAG, "Update todo: Done!");
            } catch (Exception e) {
                Log.d(TAG, "Update todo error: " + e.getMessage());
            }

        } else {
            try {

                // generate new todos id
                SharedPreferences prefs = getSharedPreferences(Utils.SPK, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                int newId = prefs.getInt(Utils.TODO_COUNT_KEY, 0);

                // creating a todos
                if (currentType == TodoItem.Type.NOTEITEM)

                    currentTodo = new NoteItem(newId, title.getText().toString(), new Pair<>(description.getText().toString(), 0), current_date, null, new Time(start_time.getTime()), new Time(end_time.getTime())
                            , Utils.loggedInUser, currentState, TodoItem.Type.NOTEITEM);

                else if (currentType == TodoItem.Type.CHECKBOX) {

                    ArrayList<Pair<String, Integer>> contents = new ArrayList<>();
                    for (EditCheckBox box : boxes)
                        contents.add(new Pair<>(box.getText(), box.isChecked() ? 1 : 0));

                    currentTodo = new CheckBoxItem(newId, title.getText().toString(), contents, current_date, null, new Time(start_time.getTime()), new Time(end_time.getTime())
                            , Utils.loggedInUser, currentState, TodoItem.Type.CHECKBOX);
                }

                currentTodo.setDayOfWeek(jc.getDayOfWeek());
                currentTodo.setMonth(jc.getMonth());
                dbHelper.createTodo(currentTodo);

                editor.putInt(Utils.TODO_COUNT_KEY, newId + 1);
                editor.commit();

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        // todo add draft settings

        notificationHandler = new NotificationHandler(currentTodo.getId(), Utils.CHANNEL_ID, this);
        setAlerts();
        checkComment();
        finish();
        return true;
    }

    public void addEditListener(int value) {
        boolean found = false;
        int size = boxes.size();
        EditCheckBox editCheckBox = new EditCheckBox(context, activity);
        for (int i = 0; i < size; i++) {
            EditCheckBox current = boxes.get(i);
            Log.d(TAG, "addEditListener: " + current.getValue());
            if (current.getValue() == value && !found) {
                editCheckBox.setValue(value);
                boxes.add(i + 1, editCheckBox);
                checkBoxLayout.addView(editCheckBox, i + 1);
                editCheckBox.getEditText().requestFocus();
                found = true;
            }

            if (found) boxes.get(i + 1).incValue();
        }
    }

    public void removeEditListener(int value) {
        Log.d(TAG, "removeEditListener: " + value);
        boolean found = true;
        final int size = boxes.size();

        if (size == 1)
            return;

        for (int i = size - 1; i >= 0; i--) {
            EditCheckBox current = boxes.get(i);
            if (current.getValue() == value) {
                boxes.get(i).decValue();
                boxes.remove(i);

                if (i == 0)
                    boxes.get(i).getEditText().requestFocus();
                else
                    boxes.get(i - 1).getEditText().requestFocus();

                checkBoxLayout.removeViewAt(i);
                found = false;
            }

            if (found)
                boxes.get(i).decValue();

        }
    }

    public void closeAllSheets() {
        bottomSheetAlert.close();
        bottomSheetTime.close();
        bottomSheetState.close();
    }

    public void alertsUI(boolean[] types, Menu menu) {
        MenuItem alertItem = menu.findItem(R.id.alert);

        View actionView = alertItem.getActionView();
        TextView textView = actionView.findViewById(R.id.cart_badge);

        int count = 0;
        for (boolean b : types)
            if (b)
                count++;
        if (count == 0)
            textView.setVisibility(View.GONE);
        else
            textView.setText("" + count);

        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetAlert.toggle();
            }
        });

        onTime.setChecked(types[0]);
        min15Before.setChecked(types[1]);
        min30Before.setChecked(types[2]);
        oneDayBefore.setChecked(types[3]);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void setAlerts() {
        if (start_time == null)
            return;
        boolean[] types = {onTime.isChecked(),
                min15Before.isChecked(),
                min30Before.isChecked(),
                oneDayBefore.isChecked()};

        long t = Long.sum(jc.getTimeInMillis(), TimeUnit.HOURS.toMillis(start_time.getHours()));
        t = Long.sum(t, TimeUnit.MINUTES.toMillis(start_time.getMinutes()));

        if (types[0])
            notificationHandler.scheduleNotification(t, title.getText().toString(), description.getText().toString());
        if (types[1])
            notificationHandler.scheduleNotification(Long.sum(t, -1 * TimeUnit.MINUTES.toMillis(15)), title.getText().toString(), description.getText().toString());
        if (types[2])
            notificationHandler.scheduleNotification(Long.sum(t, -1 * TimeUnit.MINUTES.toMillis(30)), title.getText().toString(), description.getText().toString());
        if (types[3])
            notificationHandler.scheduleNotification(Long.sum(t, -1 * TimeUnit.DAYS.toMillis(1)), title.getText().toString(), description.getText().toString());
        dbHelper.setNotifications(currentTodo.getId(), types);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBackPressed() {
        if (bottomSheetTime.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetTime.close();
        else super.onBackPressed();
    }

    public String getDescription() {
        if (currentType == TodoItem.Type.NOTEITEM)
            return description.getText().toString();
        else {
            String description = "";
            for (EditCheckBox box : boxes)
                description += (String.format("%s %s\n", box.isChecked() ? "+" : "-", box.getText()));
            return description;
        }
    }

    public void shareTodo() {
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT,
                String.format("%s\n%s", title.getText().toString(), getDescription()));
        intent.setType("text/plain");
        startActivity(Intent.createChooser(intent, getString(R.string.share_chooser)));
    }

    public void deleteTodo() {
        dbHelper.dropTodo(currentTodo.getId());
        Toast.makeText(context, R.string.todo_delete_toast, Toast.LENGTH_SHORT).show();
        finish();
    }

    public void checkComment() {
        int count = dbHelper.todosCount();
        SharedPreferences prefs = getSharedPreferences(Utils.SPK, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        boolean firstComment = prefs.getBoolean(Utils.FIRST_COMMENT, false);
        boolean secondComment = prefs.getBoolean(Utils.FIRST_COMMENT, false);
        try {
            if ((count == 10 && !firstComment) || (count == 50 && !secondComment)) {

                if (firstComment)
                    editor.putBoolean(Utils.SECOND_COMMENT, true);
                else
                    editor.putBoolean(Utils.FIRST_COMMENT, true);
                editor.commit();

                // cafe bazaar intent
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setData(Uri.parse("bazaar://details?id=" + "com.techsanelab.doneen"));
                intent.setPackage("com.farsitel.bazaar");
                startActivity(intent);
            }
        } catch (Exception e) {
            Log.e(TAG, "checkComment: ", e);
        }
    }

}
