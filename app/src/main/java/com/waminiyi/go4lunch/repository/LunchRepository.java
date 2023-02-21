package com.waminiyi.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

public class LunchRepository {
    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<List<Lunch>> restaurantLunches = new MutableLiveData<>();
    private final MutableLiveData<List<Lunch>> userLunchesList = new MutableLiveData<>();
    private final MutableLiveData<Lunch> currentUserLunch = new MutableLiveData<>();
    private final Map<String, Lunch> lunchesMap = new HashMap<>();

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

        Map<String, Object> userMap = userSnippetDoc.getData();
        List<Lunch> lunches = new ArrayList<>();
        if (userMap != null) {
            for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                User user = userSnippetDoc.get(entry.getKey(), User.class);
                if (user != null) {
                    Lunch lunch = new Lunch(user.getuId(), user.getUserName(),
                            user.getUrlPicture(), null, null);
                    lunchesMap.put(user.getuId(), lunch);
                    lunches.add(lunch);
                    userLunchesList.postValue(lunches);

                }
            }
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

        List<Lunch> lunches = new ArrayList<>();

        for (Map.Entry<String, Lunch> entry : lunchesMap.entrySet()) {

            Lunch lunch = lunchDoc.get(entry.getKey(), Lunch.class);
            if (lunch == null) {
                lunch = entry.getValue();
                lunch.setRestaurantId(null);
                lunch.setRestaurantName(null);
            }

            lunches.add(lunch);
            lunchesMap.put(entry.getKey(), lunch);
            userLunchesList.postValue(lunches);
        }
    }

    public LiveData<List<Lunch>> getAllUsersLunches() {
        return userLunchesList;
    }

    public void getCurrentRestaurantLunchesFromDb(String restaurantId, DocumentSnapshot lunchDoc) {

        Map<String, Object> lunchesMap = lunchDoc.getData();
        List<Lunch> lunchesList = new ArrayList<>();
        if (lunchesMap != null) {

            for (Map.Entry<String, Object> entry : lunchesMap.entrySet()) {
                Lunch lunch = lunchDoc.get(entry.getKey(), Lunch.class);
                if (lunch != null && lunch.getRestaurantId().equals(restaurantId)) {
                    lunchesList.add(lunch);
                    restaurantLunches.postValue(lunchesList);
                }
            }
        }
        restaurantLunches.postValue(lunchesList);
    }

    public LiveData<List<Lunch>> getCurrentRestaurantLunches() {
        return restaurantLunches;
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
