package com.waminiyi.go4lunch.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.waminiyi.go4lunch.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }
}