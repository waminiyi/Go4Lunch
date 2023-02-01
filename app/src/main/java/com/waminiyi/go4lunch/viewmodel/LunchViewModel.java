package com.waminiyi.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.model.User;
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

    public void setCurrentUserLunch(Lunch lunch, Restaurant restaurant) {
        lunchRepository.setCurrentUserLunch(lunch,restaurant);
    }

    public void deleteCurrentUserLunch(String userId, String restaurantId) {
        lunchRepository.deleteCurrentUserLunch(userId,restaurantId);
    }

    public LiveData<Lunch> getCurrentUserLunch() {
        return lunchRepository.getCurrentUserLunch();
    }

    public void updateUsersList(){
        lunchRepository.updateUsersList();
    }

    public void updateUsersWithLunches() {
        lunchRepository.updateUsersWithLunches();
    }

    public LiveData<List<User>> getUsersLunches() {
        return lunchRepository.getUsersLunches();
    }

    public LiveData<List<User>> getRestaurantLunches(String restaurantId) {
        return lunchRepository.getRestaurantLunches(restaurantId);
    }

}
