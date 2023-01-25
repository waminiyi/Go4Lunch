package com.waminiyi.go4lunch.model;

import java.util.Map;

public class RestaurantRating {
    private Map<String, Rating> ratingMap;

    public Rating getRating(String restaurantId){
        return ratingMap.get(restaurantId);
    }
}
