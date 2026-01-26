package com.synapseevent.utils;

import com.synapseevent.entities.User;

public class CurrentUser {
    private static User currentUser;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static boolean isAdmin() {
        return currentUser != null && "Admin".equals(currentUser.getRole().getName());
    }

    public static void logout() {
        currentUser = null;
    }
}