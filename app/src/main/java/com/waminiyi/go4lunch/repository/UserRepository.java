package com.waminiyi.go4lunch.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.UserEntity;

import javax.inject.Inject;

public class UserRepository {

    private final FirebaseHelper firebaseHelper;
    public MutableLiveData<UserEntity> currentUserEntity = new MutableLiveData<>();

    @Inject
    public UserRepository(FirebaseHelper firebaseHelper) {
       this.firebaseHelper=firebaseHelper;
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

    public MutableLiveData<UserEntity> getCurrentUserData() {
        firebaseHelper.getCurrentUserData().addOnSuccessListener(documentSnapshot -> {

            UserEntity userEntity = documentSnapshot.toObject(UserEntity.class);
            currentUserEntity.postValue(userEntity);
        }).addOnFailureListener(e -> currentUserEntity.postValue(null));

        return currentUserEntity;
    }

    public void logOut() {
        firebaseHelper.logOut();
    }
}
