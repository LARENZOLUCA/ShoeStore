package com.shoestore.controller;

import com.shoestore.model.User;

public class UserSession {
    private static UserSession instance;
    private User currentUser;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isGuest() {
        return currentUser == null;
    }

    public boolean isAdmin() {
        return currentUser != null && "ADMIN".equals(currentUser.getRole());
    }

    public boolean isManager() {
        return currentUser != null && "MANAGER".equals(currentUser.getRole());
    }

    public boolean isClient() {
        return currentUser != null && "CLIENT".equals(currentUser.getRole());
    }
}