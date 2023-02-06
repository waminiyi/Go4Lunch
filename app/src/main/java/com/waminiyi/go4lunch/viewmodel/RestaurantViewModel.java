package com.waminiyi.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.repository.RestaurantRepository;
import com.waminiyi.go4lunch.util.SnapshotListener;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class RestaurantViewModel extends ViewModel {

    private final RestaurantRepository restaurantRepository;

    @Inject
    public RestaurantViewModel(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public LiveData<List<Restaurant>> getRestaurantLiveList() {
        return restaurantRepository.getRestaurantLiveList();
    }


    public void updateRestaurantsWithPlaces(String keyword, double latitude, double longitude,
                                            Integer radius, String apiKey) {

        restaurantRepository.updateRestaurantsWithPlaces(keyword, latitude, longitude, radius, apiKey);
    }

    public void updateRestaurantsWithPlaces(double latitude, double longitude,
                                            Integer radius, String apiKey) {
        restaurantRepository.updateRestaurantsWithPlaces(latitude, longitude, radius, apiKey);
    }

    public void updateRestaurantsWithPlaces(String apiKey) {
        restaurantRepository.updateRestaurantsWithPlaces(apiKey);
    }

    public String getNextPageToken() {
        return restaurantRepository.getNextPageToken();
    }

    public void updateRestaurantsWithRating() {
        restaurantRepository.updateRestaurantsWithRating();
    }

    public void updateRestaurantsWithLunches() {
        restaurantRepository.updateRestaurantsWithLunches();
    }

    public Restaurant getUserLunchRestaurant(String restaurantId) {
        return restaurantRepository.getUserLunchRestaurant(restaurantId);
    }


    public void listenToLunches() {
        restaurantRepository.listenToLunches();
    }

    public void listenToRatings() {
        restaurantRepository.listenToRatings();
    }

    public void listenToCurrentUserDoc() {
        restaurantRepository.listenToCurrentUserDoc();
    }

    public void listenToUsersSnippet() {
        restaurantRepository.listenToUsersSnippet();
    }

    public void setListener(SnapshotListener listener) {
        restaurantRepository.setListener(listener);
    }
}
