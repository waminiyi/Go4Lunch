package com.waminiyi.go4lunch.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.manager.LocationManager;
import com.waminiyi.go4lunch.manager.LocationPreferenceManager;
import com.waminiyi.go4lunch.manager.PermissionManager;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.viewmodel.RestaurantViewModel;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationManager.LocationListener {

    private NavController mNavController;
    private BottomNavigationView mBottomNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private UserViewModel mUserViewModel;
    private UserEntity mCurrentUserEntity;
    TextView navUsernameTV;
    private LocationPreferenceManager locationPrefManager;
    TextView navUserMailTV;
    private int RADIUS;
    private double currentLat = 0;
    private double currentLong = 0;
    private PermissionManager permissionManager;
    private RestaurantViewModel restaurantViewModel;
    private LocationManager locationManager;
    private final String CRUISE = "indian";
    private final String MAP_KEY = "MAP";
    private final String LATITUDE_KEY = "LATITUDE";
    private final String LONGITUDE_KEY = "LONGITUDE";
    private final String RADIUS_KEY = "RADIUS";


    public MainActivity() {
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        locationPrefManager = new LocationPreferenceManager(this);
        permissionManager = new PermissionManager();
        permissionManager.registerForPermissionResult(this);
        locationManager = new LocationManager(this);
        restaurantViewModel =
                new ViewModelProvider(this).get(RestaurantViewModel.class);

        initRestaurantList();
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
        navUsernameTV = headerView.findViewById(R.id.drawer_username_textview);
        navUserMailTV = headerView.findViewById(R.id.drawer_user_mail);
    }

    private void setUpNavigation() {
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.navigation_map_view, R.id.navigation_list_view, R.id.navigation_workmates).setDrawerLayout(mDrawerLayout)
                        .build();
        mToggle =
                new ActionBarDrawerToggle(this, mDrawerLayout, R.string.nav_drawer_open, R.string.nav_drawer_close);

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
        String username =
                TextUtils.isEmpty(mCurrentUserEntity.getUserName()) ? getString(R.string.username_not_found) : mCurrentUserEntity.getUserName();
        String userMail =
                TextUtils.isEmpty(mCurrentUserEntity.getUserEmail()) ? getString(R.string.user_mail_not_found) : mCurrentUserEntity.getUserEmail();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_filter_menu, menu);
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

    @Override
    protected void onStop() {
        super.onStop();
        locationPrefManager.clearLastLocation();
    }

    private void initRestaurantList() {
        RADIUS = locationPrefManager.getRadius();
        locationManager.getLastLocation();
    }

    private void updateRestaurantsList(double latitude, double longitude) {

        restaurantViewModel.updateRestaurantsWithPlaces(latitude, longitude, RADIUS, getString(R.string.google_map_key));
    }

    @Override
    public void onLocationFetched(Location location) {
        currentLat = location.getLatitude();
        currentLong = location.getLongitude();
        locationPrefManager.saveLastLocation(currentLat, currentLong);
        updateRestaurantsList(currentLat, currentLong);
    }

    @Override
    public void onLocationError(Exception e) {

    }
}