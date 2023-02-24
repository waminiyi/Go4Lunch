package com.waminiyi.go4lunch.manager;

import android.os.Looper;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;

public class NetworkStateManager {

    private static final MutableLiveData<Boolean> hasConnectedNetwork = new MutableLiveData<>();

    @Inject
    public NetworkStateManager() {}

    /**
     * Updates the active network status live-data
     */
    public void setNetworkConnectivityStatus(boolean connectivityStatus) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            hasConnectedNetwork.setValue(connectivityStatus);
        } else {
            hasConnectedNetwork.postValue(connectivityStatus);
        }
    }

    /**
     * Returns the current network status
     */
    public LiveData<Boolean> getNetworkConnectivityStatus() {
        return hasConnectedNetwork;
    }

}
