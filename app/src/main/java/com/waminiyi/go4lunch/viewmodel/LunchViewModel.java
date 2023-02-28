package com.waminiyi.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.User;
import com.waminiyi.go4lunch.model.UserLunch;
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


    public LiveData<Lunch> getCurrentUserLunch() {
        return lunchRepository.getCurrentUserLunch();
    }

    public void parseUsersSnippetDoc(DocumentSnapshot userSnippetDoc) {
        lunchRepository.parseUsersSnippetDoc(userSnippetDoc);
    }

    public void parseLunchesDoc(DocumentSnapshot lunchDoc) {
        lunchRepository.parseLunchesDoc(lunchDoc);
    }

    public LiveData<List<UserLunch>> getUsersLunches() {
        return lunchRepository.getAllUsersLunches();
    }

    public void getCurrentRestaurantLunchesFromDb(String restaurantId) {
        lunchRepository.getCurrentRestaurantLunchesFromDb(restaurantId);
    }

    public LiveData<List<User>> getCurrentRestaurantLunches() {
        return lunchRepository.getCurrentRestaurantLunches();
    }

    public void setLunchListener(FirebaseHelper.LunchListener listener) {
        lunchRepository.setLunchListener(listener);
    }

    public void listenToLunches() {
        lunchRepository.listenToLunches();
    }

    public void listenToLunchesCount() {
        lunchRepository.listenToLunchesCount();
    }
}
