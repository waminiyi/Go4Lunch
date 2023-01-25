package com.waminiyi.go4lunch.ui;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.manager.LocationManager;
import com.waminiyi.go4lunch.manager.LocationPreferenceManager;
import com.waminiyi.go4lunch.manager.PermissionManager;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrganizerActivity extends AppCompatActivity implements PermissionManager.PermissionListener, LocationManager.LocationListener {

    private UserViewModel mUserViewModel;
    @Inject
    LocationPreferenceManager locationPrefManager;
    private PermissionManager permissionManager;
    private  Intent mainIntent ;
    private  Intent signInIntent;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainIntent = new Intent(OrganizerActivity.this, MainActivity.class);
        signInIntent = new Intent(OrganizerActivity.this, LoginActivity.class);
        setContentView(R.layout.activity_organizer);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        permissionManager = new PermissionManager();
        locationPrefManager = new LocationPreferenceManager(this);
        permissionManager.registerForPermissionResult(this);
        locationManager = new LocationManager(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        Handler handler = new Handler();
        handler.postDelayed(() -> {

            if (mUserViewModel.isCurrentUserLogged()) {
                if (permissionManager.isPermissionGranted(this)) {
                    launchMainActivity();
                } else {
                    permissionManager.requestPermission();
                }

            } else {
                launchLoginActivity();
            }

        }, 3000);
    }

    @Override
    public void onPermissionGranted() {
        locationManager.getCurrentLocation();
    }

    @Override
    public void onPermissionDenied() {
        finish(); //TODO : handle this
    }

    private void launchMainActivity() {
        startActivity(mainIntent);
        finish();
    }

    private void launchLoginActivity() {
        startActivity(signInIntent);
        finish();
    }

    @Override
    public void onLocationFetched(Location location) {
        locationPrefManager.saveLastLocation(location.getLatitude(), location.getLongitude());
        launchMainActivity();
    }

    @Override
    public void onLocationError(Exception e) {

    }
}