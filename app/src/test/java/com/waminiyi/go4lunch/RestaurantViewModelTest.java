package com.waminiyi.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.repository.RestaurantRepository;
import com.waminiyi.go4lunch.util.FilterMethod;
import com.waminiyi.go4lunch.util.SortMethod;
import com.waminiyi.go4lunch.viewmodel.RestaurantViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;
import java.util.Objects;

@RunWith(JUnit4.class)
public class RestaurantViewModelTest {

    private final RestaurantRepository mockedRepo = mock(RestaurantRepository.class);
    private final RestaurantViewModel restaurantVM = new RestaurantViewModel(mockedRepo);
    private final TestDataUtil mTestDataUtil = new TestDataUtil();
    private final DocumentSnapshot mockedDoc = mock(DocumentSnapshot.class);

    @Rule
    public InstantTaskExecutorRule taskRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {

        when(mockedRepo.getRestaurantLiveList()).thenReturn(new MutableLiveData<>(mTestDataUtil.getFakeRestaurants()));
        when(mockedRepo.getCurrentLocation()).thenReturn(new MutableLiveData<>(new LatLng(1, 1)));
        when(mockedRepo.getRadius()).thenReturn(500);
        when(mockedRepo.getNextPageToken()).thenReturn("token");

    }

    @Test
    public void getRestaurantLiveList() {
        List<Restaurant> restaurants = restaurantVM.getRestaurantLiveList().getValue();

        assertEquals(4, Objects.requireNonNull(restaurants).size());
        assertEquals("c", restaurants.get(2).getId());
    }

    @Test
    public void updateCurrentLocation() {
        restaurantVM.updateCurrentLocation(10, 10);
        verify(mockedRepo).updateCurrentLocation(10, 10);
    }

    @Test
    public void getCurrentLocation() {
        assertEquals(mockedRepo.getCurrentLocation(), restaurantVM.getCurrentLocation());
    }

    @Test
    public void updateSearchRadius() {
        restaurantVM.updateSearchRadius(50);
        verify(mockedRepo).updateSearchRadius(50);
    }

    @Test
    public void fetchNearbyRestaurants() {
        restaurantVM.fetchNearbyRestaurants();
        verify(mockedRepo).fetchNearbyRestaurants();
    }

    @Test
    public void loadNextSearchResultPage() {
        restaurantVM.loadNextSearchResultPage();
        verify(mockedRepo).loadNextSearchResultPage();
    }

    @Test
    public void getNextPageToken() {
        assertEquals("token", restaurantVM.getNextPageToken());
    }

    @Test
    public void updateRestaurantsWithRating() {
        restaurantVM.updateRestaurantsWithRating(mockedDoc);
        verify(mockedRepo).updateRestaurantsWithRating(mockedDoc);
    }

    @Test
    public void updateRestaurantsWithLunchesCount() {
        restaurantVM.updateRestaurantsWithLunchesCount(mockedDoc);
        verify(mockedRepo).updateRestaurantsWithLunchesCount(mockedDoc);
    }

    @Test
    public void updateRestaurantsWithFavorites() {
        restaurantVM.updateRestaurantsWithFavorites(mockedDoc);
        verify(mockedRepo).updateRestaurantsWithFavorites(mockedDoc);
    }

    @Test
    public void updateSortingMethod() {
        restaurantVM.updateSortingMethod(SortMethod.NEAREST);
        verify(mockedRepo).updateSortingMethod(SortMethod.NEAREST);
    }

    @Test
    public void updateFilteringMethod() {
        restaurantVM.updateFilteringMethod(FilterMethod.OPEN);
        verify(mockedRepo).updateFilteringMethod(FilterMethod.OPEN);
    }

    @Test
    public void clearFilteringMethod() {
        restaurantVM.clearFilteringMethod();
        verify(mockedRepo).clearFilteringMethod();
    }

}
