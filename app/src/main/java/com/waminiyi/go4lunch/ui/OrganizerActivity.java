package com.waminiyi.go4lunch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.waminiyi.go4lunch.R;

public class OrganizerActivity extends AppCompatActivity {

    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_organizer);
        FirebaseAuth auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();
    }

    @Override
    public void onStart() {
        super.onStart();
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent mainIntent = new Intent(OrganizerActivity.this, MainActivity.class);
            Intent signInIntent = new Intent(OrganizerActivity.this, LoginActivity.class);
            if (currentUser == null) {
                startActivity(signInIntent);
            } else {
                startActivity(mainIntent);
            }

        }, 3000);
    }
}