package com.techsanelab.todo.entity;

public class LoggedInUser extends User {

    public LoggedInUser(Integer id, String name, String userName, String accessToken) {
        super(id, name, userName, accessToken);
    }

}
