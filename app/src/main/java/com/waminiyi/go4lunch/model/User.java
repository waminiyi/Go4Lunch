package com.waminiyi.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.Objects;

public class User {
    private  String userId;
    private  String userName;
    @Nullable
    private  String userPictureUrl;

    public User() {
    }

    public User(String userId, String userName, @Nullable String userPictureUrl) {
        this.userId = userId;
        this.userName = userName;
        this.userPictureUrl = userPictureUrl;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    @Nullable
    public String getUserPictureUrl() {
        return userPictureUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getUserId().equals(user.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }

}
