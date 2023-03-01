package com.waminiyi.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.User;
import com.waminiyi.go4lunch.model.UserLunch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

public class LunchRepository {

    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<List<User>> restaurantUserLunches = new MutableLiveData<>();
    private final MutableLiveData<List<UserLunch>> userLunchesList = new MutableLiveData<>();
    private final MutableLiveData<Lunch> currentUserLunch = new MutableLiveData<>();
    private final Map<String, UserLunch> usersLunchesMap = new HashMap<>();


    @Inject
    public LunchRepository(FirebaseHelper firebaseHelper) {
        this.firebaseHelper = firebaseHelper;
    }

    public void setCurrentUserLunch(Lunch lunch) {
        firebaseHelper.setCurrentUserLunch(lunch);
    }

    public void deleteCurrentUserLunch(Lunch lunch) {
        firebaseHelper.deleteCurrentUserLunch(lunch);
    }

    public LiveData<Lunch> getCurrentUserLunch() {
        return currentUserLunch;
    }

    public void parseUsersSnippetDoc(DocumentSnapshot userSnippetDoc) {
        usersLunchesMap.clear();

        Map<String, Object> userMap = userSnippetDoc.getData();
        List<UserLunch> userLunches = new ArrayList<>();
        if (userMap != null) {
            for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                User user = userSnippetDoc.get(entry.getKey(), User.class);
                if (user != null) {
                    UserLunch uLunch = new UserLunch(user.getUserId(), user.getUserName(),
                            user.getUserPictureUrl(), null, null);
                    usersLunchesMap.put(user.getUserId(), uLunch);
                    userLunches.add(uLunch);
                    userLunchesList.postValue(userLunches);//TODO: remove and handle map==null
                }
            }
            userLunchesList.postValue(userLunches);
            addLunches();
        }

    }

    private void addLunches() {

        firebaseHelper.getLunches().addOnSuccessListener(this::parseLunchesDoc);
    }

    public void parseLunchesDoc(DocumentSnapshot lunchDoc) {

        Lunch userLunch =
                lunchDoc.get(Objects.requireNonNull(firebaseHelper.getCurrentUserUID()), Lunch.class);
        currentUserLunch.postValue(userLunch);

        List<UserLunch> userLunches = new ArrayList<>();

        for (Map.Entry<String, UserLunch> entry : usersLunchesMap.entrySet()) {

            Lunch lunch = lunchDoc.get(entry.getKey(), Lunch.class);
            UserLunch uLunch=entry.getValue();
            if (lunch == null) {
                uLunch.setRestaurantId(null);
                uLunch.setRestaurantName(null);
            }else{
                uLunch.setRestaurantId(lunch.getRestaurantId());
                uLunch.setRestaurantName(lunch.getRestaurantName());
            }

            userLunches.add(uLunch);
            usersLunchesMap.put(entry.getKey(), uLunch);
            userLunchesList.postValue(userLunches);
        }
    }

    public LiveData<List<UserLunch>> getAllUsersLunches() {
        return userLunchesList;
    }

    public void getCurrentRestaurantLunchesFromDb(String restaurantId) {

        List<User> usersList = new ArrayList<>();

        for (Map.Entry<String, UserLunch> entry : usersLunchesMap.entrySet()) {
            UserLunch userLunch=usersLunchesMap.get(entry.getKey());
            if (userLunch != null && userLunch.getRestaurantId()!=null && userLunch.getRestaurantId().equals(restaurantId)) {
                User user = new User(userLunch.getUserId(), userLunch.getUserName(),
                        userLunch.getUserPictureUrl());

                usersList.add(user);
                restaurantUserLunches.postValue(usersList);
            }
        }
        restaurantUserLunches.postValue(usersList);
    }

    public LiveData<List<User>> getCurrentRestaurantLunches() {
        return restaurantUserLunches;
    }

    public void setLunchListener(FirebaseHelper.LunchListener listener) {
        firebaseHelper.setLunchListener(listener);
    }

    public void listenToLunches() {
        firebaseHelper.listenToLunches();
    }

    public void listenToLunchesCount() {
        firebaseHelper.listenToLunchesCount();
    }

}
