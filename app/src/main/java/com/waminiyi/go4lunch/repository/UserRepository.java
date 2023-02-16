package com.waminiyi.go4lunch.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
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
        this.firebaseHelper = firebaseHelper;
    }


    public FirebaseUser getCurrentUser() {
        return firebaseHelper.getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    public void createNewUserInDatabase(@NonNull FirebaseUser user) {
        firebaseHelper.createNewUserInDatabase(user);
    }

    public LiveData<UserEntity> getCurrentUserData() {
        return currentUserEntity;
    }

    public void getCurrentUserDataFromDatabase() {
        firebaseHelper.getCurrentUserDoc().addOnSuccessListener(documentSnapshot -> {
            UserEntity userEntity = documentSnapshot.toObject(UserEntity.class);
            currentUserEntity.postValue(userEntity);
        }).addOnFailureListener(e -> currentUserEntity.postValue(null));//TODo: handle failure
    }


    public void logOut() {
        firebaseHelper.logOut();
    }

    @Nullable
    public String getCurrentUserUID() {
        return firebaseHelper.getCurrentUserUID();
    }

    public void setUserListener(FirebaseHelper.UserListener listener) {
        firebaseHelper.setUserListener(listener);
    }

    public void listenToCurrentUserDoc() {
        firebaseHelper.listenToCurrentUserDoc();
    }

    public void listenToUsersSnippet() {
        firebaseHelper.listenToUsersSnippet();
    }


    public void addRestaurantToUserFavorite(String restaurantId) {
        firebaseHelper.addRestaurantToUserFavorite(restaurantId);
    }

    public void removeRestaurantFromUserFavorite(String restaurantId) {
        firebaseHelper.removeRestaurantFromUserFavorite(restaurantId);
    }

}
