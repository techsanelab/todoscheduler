package com.techsanelab.todo.fragments;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.techsanelab.todo.AddTodoActivity;
import com.techsanelab.todo.DBHelper;
import com.techsanelab.todo.DOWViewModel;
import com.techsanelab.todo.DayOfWeek;
import com.techsanelab.todo.DialogHandler;
import com.techsanelab.todo.EasyFont;
import com.techsanelab.todo.ItemClickSupport;
import com.techsanelab.todo.CustomCalendar;
import com.techsanelab.todo.PurchaseReceiver;
import com.techsanelab.todo.R;
import com.techsanelab.todo.SubsHelper;
import com.techsanelab.todo.TodoPurchase;
import com.techsanelab.todo.TodoPurchaseIC;
import com.techsanelab.todo.TodosRecyclerAdapter;
import com.techsanelab.todo.Utils;
import com.techsanelab.todo.entity.items.TodoItem;


public class CalendarFragment extends Fragment {
    private static final String TAG = "CalendarFragment";
    ViewPager daysOfWeek;
    DaysPager daysPager;
    CustomCalendar jc, current_date;
    View view;
    TextView monthTitle;
    CalendarFragment me;
    FloatingActionButton fab;
    LinearLayout emptyList;
    ConstraintLayout layout;
    Gson gson = new Gson();
    MaterialButton todayButton, editButton;
    int todayIndex;
    MaterialButtonToggleGroup currentGroup, todayGroup;
    private Boolean todayFlag = false;
    RecyclerView todosRecyclerView;
    TodosRecyclerAdapter todosRecyclerAdapter;
    String monthTemp;
    List<TodoItem> todoItems = new ArrayList<>();
    List<DayOfWeek> dayOfWeeks = new ArrayList<>();
    DBHelper dbHelper;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    TodoPurchase todoPurchase;

    @SuppressLint("DefaultLocale")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_calendar, container, false);
        me = this;
        jc = new CustomCalendar();
        current_date = jc;
        emptyList = view.findViewById(R.id.empty_layout);
        dbHelper = DBHelper.getInstance(getContext());
        layout = view.findViewById(R.id.layout);

        prefs = Objects.requireNonNull(getContext()).getSharedPreferences(Utils.SPK, Context.MODE_PRIVATE);
        editor = prefs.edit();

        boolean first_purchase = prefs.getBoolean(Utils.TODO_PURCHASE_KEY, true);
        if (first_purchase) {
            todoPurchase = new TodoPurchaseIC(getContext()).createInstance();
            setupPurchase(getContext());
            todoPurchase.save();
            editor.putBoolean(Utils.TODO_PURCHASE_KEY, false);
            editor.commit();
        } else {
            loadTodoPurchase();
            Log.d(TAG, "loadTodoPurchase: Load");
        }

        Log.d(TAG, "onCreateView: " + todoPurchase.getScope());

        // FAB
        fab = view.findViewById(R.id.add_todo);
        fab.setOnClickListener(view -> {
            Log.d(TAG, "ACC: " + todoPurchase.getAcc());
            Log.d(TAG, "PACC: " + todoPurchase.getPeriodAcc());
            Log.d(TAG, "INDEX: " + todoPurchase.getIndex());

            SubsHelper.getInstance();
            if (prefs.getBoolean(Utils.PURCHASE_KEY, false) && !SubsHelper.prm && false) {
                DialogHandler.customDialog(getContext(), Objects.requireNonNull(getActivity()), getString(R.string.attention),
                        getString(R.string.submit_subs), null);
            } else {
                Intent intent = new Intent(getActivity(), AddTodoActivity.class);
                intent.putExtra("current_date", current_date.toString());
                intent.putExtra("month_title", monthTitle.getText().toString());
                intent.putExtra("mode", Utils.ActivityMode.ADD);
                startActivity(intent);
            }
        });

        EasyFont easyFont = new EasyFont(Objects.requireNonNull(getActivity()), view);
        monthTitle = view.findViewById(R.id.month_title);
        monthTitle.setText(String.format("%s %d %s", jc.getDayOfWeekString(), jc.getDay(), jc.getMonthString()));
        layout.setBackgroundResource(getSeasonId(jc.getSeason()));

        easyFont.tfBoldFragment(R.id.month_title);
        easyFont.tfFragment(R.id.empty_todos);

        daysOfWeek = view.findViewById(R.id.dayOfWeeks);


        try {
            daysPager = new DaysPager(getActivity().getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        daysPager.setCount(500);
        daysOfWeek.setAdapter(daysPager);
        todayIndex = daysOfWeek.getCurrentItem() + daysPager.getCount() / 2;
        daysOfWeek.setCurrentItem(todayIndex);
        daysOfWeek.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                monthTitle.setText(dayOfWeeks.get(position).getDays()[2].getMonthString());
                layout.setBackgroundResource(getSeasonId(dayOfWeeks.get(position).getDays()[2].getSeason()));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        todayButton = view.findViewById(R.id.today_button);
        todayButton.setOnClickListener(view -> findToday());

        editButton = view.findViewById(R.id.edit_view);
        editButton.setOnClickListener(view -> {
            todosRecyclerAdapter.editMode();
            toggleEdit();
        });


//        TodoViewModel model = new ViewModelProvider.NewInstanceFactory().create(TodoViewModel.class);
//        todoItems = model.getTodos(dbHelper, current_date.toString());

        try {
            todosRecyclerView = view.findViewById(R.id.todos_recycler);
            todosRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (dy > 0) {
                        fab.hide();
                    } else {
                        fab.show();
                    }

                }
            });

            todosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            todosRecyclerAdapter = new TodosRecyclerAdapter(todoItems, R.layout.layout_todo, getContext(), getActivity());
            notifyTodos();
            todosRecyclerView.setAdapter(todosRecyclerAdapter);

            ItemClickSupport.addTo(todosRecyclerView).setOnItemClickListener((recyclerView, position, v) -> {

                if (todosRecyclerAdapter.getMode()) {
                    todosRecyclerAdapter.addEditItem(position);
                    if (todosRecyclerAdapter.getEditCount() == 0) {
                        todosRecyclerAdapter.defaultMode();
                        toggleEdit();
                    }
                } else {
                    Intent intent = new Intent(getActivity(), AddTodoActivity.class);
                    intent.putExtra("current_date", current_date.toString());
                    intent.putExtra("month_title", monthTitle.getText().toString());
                    intent.putExtra("mode", Utils.ActivityMode.EDIT.getValue());
                    intent.putExtra("type", todoItems.get(position).getType().getValue());
                    intent.putExtra("todo_item", gson.toJson(todoItems.get(position)));
                    startActivity(intent);
                }
            });

            ItemClickSupport.addTo(todosRecyclerView).setOnItemLongClickListener((recyclerView, position, v) -> {
                todosRecyclerAdapter.editMode(position);
                toggleEdit();
                return true;
            });

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "run: " + e.getMessage());
        }


        Utils.showCases(Objects.requireNonNull(getActivity()), Utils.CALENDAR_FIRST_KEY
                , new View[]{daysOfWeek, todayButton}
                , new int[]{R.string.dow_pager, R.string.today_button});

        return view;
    }

    public void setupPurchase(Context context) {
        Intent intent = new Intent(context, PurchaseReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(context, 12131, intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long time = Long.sum(System.currentTimeMillis(), 86400000L); // Every 24 hours
        manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, pending);
        todoPurchase.save();
    }

    public void emptyView() {
        emptyList.setVisibility(View.VISIBLE);
        todosRecyclerView.setVisibility(View.GONE);
        editButton.setVisibility(View.GONE);
    }

    public void notEmptyView() {
        emptyList.setVisibility(View.GONE);
        todosRecyclerView.setVisibility(View.VISIBLE);
        editButton.setVisibility(View.VISIBLE);
    }

    @SuppressLint("ResourceType")
    public void findToday() {
        try {
            daysOfWeek.setCurrentItem(todayIndex);
            todayGroup.check(3);
            todayFlag = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notifyTodos() {
        todoItems = dbHelper.selectTodoByStartDate(current_date.toString());
        Log.d(TAG, "notifyTodos: " + todoItems.size());
        todosRecyclerAdapter.updateItems(todoItems);
        todosRecyclerAdapter.notifyDataSetChanged();

        if (todoItems.size() != 0) notEmptyView();
        else emptyView();
    }

    @SuppressLint("DefaultLocale")
    public void currentDateListener(CustomCalendar cd, MaterialButtonToggleGroup group) {
        this.current_date = cd;
//        DateFormatSymbols dfs = new DateFormatSymbols();
//        Log.d(TAG, String.format("currentDateListener: dow:%d %s Month:%d %s", cd.getDayOfWeek(), cd.getDayOfWeekString(), cd.getMonth(),dfs.getMonths()[cd.getMonth()]));
        this.monthTitle.setText(String.format("%s %d %s", cd.getDayOfWeekString(), cd.getDay(), cd.getMonthString()));
        this.layout.setBackgroundResource(getSeasonId(cd.getSeason()));
        todosRecyclerView.post(() -> todosRecyclerView.scrollToPosition(0));

        notifyTodos();

        fab.show();
        currentGroup = group;
        Log.d(TAG, "current_date_listener: " + cd.toString());
    }

    public void setTodayGroup(MaterialButtonToggleGroup group) {
        Log.d(TAG, String.valueOf(group == null));
        todayGroup = group;
    }


    @Override
    public void onResume() {
        notifyTodos();
        todoPurchase = null;
        loadTodoPurchase();
        super.onResume();
    }

    public void loadTodoPurchase() {
        if (todoPurchase == null) {
            todoPurchase = new TodoPurchaseIC(getContext()).createInstance();
            todoPurchase.setAcc(prefs.getInt(Utils.TODO_PURCHASE_KEY + "acc", 0));
            todoPurchase.setPeriodAcc(prefs.getInt(Utils.TODO_PURCHASE_KEY + "periodacc", 0));
            todoPurchase.setIndex(prefs.getInt(Utils.TODO_PURCHASE_KEY + "index", 0));
            todoPurchase.setPeriodItems(prefs.getStringSet(Utils.TODO_PURCHASE_KEY + "list", null));
            todoPurchase.updateAvg();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public class DaysPager extends FragmentStatePagerAdapter {

        private int count;

        public DaysPager(@NonNull FragmentManager fm, int behavior) throws InterruptedException {
            super(fm, behavior);
            DOWViewModel model = new ViewModelProvider.NewInstanceFactory().create(DOWViewModel.class);
            dayOfWeeks = model.getDayOfWeeks(me);
        }

        @NonNull
        @Override
        public DayOfWeek getItem(int position) {
            DayOfWeek dayOfWeek = dayOfWeeks.get(position);
            return dayOfWeek;
        }

        @Override
        public void finishUpdate(@NonNull ViewGroup container) {
            super.finishUpdate(container);
            if (currentGroup != null && !todayFlag)
                currentGroup.clearChecked();
            todayFlag = false;
        }

        @Override
        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }

    public void toggleEdit() {
        if (todosRecyclerAdapter.getMode()) {
            fab.hide();
            view.findViewById(R.id.ar_left).setVisibility(View.GONE);
            view.findViewById(R.id.ar_right).setVisibility(View.GONE);
            daysOfWeek.setVisibility(View.GONE);
            monthTemp = monthTitle.getText().toString();
            monthTitle.setText(getString(R.string.todo_edit));
            todayButton.setIconResource(R.drawable.ic_round_close_24);
            todayButton.setOnClickListener(view -> {
                todosRecyclerAdapter.defaultMode();
                toggleEdit();
            });

            editButton.setIconResource(R.drawable.ic_twotone_delete_outline_24);
            editButton.setOnClickListener(view -> {
                for (int position : todosRecyclerAdapter.getEditItems()) {
                    todoPurchase.query(-1, todosRecyclerAdapter.getTodoItems().get(position).getId());
                }

                todosRecyclerAdapter.deleteEditItems();
                todosRecyclerAdapter.defaultMode();
                notifyTodos();
                toggleEdit();
            });
        } else {
            fab.show();
            view.findViewById(R.id.ar_left).setVisibility(View.VISIBLE);
            view.findViewById(R.id.ar_right).setVisibility(View.VISIBLE);
            daysOfWeek.setVisibility(View.VISIBLE);
            monthTitle.setText(monthTemp);

            editButton.setIconResource(R.drawable.ic_twotone_edit_24);
            editButton.setOnClickListener(view -> {
                todosRecyclerAdapter.editMode();
                toggleEdit();
            });

            todayButton.setIconResource(R.drawable.ic_twotone_today_24);
            todayButton.setOnClickListener(view -> findToday());
        }
    }

    public int getSeasonId(int code) {

        switch (code) {
            case 2:
                Utils.changeStatus(Objects.requireNonNull(getActivity()), Objects.requireNonNull(getContext()), R.color.summer);
                fab.setBackgroundTintList(ColorStateList.valueOf(getActivity().getColor(R.color.summer)));
                return R.drawable.back_summer;
            case 3:
                Utils.changeStatus(Objects.requireNonNull(getActivity()), Objects.requireNonNull(getContext()), R.color.fall);
                fab.setBackgroundTintList(ColorStateList.valueOf(getActivity().getColor(R.color.fall)));
                return R.drawable.back_fall;
            case 4:
                Utils.changeStatus(Objects.requireNonNull(getActivity()), Objects.requireNonNull(getContext()), R.color.winter);
                fab.setBackgroundTintList(ColorStateList.valueOf(getActivity().getColor(R.color.winter)));
                return R.drawable.back_winter;
            default:
                Utils.changeStatus(Objects.requireNonNull(getActivity()), Objects.requireNonNull(getContext()), R.color.spring);
                fab.setBackgroundTintList(ColorStateList.valueOf(getActivity().getColor(R.color.spring)));
                return R.drawable.back_spring;
        }
    }

}
