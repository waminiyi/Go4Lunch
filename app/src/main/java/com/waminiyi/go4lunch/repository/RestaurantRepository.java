package com.waminiyi.go4lunch.repository;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
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
     * MutableLiveData that stores current list of restaurants
     */
    private final MutableLiveData<List<Restaurant>> restaurantLiveList = new MutableLiveData<>();

    /**
     * A HashMap that associate each restaurant object with its unique Id
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

    /**
     * MutableLiveData that stores current user's location
     */
    private final MutableLiveData<LatLng> currentLocation = new MutableLiveData<>();

    /**
     * integer representing the current search radius
     */
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
     * FirebaseHelper instance for handling Firestore database operations
     */
    private final FirebaseHelper firebaseHelper;

    /**
     * Google maps api key
     */
    private final String MAPS_API_KEY = BuildConfig.MAPS_API_KEY;

    /**
     * Restaurant list sorting method
     */
    private SortMethod sortMethod = SortMethod.RATING;

    /**
     * Restaurant list filtering method
     */
    private FilterMethod filterMethod = FilterMethod.NONE;

    @Inject
    public RestaurantRepository(NearbyPlaceApi nearbyPlaceApi, FirebaseHelper firebaseHelper) {
        this.nearbyPlaceApi = nearbyPlaceApi;
        this.firebaseHelper = firebaseHelper;
    }

    /**
     * return a LiveData of the restaurant list fetched by the API and updated with rankings,
     * lunches and favorites
     *
     * @return LiveData<List < Restaurant>>
     */
    public LiveData<List<Restaurant>> getRestaurantLiveList() {
        return restaurantLiveList;
    }


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
     * return the current location
     *
     * @return LiveData<LatLng>
     */
    public LiveData<LatLng> getCurrentLocation() {
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
     * fetch the nearby restaurants
     */
    public void fetchNearbyRestaurants() {
        String location = this.latitude + "," + this.longitude;
        nextPageToken = null;
        restaurantMap.clear();

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
                Log.d("nearbyPlaces", t.getMessage());
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
                Log.d("nearbyPlaces", t.getMessage());
            }
        });
    }

    /**
     * parse the NearbyPlaceSearchResult objects to Restaurant object
     *
     * @param placesSearchResults: array representing the body of the nearby places search result
     */
    private void parsePlaceSearchResults(NearbyPlaceSearchResult[] placesSearchResults) {
        List<Restaurant> updatedList = new ArrayList<>(restaurantMap.values());

        if (placesSearchResults != null && placesSearchResults.length != 0) {
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

            }
            addRatings();
            addLunches();
            addUserFavorites();
        }

    }


    /**
     * return the next page token if available
     *
     * @return String
     */
    public String getNextPageToken() {
        return nextPageToken;
    }


    /**
     * add lunch data to restaurants at initialization
     */
//    private void addLunches() {
//        firebaseHelper.getLunchesCount().addOnSuccessListener(this::updateRestaurantsWithLunchesCount);
//    }

    private void addLunches() {
        firebaseHelper.getLunchesCount().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                updateRestaurantsWithLunchesCount(documentSnapshot);
            }
        });
    }

    /**
     * add rating data to restaurants at initialization
     */
    private void addRatings() {
        firebaseHelper.getRestaurantNotes().addOnSuccessListener(this::updateRestaurantsWithRating);
    }

    /**
     * add favorite data to restaurants at initialization
     */
    private void addUserFavorites() {
        firebaseHelper.getCurrentUserDoc().addOnSuccessListener(this::updateRestaurantsWithFavorites);
    }

    /**
     * update restaurant list with ratings data
     *
     * @param ratingDoc: ratings DocumentSnapshot
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
        }
    }

    /**
     * update restaurant list with lunches data
     *
     * @param lunchCountDoc: lunch count DocumentSnapshot
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
        }
    }


    /**
     * update restaurant list with favorites data
     *
     * @param userDoc: user documentSnapshot
     */
    @SuppressWarnings("unchecked")
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
        }
    }

    /**
     * post the restaurant list in restaurantLiveList
     *
     * @param restaurants: List of restaurant to post
     */
    private void postRestaurantList(List<Restaurant> restaurants) {
        sortRestaurants(restaurants);
        restaurantLiveList.postValue(filterRestaurants(restaurants));
    }

    /**
     * sort restaurant list
     *
     * @param restaurants: List of restaurant to sort
     */
    private void sortRestaurants(List<Restaurant> restaurants) {

        if (restaurants != null) {
            switch (sortMethod) {

                case RATING:
                    Collections.sort(restaurants, new RestaurantComparator.SortByRating());
                    break;
                case NEAREST:
                    Collections.sort(restaurants, new RestaurantComparator.SortByNearest());
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * Filter the list of restaurants passed as argument
     *
     * @param restaurants: List of restaurant to filter
     * @return List<Restaurant> : filtered list
     */
    private List<Restaurant> filterRestaurants(List<Restaurant> restaurants) {
        List<Restaurant> filteredList;

        switch (filterMethod) {

            case FAVORITE:
                filteredList = filterByFavorite(restaurants);
                break;
            case OPEN:
                filteredList = filterByOpening(restaurants);
                break;
            default:
                filteredList = restaurants;

        }
        return filteredList;
    }

    /**
     * Filter the list of restaurants passed as argument by opening
     *
     * @param restaurants: List of restaurant to filter
     * @return List<Restaurant> : filtered list
     */
    private List<Restaurant> filterByOpening(List<Restaurant> restaurants) {
        List<Restaurant> filteredList = new ArrayList<>();

        for (Restaurant restaurant : restaurants) {
            if (restaurant.isOpenNow()) {
                filteredList.add(restaurant);
            }
        }
        return filteredList;
    }

    /**
     * Filter the list of restaurants passed as argument by favorite
     *
     * @param restaurants: List of restaurant to filter
     * @return List<Restaurant> : filtered list
     */
    private List<Restaurant> filterByFavorite(List<Restaurant> restaurants) {
        List<Restaurant> filteredList = new ArrayList<>();

        for (Restaurant restaurant : restaurants) {
            if (restaurant.isUserFavorite()) {
                filteredList.add(restaurant);
            }
        }

        return filteredList;
    }

    /**
     * Update the sorting method
     *
     * @param sortMethod: SortMethod
     */
    public void updateSortingMethod(SortMethod sortMethod) {
        this.sortMethod = sortMethod;
        List<Restaurant> restaurants = restaurantLiveList.getValue();
        sortRestaurants(restaurants);
        restaurantLiveList.postValue(restaurants);
    }

    /**
     * Update the filtering method
     *
     * @param filterMethod: FilterMethod
     */
    public void updateFilteringMethod(FilterMethod filterMethod) {
        this.filterMethod = filterMethod;
        postRestaurantList(new ArrayList<>(restaurantMap.values()));
    }

    /**
     * Reset the filtering method
     */
    public void clearFilteringMethod() {
        this.filterMethod = FilterMethod.NONE;
        this.postRestaurantList(new ArrayList<>(restaurantMap.values()));
    }

    @VisibleForTesting
    public int getRadius() {
        return radius;
    }

    @VisibleForTesting
    public List<Restaurant> filterRestaurantsByFavorite(List<Restaurant> restaurants) {
        return filterByFavorite(restaurants);
    }

    @VisibleForTesting
    public List<Restaurant> filterRestaurantsByOpening(List<Restaurant> restaurants) {
        return filterByOpening(restaurants);
    }

    @VisibleForTesting
    public void setRestaurantLiveList(List<Restaurant> restaurants) {
        for (Restaurant r : restaurants){
            restaurantMap.put(r.getId(),r);
        }
        restaurantLiveList.postValue(restaurants);
    }

    @VisibleForTesting
    public void parsePlaceArray(NearbyPlaceSearchResult[] placesSearchResults) {
        parsePlaceSearchResults(placesSearchResults);
    }

    @VisibleForTesting
    public SortMethod getSortMethod() {
        return this.sortMethod;
    }

    @VisibleForTesting
    public FilterMethod getFilterMethod() {
        return this.filterMethod;
    }

}
