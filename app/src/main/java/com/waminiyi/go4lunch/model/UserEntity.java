package com.waminiyi.go4lunch.model;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserEntity {

    private String uId;
    private String userName;
    @Nullable
    private String userEmail;
    @Nullable
    private String urlPicture;
    @Nullable
    private String userPhone;

    private ArrayList <String> favoriteRestaurant;

    public UserEntity() { }

    public UserEntity(String uId, String userName, @Nullable String userEmail, @Nullable String userPhone, @Nullable String urlPicture) {
        this.uId = uId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.urlPicture = urlPicture;
        this.userPhone = userPhone;
        this.favoriteRestaurant=new ArrayList<>();
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
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public ArrayList<String> getFavoriteRestaurant() {
        return favoriteRestaurant;
    }

    public void setFavoriteRestaurant(ArrayList<String> favoriteRestaurant) {
        this.favoriteRestaurant = favoriteRestaurant;
    }

    @Nullable
    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(@Nullable String userPhone) {
        this.userPhone = userPhone;
    }

    public User toUser(){
        return new User(uId,userName,urlPicture);
    }

}
