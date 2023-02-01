package com.waminiyi.go4lunch.viewmodel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.repository.UserRepository;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UserViewModel extends ViewModel {
    private final UserRepository userRepository;

    @Inject
    public UserViewModel(UserRepository userRepository) {
        this.userRepository=userRepository;
    }

    public FirebaseUser getCurrentUser() {
        return userRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return userRepository.isCurrentUserLogged();
    }

    public void createNewUser(@NonNull FirebaseUser user) {
        userRepository.createNewUser(user);
    }

    public LiveData<UserEntity> getCurrentUserData() {

        return userRepository.getCurrentUserData();
    }

    public void logOut(){
        userRepository.logOut();
    }

    @Nullable
    public String getCurrentUserUID() {
        return userRepository.getCurrentUserUID();
    }
}

