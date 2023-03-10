package com.waminiyi.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.api.NearbyPlaceApi;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.NearbyPlaceSearchResponse;
import com.waminiyi.go4lunch.model.Rating;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.repository.RestaurantRepository;
import com.waminiyi.go4lunch.util.FilterMethod;
import com.waminiyi.go4lunch.util.SortMethod;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@RunWith(JUnit4.class)
public class RestaurantRepositoryTest {

    private final NearbyPlaceApi mockedApi = mock(NearbyPlaceApi.class);
    private final FirebaseHelper mockedHelper = mock(FirebaseHelper.class);
    @SuppressWarnings("unchecked")
    private final Call<NearbyPlaceSearchResponse> mockedCall = mock(Call.class);
    @SuppressWarnings("unchecked")
    private final Call<NearbyPlaceSearchResponse> nextPageMockedCall = mock(Call.class);

    private final TestDataUtil mTestDataUtil = new TestDataUtil();
    private final RestaurantRepository restaurantRepo =
            new RestaurantRepository(mockedApi, mockedHelper);

    @Rule
    public InstantTaskExecutorRule taskRule = new InstantTaskExecutorRule();

    @SuppressWarnings("unchecked")
    @Before
    public void setUp() {

        // Mocking necessary object
        mTestDataUtil.buildMockedResponse();

        mTestDataUtil.setUpHelper(mockedHelper);
        when(mockedApi.getNearbyPlaces(any(), any(), any(), any())).thenReturn(mockedCall);
        when(mockedApi.getNearbyPlaces(any(), any())).thenReturn(nextPageMockedCall);

        doAnswer(invocation -> {
            Callback<NearbyPlaceSearchResponse> callback = invocation.getArgument(0,
                    Callback.class);
            callback.onResponse(mockedCall, Response.success(mTestDataUtil.fakeResponse));
            callback.onFailure(mockedCall, new Throwable());
            return null;
        }).when(mockedCall).enqueue(any(Callback.class));

        doAnswer(invocation -> {
            Callback<NearbyPlaceSearchResponse> callback = invocation.getArgument(0,
                    Callback.class);
            callback.onResponse(nextPageMockedCall, Response.success(mTestDataUtil.fakeNextPageResponse));
            callback.onFailure(nextPageMockedCall, new Throwable());
            return null;
        }).when(nextPageMockedCall).enqueue(any(Callback.class));

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
     * Verify that the findNearbyRestaurants method makes the right API call and that the list of
     * restaurant returned by getRestaurantLiveList correspond to the fetched restaurants
     */
    @SuppressWarnings("unchecked")
    @Test
    public void findNearbyRestaurantsTest() {

        restaurantRepo.fetchNearbyRestaurants();
        verify(mockedApi).getNearbyPlaces(any(), any(), any(), any());
        verify(mockedCall).enqueue(any(Callback.class));

        List<Restaurant> restaurants = restaurantRepo.getRestaurantLiveList().getValue();

        assertEquals(4, Objects.requireNonNull(restaurants).size());

        assertEquals("b", restaurants.get(0).getId());

        assertEquals(3, restaurants.get(0).getRating(), 0);
        assertEquals(3, restaurants.get(0).getLunchCount());
        assertFalse(restaurants.get(0).isOpenNow());
        assertFalse(restaurants.get(0).isUserFavorite());

        assertNotNull(restaurantRepo.getNextPageToken());

    }


    /**
     * Verify that the loadNextSearchResultPage method makes the right API call and that the list of
     * restaurant returned by getRestaurantLiveList includes the result of newly fetched
     * restaurant list
     */
    @SuppressWarnings("unchecked")
    @Test
    public void loadNextSearchResultPageTest() {
        restaurantRepo.loadNextSearchResultPage();
        verify(mockedApi).getNearbyPlaces(any(), any());
        verify(nextPageMockedCall).enqueue(any(Callback.class));

        List<Restaurant> restaurants = restaurantRepo.getRestaurantLiveList().getValue();
        assertEquals(1, Objects.requireNonNull(restaurants).size());

        assertEquals("e", restaurants.get(0).getId());

        assertEquals(0, restaurants.get(0).getRating(), 0);
        assertEquals(0, restaurants.get(0).getLunchCount());
        assertTrue(restaurants.get(0).isOpenNow());
        assertTrue(restaurants.get(0).isUserFavorite());

    }

    /**
     * Test that the updateSearchRadius method updates the current search radius
     */
    @Test
    public void updateSearchRadiusTest() {
        int radius = restaurantRepo.getRadius();
        assertEquals(0, radius);

        restaurantRepo.updateSearchRadius(5000);
        assertEquals(5000, restaurantRepo.getRadius());
    }


    /**
     * Verify that the parsePlaceSearchResults parse the provided PlaceSearchResult into a list of
     * Restaurant
     */
    @Test
    public void parsePlaceSearchResultsTest() {
        List<Restaurant> restaurants = restaurantRepo.getRestaurantLiveList().getValue();
        assertNull(restaurants);

        restaurantRepo.parsePlaceArray(mTestDataUtil.results);
        restaurants = restaurantRepo.getRestaurantLiveList().getValue();
        assertNotNull(restaurants);

        assertEquals(4, restaurants.size());

        assertEquals("b", restaurants.get(0).getId());

        assertEquals(3, restaurants.get(0).getRating(), 0);
        assertEquals(3, restaurants.get(0).getLunchCount());
        assertFalse(restaurants.get(0).isOpenNow());
        assertFalse(restaurants.get(0).isUserFavorite());
    }

    /**
     * Verify that the updateRestaurantsWithRatings method updates ratings data for the current
     * list of Restaurant
     */
    @Test
    public void updateRestaurantsWithRatingTest() {
        DocumentSnapshot mockedDoc = mock(DocumentSnapshot.class);
        when(mockedDoc.get("a", Rating.class)).thenReturn(new Rating(6, 3));
        when(mockedDoc.get("b", Rating.class)).thenReturn(new Rating(6, 2));
        restaurantRepo.setRestaurantLiveList(mTestDataUtil.getFakeRestaurants());

        List<Restaurant> restaurants = restaurantRepo.getRestaurantLiveList().getValue();

        //the list is unsorted, restaurant with id "a" is the first element
        assertEquals("a", Objects.requireNonNull(restaurants).get(0).getId());
        assertEquals(2.2f, restaurants.get(0).getRating(), 0);

        restaurantRepo.updateRestaurantsWithRating(mockedDoc);
        //when updating, the list is sorted, default sorting method is rating, the restaurant
        // with id "a" should have moved to index 1 and the rating should have change from 2.2 to 2
        restaurants = restaurantRepo.getRestaurantLiveList().getValue();
        assertEquals("a", restaurants.get(1).getId());
        assertEquals(2f, restaurants.get(1).getRating(), 0);
    }

    /**
     * Verify that the updateRestaurantsWithLunchesCount method updates lunch count data for the
     * current list of Restaurant
     */
    @Test
    public void updateRestaurantsWithLunchesCountTest() {
        DocumentSnapshot mockedDoc = mock(DocumentSnapshot.class);
        when(mockedDoc.getLong("a")).thenReturn(26L);
        when(mockedDoc.getLong("b")).thenReturn(12L);
        restaurantRepo.setRestaurantLiveList(mTestDataUtil.getFakeRestaurants());

        List<Restaurant> restaurants = restaurantRepo.getRestaurantLiveList().getValue();

        //the list is unsorted, restaurant with id "a" is the first element
        assertEquals("a", Objects.requireNonNull(restaurants).get(0).getId());
        assertEquals(25, restaurants.get(0).getLunchCount(), 0);

        restaurantRepo.updateRestaurantsWithLunchesCount(mockedDoc);
        //when updating, the list is sorted, default sorting method is rating, the restaurant
        // with id "a" should have moved to index 2 and the lunchCount should have change from 25
        // to 26
        restaurants = restaurantRepo.getRestaurantLiveList().getValue();
        assertEquals("a", restaurants.get(2).getId());
        assertEquals(26, restaurants.get(2).getLunchCount(), 0);

    }

    /**
     * Verify that the updateRestaurantsWithFavorites method updates user's favorites data for the
     * current list of Restaurant
     */
    @Test
    public void updateRestaurantsWithFavoritesTest() {
        DocumentSnapshot mockedDoc = mock(DocumentSnapshot.class);
        when(mockedDoc.get("favoriteRestaurant")).thenReturn(Arrays.asList("a", "b"));
        restaurantRepo.setRestaurantLiveList(mTestDataUtil.getFakeRestaurants());

        List<Restaurant> restaurants = restaurantRepo.getRestaurantLiveList().getValue();

        //the list is unsorted, restaurant with id "a" is the first element
        assertEquals("a", Objects.requireNonNull(restaurants).get(0).getId());
        assertEquals("b", restaurants.get(1).getId());
        assertTrue(restaurants.get(0).isUserFavorite());
        assertFalse(restaurants.get(1).isUserFavorite());

        restaurantRepo.updateRestaurantsWithFavorites(mockedDoc);
        //when updating, the list is sorted, default sorting method is rating, the restaurant
        // with id "a" should have moved to index 2
        restaurants = restaurantRepo.getRestaurantLiveList().getValue();
        assertEquals("a", restaurants.get(2).getId());
        assertEquals("b", restaurants.get(1).getId());
        assertTrue(restaurants.get(2).isUserFavorite());
        assertTrue(restaurants.get(1).isUserFavorite());

    }


    /**
     * Verify that the filterByFavorite method filters the current list of Restaurant  and return
     * only restaurants that are the user's favorites
     */
    @Test
    public void filterByFavoriteTest() {
        List<Restaurant> restaurants = mTestDataUtil.getFakeRestaurants();
        assertEquals(4, restaurants.size());

        restaurants = restaurantRepo.filterRestaurantsByFavorite(restaurants);
        assertEquals(2, restaurants.size());
        for (Restaurant r : restaurants) {
            assertTrue(r.isUserFavorite());
        }

    }

    /**
     * Verify that the filterByOpening method filters the current list of Restaurant  and return
     * only restaurants that are open now
     */
    @Test
    public void filterByOpeningTest() {
        List<Restaurant> restaurants = mTestDataUtil.getFakeRestaurants();
        assertEquals(4, restaurants.size());

        restaurants = restaurantRepo.filterRestaurantsByOpening(restaurants);
        assertEquals(3, restaurants.size());
        for (Restaurant r : restaurants) {
            assertTrue(r.isOpenNow());
        }

    }

    /**
     * Verify that the updateSortingMethod method update the current sorting method and that the
     *  list of Restaurant  is well sorted
     */
    @Test
    public void updateSortingMethodTest() {

        //we add some data here, the list will not be sorted because the setRestaurantLiveList
        // method is only visible for testing and just add insert data
        restaurantRepo.setRestaurantLiveList(mTestDataUtil.getFakeRestaurants());

        assertEquals(SortMethod.RATING, restaurantRepo.getSortMethod()); //the default sorting

        restaurantRepo.updateSortingMethod(SortMethod.NEAREST);
        assertEquals(SortMethod.NEAREST, restaurantRepo.getSortMethod());
        List<Restaurant> restaurants = restaurantRepo.getRestaurantLiveList().getValue();
        for (int i = 0; i <= Objects.requireNonNull(restaurants).size() - 2; i++) {
            assertTrue(restaurants.get(i).getDistance() <= restaurants.get(i + 1).getDistance());
        }
        restaurantRepo.updateSortingMethod(SortMethod.RATING);
        assertEquals(SortMethod.RATING, restaurantRepo.getSortMethod());
        restaurants = restaurantRepo.getRestaurantLiveList().getValue();
        for (int i = 0; i <= restaurants.size() - 2; i++) {
            assertTrue(restaurants.get(i).getRating() >= restaurants.get(i + 1).getRating());
        }
    }

    /**
     * Verify that the updateFilteringMethod method update the current filtering method and that
     * the list of Restaurant  is well filtered
     */
    @Test
    public void updateFilteringMethodTest() {
        restaurantRepo.setRestaurantLiveList(mTestDataUtil.getFakeRestaurants());
        restaurantRepo.updateFilteringMethod(FilterMethod.FAVORITE);
        assertEquals(FilterMethod.FAVORITE, restaurantRepo.getFilterMethod());

        List<Restaurant> restaurants = restaurantRepo.getRestaurantLiveList().getValue();
        assertEquals(2, Objects.requireNonNull(restaurants).size());
        for (Restaurant r : restaurants) {
            assertTrue(r.isUserFavorite());
        }

        restaurantRepo.updateFilteringMethod(FilterMethod.OPEN);
        assertEquals(FilterMethod.OPEN, restaurantRepo.getFilterMethod());

        restaurants = restaurantRepo.getRestaurantLiveList().getValue();
        assertEquals(3, restaurants.size());
        for (Restaurant r : restaurants) {
            assertTrue(r.isOpenNow());
        }

        restaurantRepo.clearFilteringMethod();
        restaurants = restaurantRepo.getRestaurantLiveList().getValue();
        assertEquals(FilterMethod.NONE, restaurantRepo.getFilterMethod());
        assertEquals(4, restaurants.size());
    }

    @Test
    public void nearbyPlaceApiTest() throws InterruptedException {
        Retrofit mRetrofit = new Retrofit.Builder()
                .baseUrl(NearbyPlaceApi.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        NearbyPlaceApi api = mRetrofit.create(NearbyPlaceApi.class);

        RestaurantRepository repository=new RestaurantRepository(api,mockedHelper);

        repository.updateCurrentLocation(45.192459,5.697220 );
        repository.updateSearchRadius(100);

        List<Restaurant> restaurants= repository.getRestaurantLiveList().getValue();
        assertNull(restaurants);

        repository.fetchNearbyRestaurants();

        Thread.sleep(5000);

        restaurants= repository.getRestaurantLiveList().getValue();
        assertNotNull(restaurants);
        assertEquals(4, restaurants.size());
        assertEquals(mTestDataUtil.realId2,restaurants.get(0).getId() );

    }

}
