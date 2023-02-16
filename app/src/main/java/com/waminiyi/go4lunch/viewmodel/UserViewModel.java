package com.waminiyi.go4lunch.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;

    @Inject
    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return userRepository.isCurrentUserLogged();
    }

    public void createNewUserInDatabase(@NonNull FirebaseUser user) {
        userRepository.createNewUserInDatabase(user);
    }

    public LiveData<UserEntity> getCurrentUserData() {

        return userRepository.getCurrentUserData();
    }

    public void logOut() {
        userRepository.logOut();
    }

    public void getCurrentUserDataFromDatabase() {
        userRepository.getCurrentUserDataFromDatabase();
    }

    @Nullable
    public String getCurrentUserUID() {
        return userRepository.getCurrentUserUID();
    }

    public void setUserListener(FirebaseHelper.UserListener listener) {
        userRepository.setUserListener(listener);
    }

    public void listenToCurrentUserDoc() {
        userRepository.listenToCurrentUserDoc();
    }

    public void listenToUsersSnippet() {
        userRepository.listenToUsersSnippet();
    }

    public void addRestaurantToUserFavorite(String restaurantId) {
        userRepository.addRestaurantToUserFavorite(restaurantId);
    }

    public void removeRestaurantFromUserFavorite(String restaurantId) {
        userRepository.removeRestaurantFromUserFavorite(restaurantId);
    }
}

