package org.example.HW19.utils;

import org.example.HW19.entity.User;
public class SecurityUtils {
    private static User user = null;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        SecurityUtils.user = user;
    }
}
