package com.waminiyi.go4lunch.util;

import com.waminiyi.go4lunch.model.Restaurant;

import java.util.Comparator;

public class RestaurantComparator {
    public static class SortByNearest implements Comparator<Restaurant> {

        @Override
        public int compare(Restaurant left, Restaurant right) {
            return left.getDistance() - right.getDistance();
        }
    }

    public static class SortByRating implements Comparator<Restaurant> {

        @Override
        public int compare(Restaurant left, Restaurant right) {
            return Float.compare(right.getRating(), left.getRating());
        }
    }
}
