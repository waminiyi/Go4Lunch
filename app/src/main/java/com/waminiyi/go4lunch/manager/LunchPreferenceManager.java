package com.waminiyi.go4lunch.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.waminiyi.go4lunch.model.Lunch;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class LunchPreferenceManager {

    private static final String LUNCH_RESTAURANT_ID = "restaurantId";
    private static final String LUNCH_RESTAURANT_NAME = "restaurantName";
    private static final String FAVORITE_RESTAURANT = "favorites";
    private static final String WAS_SAVED = "lunch_saved";

    private static final String PREFS_NAME = "LunchPrefs";

    private final SharedPreferences lunchPrefs;

    @Inject
    public LunchPreferenceManager(@ApplicationContext Context context) {
        lunchPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveCurrentLunch(Lunch lunch) {
        SharedPreferences.Editor editor = lunchPrefs.edit();

        editor.putString(LUNCH_RESTAURANT_ID, lunch.getRestaurantId());
        editor.putString(LUNCH_RESTAURANT_NAME, lunch.getRestaurantName());
        editor.putBoolean(WAS_SAVED, true);
        editor.apply();
    }


    public String getLunchRestaurantId() {
        return lunchPrefs.getString(LUNCH_RESTAURANT_ID, null);
    }

    public String getLunchRestaurantName() {
        return lunchPrefs.getString(LUNCH_RESTAURANT_NAME, null);
    }

    public boolean wasLunchSaved() {
        return lunchPrefs.getBoolean(WAS_SAVED, false);
    }


    public void clearLunch() {
        SharedPreferences.Editor editor = lunchPrefs.edit();

        editor.putString(LUNCH_RESTAURANT_ID, null);
        editor.putString(LUNCH_RESTAURANT_NAME, null);
        editor.putBoolean(WAS_SAVED, false);
        editor.apply();
    }
}
