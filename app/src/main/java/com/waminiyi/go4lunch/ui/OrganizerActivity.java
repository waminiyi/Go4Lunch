package com.waminiyi.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.databinding.ActivityOrganizerBinding;
import com.waminiyi.go4lunch.manager.GoNotificationManager;
import com.waminiyi.go4lunch.manager.LocationPermissionObserver;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrganizerActivity extends AppCompatActivity implements LocationPermissionObserver.PermissionListener {

    private UserViewModel mUserViewModel;
    private LocationPermissionObserver permissionManager;
    private Intent mainIntent;
    private Intent signInIntent;
    private ActivityOrganizerBinding binding;
    @Inject
    GoNotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrganizerBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mNotificationManager.createLunchNotificationChannel();

        mainIntent = new Intent(OrganizerActivity.this, MainActivity.class);
        signInIntent = new Intent(OrganizerActivity.this, LoginActivity.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

    }

    @Override
    public void onStart() {
        super.onStart();
        Handler handler = new Handler();
        handler.postDelayed(() -> {

            if (mUserViewModel.isCurrentUserLogged()) {
                launchMainActivity();

            } else {
                launchLoginActivity();
            }

        }, 3000);
    }

    @Override
    public void onLocationPermissionGranted() {
        launchMainActivity();
    }

    @Override
    public void onLocationPermissionDenied() {
        Toast.makeText(this, getString(R.string.authorization_denied_message), Toast.LENGTH_LONG).show();
        Handler handler = new Handler();
        handler.postDelayed(this::finish, 3000);

    }

    private void launchMainActivity() {
        startActivity(mainIntent);
        finish();
    }

    private void launchLoginActivity() {
        startActivity(signInIntent);
        finish();
    }

}