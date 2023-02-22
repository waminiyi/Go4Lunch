package com.waminiyi.go4lunch.di;

import com.waminiyi.go4lunch.api.NearbyPlaceApi;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.repository.LunchRepository;
import com.waminiyi.go4lunch.repository.RestaurantRepository;
import com.waminiyi.go4lunch.repository.ReviewRepository;
import com.waminiyi.go4lunch.repository.StateRepository;
import com.waminiyi.go4lunch.repository.UserRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class RepositoryModule {

    @Provides
    @Singleton
    public UserRepository provideUserRepository(FirebaseHelper firebaseHelper) {
        return new UserRepository(firebaseHelper);
    }

    @Provides
    @Singleton
    public RestaurantRepository provideRestaurantRepository(NearbyPlaceApi nearbyPlaceApi, FirebaseHelper firebaseHelper) {
        return new RestaurantRepository(nearbyPlaceApi, firebaseHelper);
    }

    @Provides
    @Singleton
    public LunchRepository provideLunchRepository(FirebaseHelper firebaseHelper) {
        return new LunchRepository(firebaseHelper);
    }

    @Provides
    @Singleton
    public ReviewRepository provideReviewRepository(FirebaseHelper firebaseHelper) {
        return new ReviewRepository(firebaseHelper);
    }

    @Provides
    @Singleton
    public StateRepository provideStateRepository() {
        return new StateRepository();
    }

}
