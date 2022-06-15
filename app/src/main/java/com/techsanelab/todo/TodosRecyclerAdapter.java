package com.techsanelab.todo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.techsanelab.todo.entity.items.TodoItem;

import static java.lang.Math.abs;

public class TodosRecyclerAdapter extends RecyclerView.Adapter<TodosRecyclerAdapter.ViewHolder> {

    private static final String TAG = "TodosRecyclerAdapter";
    private List<TodoItem> todoItems;
    private int layourId;
    private Context context;
    private List<TodoItem> temp = new ArrayList<>();
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
    private EasyFont easyFont;
    private Activity activity;
    private ViewHolder holder;

    private ArrayList<Integer> editItems;
    private Boolean isOnEdit = false;
    private RecyclerView recyclerView;

    public TodosRecyclerAdapter(List<TodoItem> todoItems, int layourId, Context context, Activity activity) {

        filterByStartTime(todoItems);
        filterByState(todoItems);
        editItems = new ArrayList<>();
        this.todoItems = todoItems;
        this.layourId = layourId;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(this.layourId, parent, false);
        easyFont = new EasyFont(activity, view);
        ViewHolder viewHolder = new ViewHolder(view);
        holder = viewHolder;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TodoItem todoItem = todoItems.get(position);
        holder.title.setText(todoItem.getTitle());
        try {
            easyFont.tfBoldFragment(holder.title.getId());
            easyFont.tfFragment(holder.summery.getId());
            easyFont.tfFragment(holder.timePeriod.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }


        String summery = "";
        summery = todoItem.getDescription();

        if (summery.length() <= 120)
            holder.summery.setText(summery);
        else
            holder.summery.setText(String.format("%s...", summery.substring(0, 120)));


        try {
            holder.timePeriod.setText(String.format("%s - %s", sdf.format(sdf.parse(todoItem.getFinishTime().toString())), sdf.format(sdf.parse(todoItem.getStartTime().toString()))));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (getMode() && needEdit(position)) {
            holder.todoCard.setBackgroundResource(R.drawable.back_selected);
        } else {
            switch (todoItem.getState()) {
                case COMPLETE:
                    holder.todoCard.setBackgroundResource(R.drawable.back_card_complete);
                    break;
                case INPROGRESS:
                    holder.todoCard.setBackgroundResource(R.drawable.back_card_inprogress);
                    break;
                default:
                    holder.todoCard.setBackgroundResource(R.drawable.back_card_incomplete);
            }
        }
    }

    @Override
    public int getItemCount() {
        return todoItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public TextView title, summery, timePeriod;
        public View todoCard;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try {
                title = (TextView) itemView.findViewById(R.id.title);
                summery = (TextView) itemView.findViewById(R.id.summery);
                timePeriod = (TextView) itemView.findViewById(R.id.time_period);
                todoCard = (View) itemView.findViewById(R.id.card_back);
            } catch (Exception e) {
                // todo handle here
            }
        }

    }

    public void updateItems(List<TodoItem> todoItems){
        filterByStartTime(todoItems);
        filterByState(todoItems);
        this.todoItems = todoItems;
    }

    public void removeItem(int position) {
        Log.d(TAG, "removeItem: " + position);
        todoItems.remove(position);
    }

    public int getEditCount() {
        return editItems.size();
    }

    public void addEditItem(int position) {
        if (!needEdit(position))
            editItems.add(position);
        else
            removeEditItem(position);
        notifyItemChanged(position);
        Log.d(TAG, "addEditItem: " + getEditItems().toString());
    }

    public void removeEditItem(int position) {
        for (int i = 0; i < getEditItems().size(); i++) {
            if (editItems.get(i) == position)
                editItems.remove(i);
        }
        notifyItemChanged(position);
    }

    public void deleteEditItems() {
        DBHelper dbHelper = DBHelper.getInstance(context);
        editItems.sort((integer, t1) -> {
            if (integer > t1)
                return 1;
            else if (integer.equals(t1))
                return 0;
            else
                return -1;
        });

        Log.d(TAG, "deleteEditItems: " + todoItems.toString());
        for (int i = 0; i < getEditCount(); i++) {
            dbHelper.dropTodo(todoItems.get(editItems.get(i)).getId());
            todoItems.remove(editItems.get(i));
            notifyItemRangeRemoved(editItems.get(i), 1);
            notifyItemRangeChanged(editItems.get(i), 1);
        }
    }

    public List<TodoItem> getTodoItems() {
        return todoItems;
    }

    public ArrayList<Integer> getEditItems() {
        return editItems;
    }

    public boolean toggleMode() {
        isOnEdit = !isOnEdit;
        return isOnEdit;
    }

    public boolean needEdit(int position) {
        for (int i = 0; i < getEditItems().size(); i++) {
            if (editItems.get(i) == position)
                return true;
        }
        return false;
    }

    public void editMode(@NonNull int position) {
        Log.d(TAG, "editMode: " + position);
        isOnEdit = true;
        addEditItem(position);
    }

    public void editMode() {
        isOnEdit = true;
    }

    public void defaultMode() {
        isOnEdit = false;
        for (int i = 0; i < getEditCount(); i++) {
            notifyItemChanged(editItems.get(i));
        }
        editItems.clear();
    }

    public boolean getMode() {
        return isOnEdit;
    }

    /*
    * Filters are for sorting items in recycler view.
    * */

    public void filterByStartTime(List list) {

        // sort items by start times
        Collections.sort(list, (Comparator<TodoItem>) (lhs, rhs) -> {
            if (rhs.getStartTime().getTime() >= lhs.getStartTime().getTime())
                return -1;
            else
                return 1;
        });
    }

    public void filterByDuration(List list) {

        // sort items by duration
        Collections.sort(list, (Comparator<TodoItem>) (lhs, rhs) -> {
            if (abs(rhs.getStartTime().getTime() - rhs.getFinishTime().getTime()) <= abs(lhs.getStartTime().getTime() - lhs.getFinishTime().getTime()))
                return -1;
            else
                return 1;
        });
    }

    public void filterByState(List list) {

        // sort items by state
        Collections.sort(list, (Comparator<TodoItem>) (lhs, rhs) -> {
            if (rhs.getState().getValue() >= lhs.getState().getValue())
                return -1;
            else
                return 1;
        });
    }
}
