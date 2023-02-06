package com.waminiyi.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.repository.LunchRepository;
import com.waminiyi.go4lunch.util.SnapshotListener;

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

    public void setCurrentUserLunch(Lunch lunch, Restaurant restaurant) {
        lunchRepository.setCurrentUserLunch(lunch,restaurant);
    }

    public void deleteCurrentUserLunch(String userId, String restaurantId) {
        lunchRepository.deleteCurrentUserLunch(userId,restaurantId);
    }

    public LiveData<Lunch> getCurrentUserLunch() {
        return lunchRepository.getCurrentUserLunch();
    }

    public void retrieveAllUsers(){
        lunchRepository.retrieveAllUsers();
    }

    public void updateLunches() {
        lunchRepository.updateLunches();
    }

    public LiveData<List<Lunch>> getUsersLunches() {
        return lunchRepository.getAllUsersLunches();
    }

    public  void getCurrentRestaurantLunchesFromDb(String restaurantId) {
        lunchRepository.getCurrentRestaurantLunchesFromDb(restaurantId);
    }

    public LiveData<List<Lunch>> getCurrentRestaurantLunches(){
        return lunchRepository.getCurrentRestaurantLunches();
    }

    public void setListener(SnapshotListener listener) {
        lunchRepository.setListener(listener);
    }
}
