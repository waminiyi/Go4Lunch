package com.waminiyi.go4lunch.repository;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import javax.inject.Inject;

public class StateRepository {

    private CameraPosition mCameraPosition;
    private int mRestaurantListPosition;
    private int mUserListPosition;

    @Inject
    public StateRepository() {
    }

    public void saveMapState(GoogleMap map) {
        if (map != null) {
            this.mCameraPosition = map.getCameraPosition();
        }
    }

    public CameraPosition getSavedCameraPosition() {
        return this.mCameraPosition;
    }

    public void saveRestaurantListPosition(int position) {
        this.mRestaurantListPosition = position;
    }

    public int getSavedRestaurantListPosition() {
        return this.mRestaurantListPosition;
    }

    public void saveUserListPosition(int position) {
        this.mUserListPosition = position;
    }

    public int getSavedUserListPosition() {
        return this.mUserListPosition;
    }

}
