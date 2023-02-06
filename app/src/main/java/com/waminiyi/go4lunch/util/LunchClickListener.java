package com.waminiyi.go4lunch.util;

import com.waminiyi.go4lunch.model.Lunch;

public interface LunchClickListener {
    /**
     * Called when the user view  is clicked
     *
     * @param lunch : lunch to want to view
     */
    void onLunchClick(Lunch lunch);

}
