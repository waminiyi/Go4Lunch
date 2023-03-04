package com.waminiyi.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.model.Geometry;
import com.waminiyi.go4lunch.api.NearbyPlaceApi;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.NearbyPlaceSearchResponse;
import com.waminiyi.go4lunch.model.NearbyPlaceSearchResult;
import com.waminiyi.go4lunch.repository.RestaurantRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@RunWith(JUnit4.class)
public class RestaurantRepositoryTest {

    NearbyPlaceApi mockedApi = mock(NearbyPlaceApi.class);
    FirebaseHelper mockedHelper = mock(FirebaseHelper.class);
    Call<NearbyPlaceSearchResponse> mockedCall = mock(Call.class);
    NearbyPlaceSearchResponse mockedResponse = mock(NearbyPlaceSearchResponse.class);
    NearbyPlaceSearchResult result = mock(NearbyPlaceSearchResult.class);
    NearbyPlaceSearchResult result2 = mock(NearbyPlaceSearchResult.class);
    NearbyPlaceSearchResult result3 = mock(NearbyPlaceSearchResult.class);
    Geometry mockedGeometry= mock(Geometry.class);
    Geometry mockedGeometry2= mock(Geometry.class);
    Geometry mockedGeometry3= mock(Geometry.class);

    RestaurantRepository restaurantRepo = new RestaurantRepository(mockedApi, mockedHelper);

    @Rule
    public InstantTaskExecutorRule taskRule = new InstantTaskExecutorRule();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {

        when(mockedApi.getNearbyPlaces(any(), any(), any(), any())).thenReturn(mockedCall);
        when(mockedApi.getNearbyPlaces(any(), any())).thenReturn(mockedCall);
        when(mockedApi.getNearbyPlaces(any(), any(), any(), any(), any())).thenReturn(mockedCall);

        doAnswer(invocation -> {
            Callback<NearbyPlaceSearchResponse> callback = invocation.getArgument(0,
                    Callback.class);
            callback.onResponse(mockedCall, Response.success(mockedResponse));
            callback.onFailure(mockedCall, new Throwable());
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));

    }

    /**
     * Test that the updateCurrentLocation method updates the current latitude and longitude
     * and that the getCurrentLocation method returns the updated value
     */
    @Test
    public void updateCurrentLocation() {
        LatLng currentLocation = restaurantRepo.getCurrentLocation().getValue();
        assertNull(currentLocation);

        restaurantRepo.updateCurrentLocation(10, 5);
        currentLocation = restaurantRepo.getCurrentLocation().getValue();
        assertNotNull(currentLocation);
        assertEquals(10, currentLocation.latitude, 0);
        assertEquals(5, currentLocation.longitude, 0);

    }

    /**
     * Test that the updateRestaurantsWithPlaces method makes the right API call
     */

    @Test
    public void updateRestaurantsWithPlacesTest() {
        restaurantRepo.updateRestaurantsWithPlaces("Indian");
        verify(mockedApi).getNearbyPlaces(eq("Indian"), any(), any(), any(), any());
        verify(mockedCall).enqueue(any(Callback.class));

    }
    @Test
    public void updateRestaurantsWithPlacesUsingKeywordTest() {
        restaurantRepo.updateRestaurantsWithPlaces("Indian");
        verify(mockedApi).getNearbyPlaces(eq("Indian"), any(), any(), any(), any());
        verify(mockedCall).enqueue(any(Callback.class));

    }

    /**
     * Test that the loadNextSearchResultPage method makes the right API call
     */
    @SuppressWarnings("unchecked")
    @Test
    public void loadNextSearchResultPageTest() {
        restaurantRepo.loadNextSearchResultPage();
        verify(mockedApi).getNearbyPlaces(any(), any());
        verify(mockedCall).enqueue(any(Callback.class));

    }


    /**
     //     * post the restaurant list in restaurantLiveList
     //     *
     //     * @param restaurants: List of restaurant to post
     //     */
//    private void postRestaurantList(List<Restaurant> restaurants) {
//        sortRestaurants(restaurants);
//        restaurantLiveList.postValue(filterRestaurants(restaurants));
//    }
//
//    /**
//     * sort restaurant list
//     *
//     * @param restaurants: List of restaurant to sort
//     */
//    private void sortRestaurants(List<Restaurant> restaurants) {
//        switch (sortMethod) {
//            case NONE:
//                break;
//            case RATING:
//                Collections.sort(restaurants, new RestaurantComparator.SortByRating());
//                break;
//            case NEAREST:
//                Collections.sort(restaurants, new RestaurantComparator.SortByNearest());
//                break;
//        }
//    }
//
//    /**
//     * Filter the list of restaurants passed as argument
//     *
//     * @param restaurants: List of restaurant to filter
//     * @return List<Restaurant> : filtered list
//     */
//    private List<Restaurant> filterRestaurants(List<Restaurant> restaurants) {
//        List<Restaurant> filteredList;
//
//        switch (filterMethod) {
//            case NONE:
//                filteredList = restaurants;
//                break;
//            case FAVORITE:
//                filteredList = filterByFavorite(restaurants);
//                break;
//            case OPEN:
//                filteredList = filterByOpening(restaurants);
//                break;
//            default:
//                return restaurants;
//        }
//        return filteredList;
//    }
//
//    /**
//     * Filter the list of restaurants passed as argument by opening
//     *
//     * @param restaurants: List of restaurant to filter
//     * @return List<Restaurant> : filtered list
//     */
//    private List<Restaurant> filterByOpening(List<Restaurant> restaurants) {
//        List<Restaurant> filteredList = new ArrayList<>();
//
//        for (Restaurant restaurant : restaurants) {
//            if (restaurant.isOpenNow()) {
//                filteredList.add(restaurant);
//            }
//        }
//        return filteredList;
//    }
//
//    /**
//     * Filter the list of restaurants passed as argument by favorite
//     *
//     * @param restaurants: List of restaurant to filter
//     * @return List<Restaurant> : filtered list
//     */
//    private List<Restaurant> filterByFavorite(List<Restaurant> restaurants) {
//        List<Restaurant> filteredList = new ArrayList<>();
//
//        for (Restaurant restaurant : restaurants) {
//            if (restaurant.isUserFavorite()) {
//                filteredList.add(restaurant);
//            }
//        }
//
//        return filteredList;
//    }
//
//    /**
//     * Update the sorting method
//     *
//     * @param sortMethod: SortMethod
//     */
//    public void updateSortingMethod(SortMethod sortMethod) {
//        this.sortMethod = sortMethod;
//        List<Restaurant> restaurants = restaurantLiveList.getValue();
//        sortRestaurants(restaurants);
//        restaurantLiveList.postValue(restaurants);
//    }
//
//    /**
//     * Update the filtering method
//     *
//     * @param filterMethod: FilterMethod
//     */
//    public void updateFilteringMethod(FilterMethod filterMethod) {
//        this.filterMethod = filterMethod;
//        restaurantLiveList.postValue(filterRestaurants(new ArrayList<>(restaurantMap.values())));
//    }
//
//    /**
//     * Reset the filtering method
//     */
//    public void clearFilteringMethod() {
//        this.filterMethod = FilterMethod.NONE;
//        restaurantLiveList.postValue(new ArrayList<>(restaurantMap.values()));
//    }

//
    //
//    /**
//     * return a LiveData of the restaurant list fetched by the API and updated with rankings,
//     * lunches and favorites
//     *
//     * @return LiveData<List < Restaurant>>
//     */
//    public LiveData<List<Restaurant>> getRestaurantLiveList() {
//        return restaurantLiveList;
//    }
//
//
//    /**
//     * Update search radius
//     *
//     * @param radius: nearby places search radius
//     */
//    public void updateSearchRadius(int radius) {
//        this.radius = radius;
//    }


//
//
//    /**
//     * parse the NearbyPlaceSearchResult objects to Restaurant object
//     *
//     * @param placesSearchResults: array representing the body of the nearby places search result
//     */
//    private void parsePlaceSearchResults(NearbyPlaceSearchResult[] placesSearchResults) {
//        List<Restaurant> updatedList = new ArrayList<>(restaurantMap.values());
//
//        for (NearbyPlaceSearchResult place : placesSearchResults) {
//            Restaurant restaurant = new Restaurant();
//
//            restaurant.setId(place.placeId);
//            restaurant.setName(place.name);
//            restaurant.setAddress(place.vicinity);
//            restaurant.setLatitude(place.geometry.location.lat);
//            restaurant.setLongitude(place.geometry.location.lng);
//
//            //Calculate the distance between the user's location an the restaurant
//            float[] distance = new float[1];
//            Location.distanceBetween(latitude, longitude, restaurant.getLatitude(), restaurant.getLongitude(), distance);
//            restaurant.setDistance((int) distance[0]);
//
//            restaurant.setOpenNow(place.openingHours != null && place.openingHours.openNow != null ?
//                    place.openingHours.openNow : false);
//
//            restaurant.setPhotoReference(place.photos != null ? place.photos[0].photoReference : null);
//
//            updatedList.add(restaurant);
//            restaurantMap.put(restaurant.getId(), restaurant);
//            postRestaurantList(updatedList);
//
//        }
//        addRatings();
//        addLunches();
//        addUserFavorites();
//
//    }
//
//
//    /**
//     * return the next page token if available
//     *
//     * @return String
//     */
//    public String getNextPageToken() {
//        return nextPageToken;
//    }
//
//
//    /**
//     * add lunch data to restaurants at initialization
//     */
//    private void addLunches() {
//        firebaseHelper.getLunchesCount().addOnSuccessListener(this::updateRestaurantsWithLunchesCount);
//    }
//
//    /**
//     * add rating data to restaurants at initialization
//     */
//    private void addRatings() {
//        firebaseHelper.getRestaurantNotes().addOnSuccessListener(this::updateRestaurantsWithRating);
//    }
//
//    /**
//     * add favorite data to restaurants at initialization
//     */
//    private void addUserFavorites() {
//        firebaseHelper.getCurrentUserDoc().addOnSuccessListener(this::updateRestaurantsWithFavorites);
//    }
//
//    /**
//     * update restaurant list with ratings data
//     *
//     * @param ratingDoc: ratings DocumentSnapshot
//     */
//    public void updateRestaurantsWithRating(DocumentSnapshot ratingDoc) {
//
//        List<Restaurant> updatedList = new ArrayList<>();
//
//        for (Map.Entry<String, Restaurant> entry : restaurantMap.entrySet()) {
//            Restaurant restaurant = entry.getValue();
//            Rating rating = ratingDoc.get(restaurant.getId(), Rating.class);
//
//            if (rating != null && rating.getRatingCount() != 0) {
//                float r =
//                        ((float) (rating.getRatingSum()) / (float) (rating.getRatingCount()));
//
//                restaurant.setRating(new BigDecimal(r).setScale(1,
//                        RoundingMode.HALF_UP).floatValue());
//            } else {
//                restaurant.setRating(0);
//            }
//            restaurantMap.put(entry.getKey(), restaurant);
//            updatedList.add(restaurant);
//            postRestaurantList(updatedList);
//        }
//    }
//
//    /**
//     * update restaurant list with lunches data
//     *
//     * @param lunchCountDoc: lunch count DocumentSnapshot
//     */
//    public void updateRestaurantsWithLunchesCount(DocumentSnapshot lunchCountDoc) {
//        List<Restaurant> updatedList = new ArrayList<>();
//
//        for (Map.Entry<String, Restaurant> entry : restaurantMap.entrySet()) {
//            Restaurant restaurant = entry.getValue();
//            Long count = lunchCountDoc.getLong(entry.getKey());
//
//            if (count != null) {
//                restaurant.setLunchCount(Math.toIntExact(count));
//
//            } else {
//                restaurant.setLunchCount(0);
//            }
//            restaurantMap.put(entry.getKey(), restaurant);
//            updatedList.add(restaurant);
//            postRestaurantList(updatedList);
//        }
//    }
//
//
//    /**
//     * update restaurant list with favorites data
//     *
//     * @param userDoc: user documentSnapshot
//     */
//    @SuppressWarnings("unchecked")
//    public void updateRestaurantsWithFavorites(DocumentSnapshot userDoc) {
//        List<Restaurant> updatedList = new ArrayList<>();
//        List<String> userFav = (List<String>) userDoc.get("favoriteRestaurant");
//        for (Map.Entry<String, Restaurant> entry : restaurantMap.entrySet()) {
//            Restaurant restaurant = entry.getValue();
//
//            if (userFav != null) {
//                restaurant.setUserFavorite(userFav.contains(restaurant.getId()));
//            }
//            restaurantMap.put(entry.getKey(), restaurant);
//            updatedList.add(restaurant);
//            postRestaurantList(updatedList);
//        }
//    }
//
//    /**
//     * return the restaurant which ID is passed as argument
//     *
//     * @param restaurantId: Restaurant's unique ID
//     * @return Restaurant
//     */
//    public Restaurant getRestaurantById(String restaurantId) {
//        return restaurantMap.get(restaurantId);
//    }
//
//
//

}
