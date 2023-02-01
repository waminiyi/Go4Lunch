package com.waminiyi.go4lunch.model;

import com.google.firebase.firestore.PropertyName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserMap {
    public Map<String, User> allUsers;

    public List<User> toUsers(){
        return new ArrayList<>(allUsers.values());
    }

    @PropertyName("Group")
    public Map<String, User> getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(Map<String, User> allUsers) {
        this.allUsers = allUsers;
    }
}
