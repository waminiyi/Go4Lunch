package com.waminiyi.go4lunch.manager;


import static com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.CancellationTokenSource;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;

public class LocationManager {
    private final FragmentActivity activity;
    private final FusedLocationProviderClient fusedLocationProviderClient;

    public LocationManager(FragmentActivity activity) {
        this.activity=activity;
        this.fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(activity);

    }

    public void getCurrentLocation() {
        @SuppressLint("MissingPermission") Task<Location> task =
                fusedLocationProviderClient.getCurrentLocation(PRIORITY_HIGH_ACCURACY, new CancellationToken() {
                    @NonNull
                    @Override
                    public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                        return new CancellationTokenSource().getToken();
                    }

                    @Override
                    public boolean isCancellationRequested() {
                        return false;
                    }
                });
        task.addOnSuccessListener(location -> {
            if (location != null) {
                ((LocationListener) activity).onLocationFetched(location);
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ((LocationListener) activity).onLocationError(e);
            }
        });
    }

    public void getLastLocation() {

        @SuppressLint("MissingPermission") Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(location -> {
            if (location != null) {
                ((LocationListener) activity).onLocationFetched(location);
            }

        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ((LocationListener) activity).onLocationError(e);
            }
        });

    }

    public interface LocationListener {
        void onLocationFetched(Location location);
        void onLocationError(Exception e);
    }
}
