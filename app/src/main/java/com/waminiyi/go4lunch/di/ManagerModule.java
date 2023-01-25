package com.waminiyi.go4lunch.di;

import android.content.Context;

import com.waminiyi.go4lunch.manager.LocationPreferenceManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class ManagerModule {

//    @Provides
//    @Singleton
//    public LocationPreferenceManager provideLocationPrefManager(@ApplicationContext Context context){
//        return new LocationPreferenceManager(context);
//    }
}
