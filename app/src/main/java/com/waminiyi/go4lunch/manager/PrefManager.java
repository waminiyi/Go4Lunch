package com.waminiyi.go4lunch.manager;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class PrefManager {

    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String DEFAULT_LONGITUDE = "longitude";
    private static final String DEFAULT_LATITUDE = "latitude";
    private static final String WAS_SAVED = "location_saved";
    private static final String RADIUS = "RADIUS";
    private static final String PREFS_NAME = "LocationPrefs";

    private final SharedPreferences prefs;

    @Inject
    public PrefManager(@ApplicationContext Context context) {
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
}
