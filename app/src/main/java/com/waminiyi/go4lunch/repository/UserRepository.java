package com.waminiyi.go4lunch.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.User;
import com.waminiyi.go4lunch.model.UserEntity;

import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

public class UserRepository {

    private final FirebaseHelper firebaseHelper;
    public MutableLiveData<UserEntity> currentUserEntity = new MutableLiveData<>();

    @Inject
    public UserRepository(FirebaseHelper firebaseHelper) {
        this.firebaseHelper = firebaseHelper;
    }


    public FirebaseUser getCurrentUser() {
        return firebaseHelper.getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    public void createNewUser(@NonNull FirebaseUser user) {
        firebaseHelper.createNewUser(user);
    }

    public LiveData<UserEntity> getCurrentUserData() {
        firebaseHelper.getCurrentUserDoc().addOnSuccessListener(documentSnapshot -> {
            UserEntity userEntity = documentSnapshot.toObject(UserEntity.class);
            currentUserEntity.postValue(userEntity);
        }).addOnFailureListener(e -> currentUserEntity.postValue(null));

        return currentUserEntity;
    }

    public void logOut() {
        firebaseHelper.logOut();
    }

    @Nullable
    public String getCurrentUserUID() {
        return firebaseHelper.getCurrentUserUID();
    }

}
