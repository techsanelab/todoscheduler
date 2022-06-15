package com.techsanelab.todo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.List;

import com.techsanelab.todo.entity.items.Habit;

public class HabitsRecyclerAdapter extends RecyclerView.Adapter<HabitsRecyclerAdapter.ViewHolder> {

    private List<Habit> habits;
    private Context context;
    private EasyFont easyFont;
    private Activity activity;

    public HabitsRecyclerAdapter(List<Habit> habits, Context context, Activity activity) {
        this.habits = habits;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_habit, parent, false);
        easyFont = new EasyFont(activity, view);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Habit habit = habits.get(position);


        holder.parent.setBackground(Utils.getGradientDrawable(habit.getColor(), 0.85f));
        holder.title.setText(habit.getTitle());
        holder.title.setTextColor(Utils.manipulateColor(Color.parseColor(habit.getColor()), 0.45f));

        Drawable icon = null;
        try {
            icon = Drawable.createFromStream(context.getAssets().open("icons/" + habit.getIcon()), null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        holder.image.setImageDrawable(icon);
    }

    @Override
    public int getItemCount() {
        return habits.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView image;
        public TextView title;
        public CardView cardView;
        public ConstraintLayout parent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            try {
                cardView = itemView.findViewById(R.id.habit_card);
                title = itemView.findViewById(R.id.habit_title);
                image = itemView.findViewById(R.id.habit_image);
                parent = itemView.findViewById(R.id.parent);
            } catch (Exception ignored) {

            }
        }
    }

    public Habit getItem(int position) {
        return habits.get(position);
    }

}
