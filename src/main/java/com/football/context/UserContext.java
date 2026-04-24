package com.football.context;

import com.football.vo.UserInfo;

public class UserContext {

    private static final ThreadLocal<UserInfo> USER_THREAD_LOCAL = new ThreadLocal<>();

    public static void setUser(UserInfo user) {
        USER_THREAD_LOCAL.set(user);
    }

    public static UserInfo getUser() {
        return USER_THREAD_LOCAL.get();
    }

    public static void clear() {
        USER_THREAD_LOCAL.remove();
    }

    public static Long getUserId() {
        UserInfo user = getUser();
        return user != null ? user.getUserId() : null;
    }

    public static String getUsername() {
        UserInfo user = getUser();
        return user != null ? user.getUsername() : null;
    }

    public static String getPhone() {
        UserInfo user = getUser();
        return user != null ? user.getPhone() : null;
    }
}
