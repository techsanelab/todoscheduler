package com.techsanelab.todo.entity;

public class User {

    Integer id;
    String name;
    String userName;
    String accessToken;

    // for selecting
    public User(Integer id) {
        this.id = id;
    }

    // for login
    public User(Integer id, String name, String userName, String accessToken) {
        this.id = id;
        this.name = name;
        this.userName = userName;
        this.accessToken = accessToken;
    }

    public User(String name, String userName) {
        this.name = name;
        this.userName = userName;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}
