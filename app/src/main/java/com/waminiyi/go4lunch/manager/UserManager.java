package com.waminiyi.go4lunch.manager;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.waminiyi.go4lunch.model.User;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.model.UsersSnippet;
import com.waminiyi.go4lunch.repository.UserRepository;

import java.util.List;

public class UserManager {
    private static volatile UserManager instance;
    private UserRepository userRepository;

    private UserManager() {
        userRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public FirebaseUser getCurrentUser(){
        return userRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }
    public void createUser(){
        userRepository.createNewUser();
    }

    public Task<UserEntity> getUserData(){
        return userRepository.getUserData().continueWith(task -> task.getResult().toObject(UserEntity.class)) ;
    }

//    public Task<List<User>> getUsersSnippet(){
//
//        return userRepository.getAllUserDataSnippet().continueWith(task -> task.getResult().toObject(UsersSnippet.class).all) ;
//    }
//
//    public Task<Void> updateUsername(String username){
//        return userRepository.updateUsername(username);
//    }

}
