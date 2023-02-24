package com.waminiyi.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.repository.RestaurantRepository;
import com.waminiyi.go4lunch.util.FilterMethod;
import com.waminiyi.go4lunch.util.SortMethod;

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

    public void updateCurrentLocation(double latitude, double longitude) {
        restaurantRepository.updateCurrentLocation(latitude, longitude);
    }

    public double getLatitude() {
        return restaurantRepository.getLatitude();
    }

    public  LiveData <LatLng>  getCurrentLocation() {
        return restaurantRepository.getCurrentLocation();
    }

    public void updateSearchRadius(int radius) {
        restaurantRepository.updateSearchRadius(radius);
    }

    public void updateRestaurantsWithPlaces(String keyword) {

        restaurantRepository.updateRestaurantsWithPlaces(keyword);
    }

    public void updateRestaurantsWithPlaces() {
        restaurantRepository.updateRestaurantsWithPlaces();
    }

    public void loadNextSearchResultPage() {
        restaurantRepository.loadNextSearchResultPage();
    }

    public String getNextPageToken() {
        return restaurantRepository.getNextPageToken();
    }

    public void updateRestaurantsWithRating(DocumentSnapshot ratingDoc) {
        restaurantRepository.updateRestaurantsWithRating(ratingDoc);
    }

    public void updateRestaurantsWithLunchesCount(DocumentSnapshot lunchCountDoc) {
        restaurantRepository.updateRestaurantsWithLunchesCount(lunchCountDoc);
    }

    public Restaurant getRestaurantById(String restaurantId) {
        return restaurantRepository.getRestaurantById(restaurantId);
    }

    public void updateRestaurantsWithFavorites(DocumentSnapshot userDoc) {
        restaurantRepository.updateRestaurantsWithFavorites(userDoc);
    }

    public void updateSortingMethod(SortMethod sortMethod) {
        restaurantRepository.updateSortingMethod(sortMethod);
    }

    public void updateFilteringMethod(FilterMethod filterMethod) {
        restaurantRepository.updateFilteringMethod(filterMethod);
    }

    public void clearFilteringMethod() {
        restaurantRepository.clearFilteringMethod();
    }

}
