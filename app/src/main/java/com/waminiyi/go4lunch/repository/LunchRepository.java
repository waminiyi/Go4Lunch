package com.waminiyi.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

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

    public void retrieveAllUsers() {

        firebaseHelper.retrieveAllUsersFromDb().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> userMap = documentSnapshot.getData();
            List<Lunch> lunches = new ArrayList<>();
            if (userMap != null) {
                for (Map.Entry<String, Object> entry : userMap.entrySet()) {
                    User user = documentSnapshot.get(entry.getKey(), User.class);
                    if (user != null) {
                        Lunch lunch = new Lunch(user.getuId(), user.getUserName(),
                                user.getUrlPicture(), null, null);
                        lunchesMap.put(user.getuId(), lunch);
                        lunches.add(lunch);
                        userLunchesList.postValue(lunches);

                    }
                }
                getLunchesFromDb();
                getCurrentUserLunchFromDb();
            }
        });
    }

    public void getLunchesFromDb() {

        List<Lunch> lunches = new ArrayList<>();

        firebaseHelper.getLunches().addOnSuccessListener(lunchDocSnapshot -> {
            for (Map.Entry<String, Lunch> entry : lunchesMap.entrySet()) {

                Lunch lunch = lunchDocSnapshot.get(entry.getKey(), Lunch.class);
                if (lunch == null) {
                    lunch = entry.getValue();
                    lunch.setRestaurantId(null);
                    lunch.setRestaurantName(null);
                }

//                if (entry.getKey().equals(firebaseHelper.getCurrentUserUID())) {
//                    currentUserLunch.postValue(lunch);
//                }

                lunches.add(lunch);
                lunchesMap.put(entry.getKey(), lunch);
                userLunchesList.postValue(lunches);

            }
        });
    }

    public void getCurrentUserLunchFromDb() {

        firebaseHelper.getLunches().addOnSuccessListener(lunchDocSnapshot -> {
            Lunch lunch =
                    lunchDocSnapshot.get(Objects.requireNonNull(firebaseHelper.getCurrentUserUID()), Lunch.class);
            currentUserLunch.postValue(lunch);

    });
}


    public LiveData<List<Lunch>> getAllUsersLunches() {
        return userLunchesList;
    }

    public void getCurrentRestaurantLunchesFromDb(String restaurantId) {

        firebaseHelper.getLunches().addOnSuccessListener(documentSnapshot -> {
            Map<String, Object> lunchesMap = documentSnapshot.getData();
            if (lunchesMap != null) {

                List<Lunch> lunchesList = new ArrayList<>();
                for (Map.Entry<String, Object> entry : lunchesMap.entrySet()) {
                    Lunch lunch = documentSnapshot.get(entry.getKey(), Lunch.class);
                    if (lunch != null && lunch.getRestaurantId().equals(restaurantId)) {
                        lunchesList.add(lunch);
                        restaurantLunches.postValue(lunchesList);

                    }
                }
                restaurantLunches.postValue(lunchesList);
            }
        });
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

}
