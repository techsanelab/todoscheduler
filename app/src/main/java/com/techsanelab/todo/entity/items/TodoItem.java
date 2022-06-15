package com.techsanelab.todo.entity.items;

import java.io.Serializable;
import java.sql.Time;

import com.techsanelab.todo.entity.User;

public abstract class TodoItem implements Exportable, Serializable {

    public enum Type {
        NOTEITEM(0),
        CHECKBOX(1),
        HABIT(2);

        private int value;

        Type(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    public enum State {
        COMPLETE(0),
        INCOMPLETE(2),
        INPROGRESS(1);

        private int value;

        State(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
    }

    Integer id,dayOfWeek,month;
    String title;
    String startDate, finishDate;
    java.sql.Time startTime, finishTime;
    java.sql.Date dueDate;
    User owner;
    User[] invited;
    State state;
    Type type;

    public TodoItem(){}

    public TodoItem(Integer id) {
        this.id = id;
    }

    public TodoItem(String title, String startDate, String finishDate, Time startTime, Time finishTime, User owner, State state, Type type) {
        this.title = title;
        this.startDate = startDate;
        this.finishDate = finishDate;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.owner = owner;
        this.state = state;
        this.type = type;
    }

    public Boolean isComplete() {
        return state == State.COMPLETE;
    }

    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Time finishTime) {
        this.finishTime = finishTime;
    }

    public java.sql.Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(java.sql.Date dueDate) {
        this.dueDate = dueDate;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public User[] getInvited() {
        return invited;
    }

    public void setInvited(User[] invited) {
        this.invited = invited;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(Integer dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }
}
