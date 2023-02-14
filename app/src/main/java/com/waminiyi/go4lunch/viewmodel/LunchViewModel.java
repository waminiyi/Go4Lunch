package com.waminiyi.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.repository.LunchRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class LunchViewModel extends ViewModel {

    private final LunchRepository lunchRepository;

    @Inject
    public LunchViewModel(LunchRepository lunchRepository) {
        this.lunchRepository = lunchRepository;
    }

    public void setCurrentUserLunch(Lunch lunch) {
        lunchRepository.setCurrentUserLunch(lunch);
    }

    public void deleteCurrentUserLunch(Lunch lunch) {
        lunchRepository.deleteCurrentUserLunch(lunch);
    }

    public void getCurrentUserLunchFromDb() {
        lunchRepository.getCurrentUserLunchFromDb();
    }

    public LiveData<Lunch> getCurrentUserLunch() {
        return lunchRepository.getCurrentUserLunch();
    }

    public void retrieveAllUsers() {
        lunchRepository.retrieveAllUsers();
    }

    public void getLunchesFromDb() {
        lunchRepository.getLunchesFromDb();
    }

    public LiveData<List<Lunch>> getUsersLunches() {
        return lunchRepository.getAllUsersLunches();
    }

    public void getCurrentRestaurantLunchesFromDb(String restaurantId) {
        lunchRepository.getCurrentRestaurantLunchesFromDb(restaurantId);
    }

    public LiveData<List<Lunch>> getCurrentRestaurantLunches() {
        return lunchRepository.getCurrentRestaurantLunches();
    }

    public void setLunchListener(FirebaseHelper.LunchListener listener) {
        lunchRepository.setLunchListener(listener);
    }

    public void listenToLunches() {
        lunchRepository.listenToLunches();
    }
}
