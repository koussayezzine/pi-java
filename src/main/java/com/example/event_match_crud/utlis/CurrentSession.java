package com.example.event_match_crud.utlis;


import com.example.event_match_crud.models.User;

public class CurrentSession {

    private static CurrentSession instance;
    private User user;

    private CurrentSession() {}

    public static CurrentSession getInstance() {
        if (instance == null) {
            instance = new CurrentSession();
        }
        return instance;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

    public void clearSession() {
        user = null;
        instance = null;
    }
}
