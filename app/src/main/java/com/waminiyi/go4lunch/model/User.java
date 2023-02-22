package com.waminiyi.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.Objects;

public class User {
    private String uId;
    private String userName;
    @Nullable
    private String urlPicture;

    @Nullable
    private String userLunch;

    @Nullable
    private String team;

    private boolean isAdmin;

    @Nullable
    public String getTeam() {
        return team;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public User() {
    }

    public User(String uId, String userName, @Nullable String urlPicture) {
        this.uId = uId;
        this.userName = userName;
        this.urlPicture = urlPicture;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return getuId().equals(user.getuId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getuId());
    }

    @Nullable
    public String getUserLunch() {
        return userLunch;
    }

    public void setUserLunch(@Nullable String userLunch) {
        this.userLunch = userLunch;
    }
}
