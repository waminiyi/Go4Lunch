package com.waminiyi.go4lunch.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkRequest;

import androidx.annotation.NonNull;

import com.waminiyi.go4lunch.manager.NetworkStateManager;

import javax.inject.Inject;

import dagger.hilt.android.qualifiers.ApplicationContext;

public class NetworkMonitoringUtil extends ConnectivityManager.NetworkCallback {

    private final NetworkRequest mNetworkRequest;
    private final ConnectivityManager mConnectivityManager;

    public NetworkStateManager mNetworkStateManager;

    @Inject
    public NetworkMonitoringUtil(@ApplicationContext Context context) {
        this.mNetworkRequest = new NetworkRequest.Builder()
                .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .build();

        this.mConnectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        mNetworkStateManager = new NetworkStateManager();
        checkNetworkState();
    }

    public void registerNetworkCallbackEvents() {
        mConnectivityManager.requestNetwork(mNetworkRequest, this);
    }

    @Override
    public void onAvailable(@NonNull Network network) {
        super.onAvailable(network);
        mNetworkStateManager.setNetworkConnectivityStatus(true);
    }

    @Override
    public void onLost(@NonNull Network network) {
        super.onLost(network);
        mNetworkStateManager.setNetworkConnectivityStatus(false);
    }


    @SuppressLint("MissingPermission")
    public void checkNetworkState() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Network currentNetwork = mConnectivityManager.getActiveNetwork();
                NetworkCapabilities caps =
                        mConnectivityManager.getNetworkCapabilities(currentNetwork);

                if (caps == null
                        || !caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    mNetworkStateManager.setNetworkConnectivityStatus(false);
                }
            } else {
                NetworkInfo activeNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo == null || !activeNetworkInfo.isConnected()) {
                    mNetworkStateManager.setNetworkConnectivityStatus(false);
                }
            }

        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
