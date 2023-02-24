package com.waminiyi.go4lunch.manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.waminiyi.go4lunch.R;

public class LocationPermissionObserver implements DefaultLifecycleObserver {

    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<String> mRequestLocationPermission;
    private PermissionListener mListener;

    public LocationPermissionObserver(@NonNull ActivityResultRegistry registry) {
        mRegistry = registry;
    }

    public void setListener(PermissionListener listener) {
        mListener = listener;
    }

    public void onCreate(@NonNull LifecycleOwner owner) {
        mRequestLocationPermission = mRegistry.register("locationPermission", owner,
                new ActivityResultContracts.RequestPermission(), isLocationPermissionGranted -> {
                    if (isLocationPermissionGranted) {
                        //if the permission is granted, we get the current location and saved it
                        mListener.onLocationPermissionGranted();
                    } else {
                        mListener.onLocationPermissionDenied();
                    }
                });
    }

    public boolean isPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public boolean shouldRequestPermission(FragmentActivity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void requestPermission() {
        mRequestLocationPermission.launch(
                android.Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void showPermissionPurpose(FragmentActivity activity) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity).setMessage(R.string.permission_purpose)
                        .setTitle(R.string.permission_purpose_dialog_title)
                        .setCancelable(false)
                        .setPositiveButton(R.string.give_permission, (dialog, which) -> requestPermission())
                        .setNegativeButton(R.string.deny, (dialog, which) -> {
                            dialog.dismiss();
                            activity.finish();
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    /**
     * Interface that should be implemented by the activity / fragment that shows
     * call the manager
     */
    public interface PermissionListener {
        void onLocationPermissionGranted();

        void onLocationPermissionDenied();

    }
}
