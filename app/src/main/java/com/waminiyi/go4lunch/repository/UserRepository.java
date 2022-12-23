package com.waminiyi.go4lunch.repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseUser;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.UserEntity;

public class UserRepository {
    private static volatile UserRepository instance;
    private final FirebaseHelper mFirebaseHelper;
    public MutableLiveData<UserEntity> currentUserEntity = new MutableLiveData<>();

    private UserRepository() {
        mFirebaseHelper = FirebaseHelper.getInstance();
    }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    public FirebaseUser getCurrentUser() {
        return mFirebaseHelper.getCurrentUser();
    }

    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    public void createNewUser() {
        mFirebaseHelper.createNewUser();
    }

    public MutableLiveData<UserEntity> getCurrentUserData() {
        mFirebaseHelper.getCurrentUserData().addOnSuccessListener(documentSnapshot -> {

            UserEntity userEntity = documentSnapshot.toObject(UserEntity.class);
            currentUserEntity.postValue(userEntity);
        }).addOnFailureListener(e -> currentUserEntity.postValue(null));

        return currentUserEntity;
    }

    public void logOut() {
        mFirebaseHelper.logOut();
    }
}
