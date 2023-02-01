package com.waminiyi.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

public class LunchRepository {
    private final FirebaseHelper firebaseHelper;
    private List<String> usersId;
    //    private UserMap usersMap;
    private final MutableLiveData<List<User>> restaurantLunches = new MutableLiveData<>();
    private final MutableLiveData<List<User>> userLunches = new MutableLiveData<>();
    private final MutableLiveData<Lunch> currentUserLunch = new MutableLiveData<>();

    @Inject
    public LunchRepository(FirebaseHelper firebaseHelper) {
        this.firebaseHelper = firebaseHelper;
    }

    public void setCurrentUserLunch(Lunch lunch, Restaurant restaurant) {
        firebaseHelper.setCurrentUserLunch(lunch, restaurant);
    }

    public void deleteCurrentUserLunch(String userId, String restaurantId) {
        firebaseHelper.deleteCurrentUserLunch(userId,restaurantId);
    }

    public LiveData<Lunch> getCurrentUserLunch() {
        firebaseHelper.getLunches().addOnSuccessListener(documentSnapshot ->
                currentUserLunch.postValue(documentSnapshot.get(Objects.requireNonNull(firebaseHelper.getCurrentUserUID()), Lunch.class)));

        return currentUserLunch;
    }

    public void updateUsersList() {

        firebaseHelper.retrieveAllUsersFromDb().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> userMap = documentSnapshot.getData();
            List<User> users = new ArrayList<>();
            if (userMap != null) {
                for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                    User user = documentSnapshot.get(entry.getKey(), User.class);
                    if (user != null) {
                        users.add(user);
                        userLunches.postValue(users);
                    }
                }
                updateUsersWithLunches();
            }
        });
    }

    public void updateUsersWithLunches() {
        List<User> users = userLunches.getValue();
        if (users != null) {
            List<User> lunches = new ArrayList<>();
            firebaseHelper.getLunches().addOnSuccessListener(lunchDocSnapshot -> {
                for (User user : users) {
                    Lunch lunch = lunchDocSnapshot.get(user.getuId(), Lunch.class);
                    if (lunch != null) {
                        user.setUserLunch(lunch.getRestaurantName());
                    }
                    lunches.add(user);
                    userLunches.postValue(lunches);
                }
            });
        }
    }


    public LiveData<List<User>> getUsersLunches() {
        return userLunches;
    }

    public LiveData<List<User>> getRestaurantLunches(String restaurantId) {

        firebaseHelper.getLunches().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> lunchesMap = documentSnapshot.getData();
            if (lunchesMap != null) {

                firebaseHelper.retrieveAllUsersFromDb().addOnSuccessListener(userDocSnapshot -> {
                    List<User> users = new ArrayList<>();
                    for (Map.Entry<String, Object> entry : lunchesMap.entrySet()) {
                        Lunch lunch = documentSnapshot.get(entry.getKey(), Lunch.class);
                        if (lunch != null && lunch.getRestaurantId().equals(restaurantId)) {
                            User user = userDocSnapshot.get(entry.getKey(), User.class);
                            if (user != null) {
                                users.add(user);
                                restaurantLunches.postValue(users);
                            }
                        }
                    }
                });
            }
        });
        return restaurantLunches;
    }

}
