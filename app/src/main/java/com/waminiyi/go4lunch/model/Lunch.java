package com.waminiyi.go4lunch.model;

public class Lunch {
    private String userId;
    private String restaurantId;
    private String restaurantName;

    public Lunch() {
    }

    public Lunch(String userId, String restaurantId,
                 String restaurantName) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
    }

    public String getUserId() {
        return userId;
    }


    public String getRestaurantId() {
        return restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

}
