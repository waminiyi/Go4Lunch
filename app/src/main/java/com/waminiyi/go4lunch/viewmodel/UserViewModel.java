package com.waminiyi.go4lunch.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseUser;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.repository.UserRepository;

public class UserViewModel extends ViewModel {
    private final UserRepository mUserRepository;

    public UserViewModel() {
        mUserRepository = UserRepository.getInstance();
    }

    public FirebaseUser getCurrentUser() {
        return mUserRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return mUserRepository.isCurrentUserLogged();
    }

    public void createNewUser(@NonNull FirebaseUser user) {
        mUserRepository.createNewUser(user);
    }

    public MutableLiveData<UserEntity> getCurrentUserData() {

        return mUserRepository.getCurrentUserData();
    }

    public void logOut(){
        mUserRepository.logOut();
    }
}

