package com.techsanelab.todo.entity.items;

import android.util.Pair;

import java.io.Serializable;
import java.sql.Time;

import com.techsanelab.todo.entity.User;

public class NoteItem extends TodoItem implements Serializable {

    Pair<String, Integer> content;

    public NoteItem(Integer id) {
        super(id);
    }

    public NoteItem(String title, Pair<String, Integer> content, String startDate, String finishDate, Time startTime, Time finishTime, User owner, State state, Type type) {
        super(title, startDate, finishDate, startTime, finishTime, owner, state, type);
        this.content = content;
    }

    public NoteItem(int id, String title, Pair<String, Integer> content, String startDate, String finishDate, Time startTime, Time finishTime, User owner, State state, Type type) {
        super(title, startDate, finishDate, startTime, finishTime, owner, state, type);
        this.id = id;
        this.content = content;
    }

    @Override
    public Pair<String, Integer> getContent() {
        return content;
    }

    @Override
    public void setContent(Object content) {
        this.content = (Pair<String, Integer>) content;
    }

    @Override
    public String getDescription() {
        return this.content.first;
    }
}
