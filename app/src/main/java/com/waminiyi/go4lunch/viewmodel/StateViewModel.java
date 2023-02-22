package com.waminiyi.go4lunch.viewmodel;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.waminiyi.go4lunch.repository.StateRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class StateViewModel extends ViewModel {

    private final StateRepository mStateRepository;

    @Inject
    public StateViewModel(StateRepository stateRepository) {
        mStateRepository = stateRepository;
    }

    public void saveMapState(GoogleMap map) {
        mStateRepository.saveMapState(map);
    }

    public CameraPosition getSavedCameraPosition() {
        return mStateRepository.getSavedCameraPosition();
    }

    public void saveRestaurantListPosition(int position) {
        mStateRepository.saveRestaurantListPosition(position);
    }

    public int getSavedRestaurantListPosition() {
        return mStateRepository.getSavedRestaurantListPosition();
    }

    public void saveUserListPosition(int position) {
        mStateRepository.saveUserListPosition(position);
    }

    public int getSavedUserListPosition() {
        return mStateRepository.getSavedUserListPosition();
    }
}
