package com.waminiyi.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class OrganizerActivity extends AppCompatActivity {

    private UserViewModel mUserViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
    }

    @Override
    public void onStart() {
        super.onStart();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent mainIntent = new Intent(OrganizerActivity.this, MainActivity.class);
            Intent signInIntent = new Intent(OrganizerActivity.this, LoginActivity.class);
            if (mUserViewModel.isCurrentUserLogged()) {
                startActivity(mainIntent);
                finish();
            } else {
                startActivity(signInIntent);
                finish();
            }

        }, 3000);
    }
}