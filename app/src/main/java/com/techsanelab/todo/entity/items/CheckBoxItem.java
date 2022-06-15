package com.techsanelab.todo.entity.items;

import android.util.Pair;

import java.sql.Time;
import java.util.ArrayList;

import com.techsanelab.todo.entity.User;

public class CheckBoxItem extends TodoItem {

    ArrayList<Pair<String, Integer>> content;

    public CheckBoxItem(Integer id) {
        super(id);
    }

    public CheckBoxItem(String title, String startDate, String finishDate, Time startTime, Time finishTime, User owner, State state, Type type) {
        super(title, startDate, finishDate, startTime, finishTime, owner, state, type);
    }

    public CheckBoxItem(int id, String title, ArrayList<Pair<String, Integer>> content, String startDate, String finishDate, Time startTime, Time finishTime, User owner, State state, Type type) {
        super(title, startDate, finishDate, startTime, finishTime, owner, state, type);
        this.id = id;
        this.content = content;
    }


    @Override
    public Object getContent() {
        return content;
    }

    @Override
    public void setContent(Object content) {
        this.content = (ArrayList<Pair<String, Integer>>) content;
    }

    @Override
    public String getDescription() {
        String description = "";
        for (Pair<String, Integer> content : this.content)
            description += String.format("%s %s\n", content.second == 1 ? "+" : "-", content.first);
        return description;
    }
}
