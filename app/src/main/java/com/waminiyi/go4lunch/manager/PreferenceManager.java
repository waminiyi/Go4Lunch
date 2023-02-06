package com.waminiyi.go4lunch.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.waminiyi.go4lunch.model.Lunch;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class PreferenceManager {

    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String DEFAULT_LONGITUDE = "longitude";
    private static final String DEFAULT_LATITUDE = "latitude";
    private static final String WAS_SAVED = "location_saved";
    private static final String RADIUS = "RADIUS";
    private static final String LUNCH_RESTAURANT_ID = "restaurantId";
    private static final String LUNCH_RESTAURANT_NAME = "restaurantName";
    private static final String PICTURE_URL = "pictureUrl";
    private static final String USER_NAME = "userName";
    private static final String USER_ID = "userId";
    private static final String FAVORITE_RESTAURANT = "favorites";
    private static final String WAS_LUNCH_SAVED = "lunch_saved";

    private static final String PREFS_NAME = "LocationPrefs";
    private static final String USER_MAIL = "userMail";

    private final SharedPreferences prefs;

    @Inject
    public PreferenceManager(@ApplicationContext Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void saveLastLocation(double latitude, double longitude) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putFloat(LATITUDE, (float) latitude);
        editor.putFloat(LONGITUDE, (float) longitude);
        editor.putBoolean(WAS_SAVED, true);
        editor.apply();
    }

    public void saveDefaultLocation(double latitude, double longitude) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putFloat(DEFAULT_LATITUDE, (float) latitude);
        editor.putFloat(DEFAULT_LONGITUDE, (float) longitude);
        editor.putBoolean(WAS_SAVED, false);
        editor.apply();
    }

    public void saveDefaultRadius(int radius) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(RADIUS, radius);
        editor.apply();
    }

    public double getSavedLatitude() {
        return prefs.getFloat(LATITUDE, 0);
    }

    public double getSavedLongitude() {
        return prefs.getFloat(LONGITUDE, 0);
    }

    public double getDefaultLatitude() {
        return prefs.getFloat(DEFAULT_LATITUDE, 0);
    }

    public double getDefaultLongitude() {
        return prefs.getFloat(DEFAULT_LONGITUDE, 0);
    }

    public boolean wasLocationSaved() {
        return prefs.getBoolean(WAS_SAVED, false);
    }

    public int getRadius() {
        return prefs.getInt(RADIUS, 2000);
    }

    public void clearLastLocation() {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putFloat(LATITUDE, 0);
        editor.putFloat(LONGITUDE, 0);
        editor.putBoolean(WAS_SAVED, false);
        editor.apply();
    }

    public void saveCurrentLunch(Lunch lunch) {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(LUNCH_RESTAURANT_ID, lunch.getRestaurantId());
        editor.putString(LUNCH_RESTAURANT_NAME, lunch.getRestaurantName());
        editor.putBoolean(WAS_LUNCH_SAVED, true);
        editor.apply();
    }

    public String getLunchRestaurantId() {
        return prefs.getString(LUNCH_RESTAURANT_ID, null);
    }

    public String getLunchRestaurantName() {
        return prefs.getString(LUNCH_RESTAURANT_NAME, null);
    }

    public boolean wasLunchSaved() {
        return prefs.getBoolean(WAS_LUNCH_SAVED, false);
    }


    public void clearLunch() {
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString(LUNCH_RESTAURANT_ID, null);
        editor.putString(LUNCH_RESTAURANT_NAME, null);
        editor.putBoolean(WAS_SAVED, false);
        editor.apply();
    }

    public void saveUserName(String userName) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_NAME, userName);
        editor.apply();
    }

    public void saveUserId(String userId) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_ID, userId);
        editor.apply();
    }

    public void saveUserMail(String mail) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(USER_MAIL, mail);
        editor.apply();
    }

    public void saveUserPictureUrl(String url) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PICTURE_URL, url);
        editor.apply();
    }

    public String getUserName() {
        return prefs.getString(USER_NAME, null);
    }

    public String getUserMail() {
        return prefs.getString(USER_MAIL, null);
    }

    public String getUserId() {
        return prefs.getString(USER_ID, null);
    }

    public String getUserPictureUrl() {
        return prefs.getString(PICTURE_URL, null);
    }

    public void saveFavoriteRestaurants(List<String> favorites) {
        SharedPreferences.Editor editor = prefs.edit();
        Set<String> fav = new HashSet<>(favorites);
        editor.putStringSet(FAVORITE_RESTAURANT, fav);
        editor.apply();
    }

    public Set<String> getFavoriteRestaurants(){
        return prefs.getStringSet(FAVORITE_RESTAURANT,new HashSet<>());
    }


}
