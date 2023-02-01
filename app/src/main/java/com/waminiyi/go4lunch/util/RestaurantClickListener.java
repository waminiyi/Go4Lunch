package com.waminiyi.go4lunch.util;

import com.waminiyi.go4lunch.model.Restaurant;

public interface RestaurantClickListener {
    /**
     * Called when the favorite button is clicked
     *
     * @param restaurant we want to add to favorites
     */
    void onFavoriteButtonClick(Restaurant restaurant);

    /**
     * Called when the lunch textView is clicked
     *
     * @param restaurant we want to go for lunch
     */
    void onLunchTextViewClick(Restaurant restaurant);

    /**
     * Called when the restaurant view is clicked
     *
     * @param restaurant we want show details for
     */
    void onRestaurantClick(Restaurant restaurant);
}
