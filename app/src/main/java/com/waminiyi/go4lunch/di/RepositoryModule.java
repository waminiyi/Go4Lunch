package com.waminiyi.go4lunch.di;

import com.waminiyi.go4lunch.helper.FirebaseHelper;
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

}
