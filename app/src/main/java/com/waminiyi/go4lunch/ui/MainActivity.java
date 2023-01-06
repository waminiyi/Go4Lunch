package com.waminiyi.go4lunch.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;


import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private NavController mNavController;
    private BottomNavigationView mBottomNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private UserViewModel mUserViewModel;
    private UserEntity mCurrentUserEntity;
    TextView navUsernameTV;
    TextView navUserMailTV;


    public MainActivity() {
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        this.configureViews();
        this.setUpNavigation();
        this.updateUI();
    }

    private void configureViews() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation_view);
        mNavController = Navigation.findNavController(this, R.id.main_frame_layout);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
        View headerView = mNavigationView.getHeaderView(0);
        navUsernameTV = (TextView) headerView.findViewById(R.id.drawer_username_textview);
        navUserMailTV = (TextView) headerView.findViewById(R.id.drawer_user_mail);
    }

    private void setUpNavigation() {
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.navigation_map_view, R.id.navigation_list_view, R.id.navigation_workmates).setDrawerLayout(mDrawerLayout)
                        .build();
        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.nav_drawer_open, R.string.nav_drawer_close);

        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        mNavigationView.setNavigationItemSelectedListener(this);

        NavigationUI.setupWithNavController(mBottomNavigationView, mNavController);

        NavigationUI.setupActionBarWithNavController(this, mNavController, appBarConfiguration);
    }

    private void updateUI() {
        getCurrentUserData();
    }

    private void updateNavDrawerWithUserData() {
        String username = TextUtils.isEmpty(mCurrentUserEntity.getUserName()) ? getString(R.string.username_not_found) : mCurrentUserEntity.getUserName();
        String userMail = TextUtils.isEmpty(mCurrentUserEntity.getUserEmail()) ? getString(R.string.usermail_not_found) : mCurrentUserEntity.getUserEmail();
        navUsernameTV.setText(username);
        navUserMailTV.setText(userMail);
    }

    private void getCurrentUserData() {
        mUserViewModel.getCurrentUserData().observe(this, userEntity -> {
            setCurrentUserEntity(userEntity);
            updateNavDrawerWithUserData();
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_your_lunch:
                NavigationUI.onNavDestinationSelected(item, mNavController);
                break;
            case R.id.navigation_settings:
                navigateToSettings();
                break;
            case R.id.navigation_logout:
                this.logOut();
                break;
        }
        this.mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (this.mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void setCurrentUserEntity(UserEntity currentUserEntity) {
        mCurrentUserEntity = currentUserEntity;
    }

    private void logOut() {
        mUserViewModel.logOut();
        Intent logInIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(logInIntent);
        finish();
    }

    private void navigateToSettings() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }
}