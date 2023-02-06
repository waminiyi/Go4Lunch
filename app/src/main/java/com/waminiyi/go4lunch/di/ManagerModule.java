package com.waminiyi.go4lunch.di;

import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class ManagerModule {

//    @Provides
//    @Singleton
//    public PreferenceManager provideLocationPrefManager(@ApplicationContext Context context){
//        return new PreferenceManager(context);
//    }
}
