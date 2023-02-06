package com.waminiyi.go4lunch.model;

public class Lunch {
    private String userId;
    private String userName;
    private String userPictureUrl;
    private String restaurantId;
    private String restaurantName;

    public Lunch(String userId, String userName, String userPictureUrl, String restaurantId,
                 String restaurantName) {
        this.userId = userId;
        this.userName = userName;
        this.userPictureUrl = userPictureUrl;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }

    public Lunch() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPictureUrl() {
        return userPictureUrl;
    }

    public void setUserPictureUrl(String userPictureUrl) {
        this.userPictureUrl = userPictureUrl;
    }
}
