package com.waminiyi.go4lunch.repository;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.BuildConfig;
import com.waminiyi.go4lunch.api.NearbyPlaceApi;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.NearbyPlaceSearchResponse;
import com.waminiyi.go4lunch.model.NearbyPlaceSearchResult;
import com.waminiyi.go4lunch.model.Rating;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.util.FilterMethod;
import com.waminiyi.go4lunch.util.RestaurantComparator;
import com.waminiyi.go4lunch.util.SortMethod;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Class that handle nearbyPlace results parsing into Restaurant object; adding lunch, rating
 * and favorites data to Restaurant objects
 */
public class RestaurantRepository {
    /**
     * NearbyPlaceApi instance for fetching nearby restaurants
     */
    private final NearbyPlaceApi nearbyPlaceApi;

    /**
     * MutableLiveData that hold current list of restaurants
     */
    private final MutableLiveData<List<Restaurant>> restaurantLiveList = new MutableLiveData<>();

    /**
     * A map that associate each restaurant object with its unique Id
     */
    private final Map<String, Restaurant> restaurantMap = new HashMap<>();

    /**
     * Current user's location latitude
     */
    private double latitude;

    /**
     * Current user's location longitude
     */
    private double longitude;

    private MutableLiveData<LatLng> currentLocation=new MutableLiveData<>();

    private int radius;

    /**
     * String representing the restaurant keyword
     */
    private final String RESTAURANT = "restaurant";


    /**
     * String representing the next page token returned by the Place API call
     */
    private String nextPageToken;

    /**
     * FirebaseHelper instance for handling firestore database operations
     */
    private final FirebaseHelper firebaseHelper;

    /**
     * Google maps api key
     */
    private final String MAPS_API_KEY = BuildConfig.MAPS_API_KEY;

    @Inject
    public RestaurantRepository(NearbyPlaceApi nearbyPlaceApi, FirebaseHelper firebaseHelper) {
        this.nearbyPlaceApi = nearbyPlaceApi;
        this.firebaseHelper = firebaseHelper;
    }

    /**
     * return a LiveData of the restaurant list fetch by the API and updated with rankings,
     * lunches and favorites
     *
     * @return LiveData<List < Restaurant>>
     */
    public LiveData<List<Restaurant>> getRestaurantLiveList() {
        return restaurantLiveList;
    }


    private SortMethod sortMethod = SortMethod.RATING;
    private FilterMethod filterMethod = FilterMethod.NONE;

    /**
     * Update current latitude and longitude
     *
     * @param latitude:  user's current location latitude
     * @param longitude: user's current location latitude
     */
    public void updateCurrentLocation(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
        currentLocation.postValue(new LatLng(latitude, longitude));
    }

    /**
     * return the current location latitude
     *
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    public LiveData <LatLng> getCurrentLocation() {
        return currentLocation;
    }

    /**
     * Update search radius
     *
     * @param radius: nearby places search radius
     */
    public void updateSearchRadius(int radius) {
        this.radius = radius;
    }

    /**
     * fetch the nearby restaurants with the specified cooking type
     *
     * @param keyword:restoration type
     */

    public void updateRestaurantsWithPlaces(String keyword) {

        String location = this.latitude + "," + this.longitude;
        nextPageToken = null;
        restaurantMap.clear();

        nearbyPlaceApi.getNearbyPlaces(keyword, location, radius, RESTAURANT, MAPS_API_KEY).enqueue(new Callback<NearbyPlaceSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyPlaceSearchResponse> call, @NonNull Response<NearbyPlaceSearchResponse> response) {

                if (response.body() != null) {
                    NearbyPlaceSearchResult[] placesSearchResults = response.body().results;
                    if (response.body().nextPageToken != null) {
                        nextPageToken = response.body().nextPageToken;
                    }
                    parsePlaceSearchResults(placesSearchResults);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbyPlaceSearchResponse> call, @NonNull Throwable t) {
                Log.d("nearbyplaces", t.getMessage());
            }
        });

    }

    /**
     * fetch the nearby restaurants
     */
    public void updateRestaurantsWithPlaces() {
        String location = this.latitude + "," + this.longitude;
        nextPageToken = null;
        restaurantMap.clear();// TODO: necessary??

        nearbyPlaceApi.getNearbyPlaces(location, radius, RESTAURANT, MAPS_API_KEY).enqueue(new Callback<NearbyPlaceSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyPlaceSearchResponse> call, @NonNull Response<NearbyPlaceSearchResponse> response) {
                if (response.body() != null) {
                    NearbyPlaceSearchResult[] placesSearchResults = response.body().results;
                    if (response.body().nextPageToken != null) {
                        nextPageToken = response.body().nextPageToken;
                    }
                    parsePlaceSearchResults(placesSearchResults);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbyPlaceSearchResponse> call, @NonNull Throwable t) {
                Log.d("nearbyplaces", t.getMessage());
            }
        });
    }

    /**
     * fetch the nearby restaurants with the nextPageToken
     */
    public void loadNextSearchResultPage() {

        nearbyPlaceApi.getNearbyPlaces(nextPageToken, MAPS_API_KEY).enqueue(new Callback<NearbyPlaceSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyPlaceSearchResponse> call, @NonNull Response<NearbyPlaceSearchResponse> response) {

                if (response.body() != null) {
                    NearbyPlaceSearchResult[] placesSearchResults = response.body().results;
                    if (response.body().nextPageToken != null) {
                        nextPageToken = response.body().nextPageToken;
                    } else {
                        nextPageToken = null;
                    }
                    parsePlaceSearchResults(placesSearchResults);
                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbyPlaceSearchResponse> call, @NonNull Throwable t) {
                Log.d("nearbyplaces", t.getMessage());
            }
        });
    }


    /**
     * parse the NearbyPlaceSearchResult objects to restaurant object
     *
     * @param placesSearchResults: array representing the body of the nearby places search result
     */
    private void parsePlaceSearchResults(NearbyPlaceSearchResult[] placesSearchResults) {
        List<Restaurant> updatedList = new ArrayList<>(restaurantMap.values());

        for (NearbyPlaceSearchResult place : placesSearchResults) {
            Restaurant restaurant = new Restaurant();

            restaurant.setId(place.placeId);
            restaurant.setName(place.name);
            restaurant.setAddress(place.vicinity);
            restaurant.setLatitude(place.geometry.location.lat);
            restaurant.setLongitude(place.geometry.location.lng);

            //Calculate the distance between the user's location an the restaurant
            float[] distance = new float[1];
            Location.distanceBetween(latitude, longitude, restaurant.getLatitude(), restaurant.getLongitude(), distance);
            restaurant.setDistance((int) distance[0]);

            restaurant.setOpenNow(place.openingHours != null && place.openingHours.openNow != null ?
                    place.openingHours.openNow : false);

            restaurant.setPhotoReference(place.photos != null ? place.photos[0].photoReference : null);

            updatedList.add(restaurant);
            restaurantMap.put(restaurant.getId(), restaurant);
            postRestaurantList(updatedList);
//            restaurantLiveList.postValue(updatedList);

        }
        addRatings();
        addLunches();
        addUserFavorites();

    }


    /**
     * return the next page token if available
     */
    public String getNextPageToken() {
        return nextPageToken;
    }


    private void addLunches() {
        firebaseHelper.getLunchesCount().addOnSuccessListener(this::updateRestaurantsWithLunchesCount);
    }

    private void addRatings() {
        firebaseHelper.getRestaurantNotes().addOnSuccessListener(this::updateRestaurantsWithRating);
    }

    private void addUserFavorites() {
        firebaseHelper.getCurrentUserDoc().addOnSuccessListener(this::updateRestaurantsWithFavorites);
    }

    /**
     * update restaurant list with ratings data
     *
     * @param ratingDoc: ratings documentSnapshot
     */


    public void updateRestaurantsWithRating(DocumentSnapshot ratingDoc) {

        List<Restaurant> updatedList = new ArrayList<>();

        for (Map.Entry<String, Restaurant> entry : restaurantMap.entrySet()) {
            Restaurant restaurant = entry.getValue();
            Rating rating = ratingDoc.get(restaurant.getId(), Rating.class);

            if (rating != null && rating.getRatingCount() != 0) {
                float r =
                        ((float) (rating.getRatingSum()) / (float) (rating.getRatingCount()));

                restaurant.setRating(new BigDecimal(r).setScale(1,
                        RoundingMode.HALF_UP).floatValue());

            } else {
                restaurant.setRating(0);
            }
            restaurantMap.put(entry.getKey(), restaurant);
            updatedList.add(restaurant);
            postRestaurantList(updatedList);
//            restaurantLiveList.postValue(updatedList);
        }
    }

    /**
     * update restaurant list with lunches data
     */

    public void updateRestaurantsWithLunchesCount(DocumentSnapshot lunchCountDoc) {
        List<Restaurant> updatedList = new ArrayList<>();

        for (Map.Entry<String, Restaurant> entry : restaurantMap.entrySet()) {
            Restaurant restaurant = entry.getValue();
            Long count = lunchCountDoc.getLong(entry.getKey());

            if (count != null) {
                restaurant.setLunchCount(Math.toIntExact(count));

            } else {
                restaurant.setLunchCount(0);
            }
            restaurantMap.put(entry.getKey(), restaurant);
            updatedList.add(restaurant);
            postRestaurantList(updatedList);
//            restaurantLiveList.postValue(updatedList);
        }
    }


    /**
     * update restaurant list with favorites data
     *
     * @param userDoc: user documentSnapshot
     */
    public void updateRestaurantsWithFavorites(DocumentSnapshot userDoc) {
        List<Restaurant> updatedList = new ArrayList<>();
        List<String> userFav = (List<String>) userDoc.get("favoriteRestaurant");
        for (Map.Entry<String, Restaurant> entry : restaurantMap.entrySet()) {
            Restaurant restaurant = entry.getValue();

            if (userFav != null) {
                restaurant.setUserFavorite(userFav.contains(restaurant.getId()));
            }
            restaurantMap.put(entry.getKey(), restaurant);
            updatedList.add(restaurant);
            postRestaurantList(updatedList);
//            restaurantLiveList.postValue(updatedList);
        }

    }

    public Restaurant getRestaurantById(String restaurantId) {
        return restaurantMap.get(restaurantId);
    }


    private void postRestaurantList(List<Restaurant> restaurants) {
        sortRestaurants(restaurants);
        restaurantLiveList.postValue(filterRestaurants(restaurants));
    }

    private void sortRestaurants(List<Restaurant> restaurants) {
        switch (sortMethod) {
            case NONE:
                break;
            case RATING:
                Collections.sort(restaurants, new RestaurantComparator.SortByRating());
                break;
            case NEAREST:
                Collections.sort(restaurants, new RestaurantComparator.SortByNearest());
                break;
        }
    }

    private List<Restaurant> filterRestaurants(List<Restaurant> restaurants) {
        List<Restaurant> filteredList;

        switch (filterMethod) {
            case NONE:
                filteredList = restaurants;
                break;
            case FAVORITE:
                filteredList = filterByFavorite(restaurants);
                break;
            case OPEN:
                filteredList = filterByOpening(restaurants);
                break;
            default:
                return restaurants;
        }
        return filteredList;
    }

    private List<Restaurant> filterByOpening(List<Restaurant> restaurants) {
        List<Restaurant> filteredList = new ArrayList<>();

        for (Restaurant restaurant : restaurants) {
            if (restaurant.isOpenNow()) {
                filteredList.add(restaurant);
            }
        }

        return filteredList;
    }

    private List<Restaurant> filterByFavorite(List<Restaurant> restaurants) {
        List<Restaurant> filteredList = new ArrayList<>();

        for (Restaurant restaurant : restaurants) {
            if (restaurant.isUserFavorite()) {
                filteredList.add(restaurant);
            }
        }

        return filteredList;
    }

    public void updateSortingMethod(SortMethod sortMethod) {
        this.sortMethod = sortMethod;
        List<Restaurant> restaurants = restaurantLiveList.getValue();
        sortRestaurants(restaurants);
        restaurantLiveList.postValue(restaurants);
    }

    public void updateFilteringMethod(FilterMethod filterMethod) {
        this.filterMethod = filterMethod;
        restaurantLiveList.postValue(filterRestaurants(new ArrayList<>(restaurantMap.values())));
    }

    public void clearFilteringMethod() {
        this.filterMethod = FilterMethod.NONE;
        restaurantLiveList.postValue(new ArrayList<>(restaurantMap.values()));
    }

    public interface PlaceSearchListener {
        void onPlaceFetched();

        void onPlaceFetchingFailure();
    }
}
