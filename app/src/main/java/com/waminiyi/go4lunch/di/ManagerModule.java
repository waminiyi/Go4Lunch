package com.waminiyi.go4lunch.di;

import android.content.Context;

import com.waminiyi.go4lunch.manager.GoNotificationManager;
import com.waminiyi.go4lunch.manager.NetworkStateManager;
import com.waminiyi.go4lunch.util.NetworkMonitoringUtil;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class ManagerModule {

    @Provides
    @Singleton
    public NetworkMonitoringUtil provideNetworkMonitoringUtil(@ApplicationContext Context context){
        return new NetworkMonitoringUtil(context);
    }

    @Provides
    @Singleton
    public NetworkStateManager provideNetworkStateManager(){
        return new NetworkStateManager();
    }

    @Provides
    @Singleton
    public GoNotificationManager provideNotificationManager(@ApplicationContext Context context){
        return new GoNotificationManager(context);
    }

}
