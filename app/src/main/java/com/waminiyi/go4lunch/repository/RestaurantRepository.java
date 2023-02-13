package com.waminiyi.go4lunch.repository;

import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.waminiyi.go4lunch.api.NearbyPlaceApi;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.NearbyPlaceSearchResponse;
import com.waminiyi.go4lunch.model.NearbyPlaceSearchResult;
import com.waminiyi.go4lunch.model.Rating;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.util.SnapshotListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestaurantRepository {

    private final NearbyPlaceApi nearbyPlaceApi;
    private final MutableLiveData<List<Restaurant>> restaurantLiveList = new MutableLiveData<>();
    private List<Restaurant> restaurantList = new ArrayList<>();
    private final Map<String, Restaurant> restaurantMap = new HashMap<>();
    private double currentLatitude;
    private double currentLongitude;
    private final String RESTAURANT = "restaurant";
    private String RESTAURANT_TYPE;
    private String nextPageToken;

    private final FirebaseHelper firebaseHelper;

    @Inject
    public RestaurantRepository(NearbyPlaceApi nearbyPlaceApi, FirebaseHelper firebaseHelper) {
        this.nearbyPlaceApi = nearbyPlaceApi;
        this.firebaseHelper = firebaseHelper;
    }

    public void setListener(SnapshotListener listener) {
        firebaseHelper.setListener(listener);
    }

    public void listenToLunches() {
        firebaseHelper.listenToLunches();
    }

    public void listenToRatings() {
        firebaseHelper.listenToRatings();
    }

    public void listenToCurrentUserDoc() {
        firebaseHelper.listenToCurrentUserDoc();
    }

    public void listenToUsersSnippet() {
        firebaseHelper.listenToUsersSnippet();
    }

    /**
     * return a LiveData of the restaurant list fetch by the API and update with rankings and lunches
     *
     * @return LiveData<List < Restaurant>>
     */
    public LiveData<List<Restaurant>> getRestaurantLiveList() {
        return restaurantLiveList;
    }

    public void updateRestaurantsWithRating() {

        List<Restaurant> updatedList = new ArrayList<>();
        if (restaurantList != null) {
            firebaseHelper.getRestaurantNotes().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {

                    for (Map.Entry<String, Restaurant> entry : restaurantMap.entrySet()) {
                        Restaurant restaurant = entry.getValue();
                        Rating rating = task.getResult().get(restaurant.getId(), Rating.class);

                        if (rating != null && rating.getRatingCount() != 0) {
                            float r =
                                    ((float) (rating.getRatingSum()) / (float) (rating.getRatingCount()));

                            restaurant.setRating(new BigDecimal(r).setScale(1,
                                    RoundingMode.HALF_UP).floatValue());
                            restaurantMap.put(entry.getKey(), restaurant);
                        }

                        updatedList.add(restaurant);
                        restaurantLiveList.postValue(updatedList);
                    }
                }
            });
        }
    }

    /**
     * fetch the nearby restaurants with the specified restoration type
     *
     * @param keyword:restoration type
     * @param latitude:           current location's longitude
     * @param longitude:          current location's latitude
     * @param radius:             search range radius
     * @param apiKey:google       places api key
     */

    public void updateRestaurantsWithPlaces(String keyword, double latitude, double longitude,
                                            Integer radius, String apiKey) {

        RESTAURANT_TYPE = keyword;
        currentLatitude = latitude;
        currentLongitude = longitude;
        String location = currentLatitude + "," + currentLongitude;
        nextPageToken = null;
        restaurantList = new ArrayList<>();

        nearbyPlaceApi.getNearbyPlaces(RESTAURANT_TYPE, location, radius, RESTAURANT, apiKey).enqueue(new Callback<NearbyPlaceSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyPlaceSearchResponse> call, @NonNull Response<NearbyPlaceSearchResponse> response) {

                if (response.body() != null) {
                    Log.d("nearbyplaces0", String.valueOf(response.body().results[0]));
                    NearbyPlaceSearchResult[] placesSearchResults = response.body().results;
                    if (response.body().nextPageToken != null) {
                        nextPageToken = response.body().nextPageToken;
                    }
                    parsePlaceSearchResults(placesSearchResults);

                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbyPlaceSearchResponse> call, @NonNull Throwable t) {
                restaurantLiveList.setValue(restaurantLiveList.getValue());//TODO : handle this
            }
        });

    }

    /**
     * fetch the nearby restaurants
     *
     * @param latitude:     current location's longitude
     * @param longitude:    current location's latitude
     * @param radius:       search range radius
     * @param apiKey:google places api key
     */
    public void updateRestaurantsWithPlaces(double latitude, double longitude,
                                            Integer radius, String apiKey) {
        RESTAURANT_TYPE = null;
        currentLatitude = latitude;
        currentLongitude = longitude;
        String location = currentLatitude + "," + currentLongitude;
        nextPageToken = null;
        restaurantList = new ArrayList<>();

        nearbyPlaceApi.getNearbyPlaces(location, radius, RESTAURANT, apiKey).enqueue(new Callback<NearbyPlaceSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyPlaceSearchResponse> call, @NonNull Response<NearbyPlaceSearchResponse> response) {

                if (response.body() != null) {
                    Log.d("nearbyplaces", Arrays.toString(response.body().results));
                    NearbyPlaceSearchResult[] placesSearchResults = response.body().results;
                    if (response.body().nextPageToken != null) {
                        nextPageToken = response.body().nextPageToken;
                    }
                    parsePlaceSearchResults(placesSearchResults);

                }
            }

            @Override
            public void onFailure(@NonNull Call<NearbyPlaceSearchResponse> call, @NonNull Throwable t) {
                restaurantLiveList.setValue(restaurantLiveList.getValue());//TODO : handle this
            }
        });


    }

    /**
     * fetch the nearby restaurants with the nextPageToken
     *
     * @param apiKey : google places api key
     */
    public void updateRestaurantsWithPlaces(String apiKey) {

        nearbyPlaceApi.getNearbyPlaces(nextPageToken, apiKey).enqueue(new Callback<NearbyPlaceSearchResponse>() {
            @Override
            public void onResponse(@NonNull Call<NearbyPlaceSearchResponse> call, @NonNull Response<NearbyPlaceSearchResponse> response) {

                if (response.body() != null) {
                    Log.d("nearbyplaces1", Arrays.toString(response.body().results));
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
                restaurantLiveList.setValue(restaurantLiveList.getValue());//TODO : handle this
            }
        });

    }


    /**
     * parse the NearbyPlaceSearchResult objects to restaurant object
     */
    private void parsePlaceSearchResults(NearbyPlaceSearchResult[] placesSearchResults) {

        for (NearbyPlaceSearchResult place : placesSearchResults) {
            Log.d(place.name, String.valueOf(place.openingHours));
            Restaurant restaurant = new Restaurant();
            float[] distance = new float[1];
            restaurant.setId(place.placeId);

            if (!restaurantList.contains(restaurant)) {
                restaurant.setName(place.name);
                restaurant.setAddress(place.vicinity);
                restaurant.setLatitude(place.geometry.location.lat);
                restaurant.setLongitude(place.geometry.location.lng);

                Location.distanceBetween(currentLatitude, currentLongitude, restaurant.getLatitude(), restaurant.getLongitude(), distance);

                restaurant.setDistance((int) distance[0]);

                if (place.openingHours != null) {

                    if (place.openingHours.openNow != null) {
                        restaurant.setOpenNow(place.openingHours.openNow);
                    }
                }
                if (place.photos != null) {//TODO :replace by ternary operator
                    restaurant.setPhotoReference(place.photos[0].photoReference);
                }

                restaurantList.add(restaurant);
                restaurantMap.put(restaurant.getId(), restaurant);
                restaurantLiveList.postValue(restaurantList);
            }
        }
        updateRestaurantsWithRating();
        updateRestaurantsWithLunches();

    }


    /**
     * return the next page token if available
     */
    public String getNextPageToken() {
        return nextPageToken;
    }

    /**
     * add lunch data to the restaurant list
     */

    public void updateRestaurantsWithLunches() {
        List<Restaurant> updatedList = new ArrayList<>();
        if (restaurantList != null) {
            firebaseHelper.getLunchesCount().addOnSuccessListener(documentSnapshot -> {
                for (Map.Entry<String, Restaurant> entry : restaurantMap.entrySet()) {
                    Restaurant restaurant = entry.getValue();
                    Long count = documentSnapshot.getLong(entry.getKey());

                    if (count != null) {
                        restaurant.setLunchCount(Math.toIntExact(count));

                    } else {
                        restaurant.setLunchCount(0);
                    }
                    restaurantMap.put(entry.getKey(), restaurant);
                    updatedList.add(restaurant);
                    restaurantLiveList.postValue(updatedList);
                }

//                for (Restaurant restaurant : restaurantList) {
//                    Long count = documentSnapshot.getLong(restaurant.getId());
//                    if (count != null) {
//                        restaurant.setLunchCount(Math.toIntExact(count));
//                    }
//                    updatedList.add(restaurant);
//                    restaurantLiveList.postValue(updatedList);
//                }
//                restaurantList = updatedList;
            });
        }
    }

    public Restaurant getUserLunchRestaurant(String restaurantId) {
        return restaurantMap.get(restaurantId);
    }
}
