package com.waminiyi.go4lunch.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.manager.LocationManager;
import com.waminiyi.go4lunch.manager.PreferenceManager;
import com.waminiyi.go4lunch.manager.PermissionManager;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.util.SnapshotListener;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;
import com.waminiyi.go4lunch.viewmodel.RestaurantViewModel;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, LocationManager.LocationListener, SnapshotListener {

    private NavController mNavController;
    private BottomNavigationView mBottomNavigationView;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mToggle;
    private NavigationView mNavigationView;
    private UserViewModel mUserViewModel;
    private UserEntity mCurrentUserEntity;
    private PreferenceManager prefManager;

    private int RADIUS;
    private double currentLat = 0;
    private double currentLong = 0;
    private PermissionManager permissionManager;
    private LunchViewModel lunchViewModel;
    private RestaurantViewModel restaurantViewModel;
    private LocationManager locationManager;
    private final String CRUISE = "indian";
    private final String MAP_KEY = "MAP";
    private final String LATITUDE_KEY = "LATITUDE";
    private final String LONGITUDE_KEY = "LONGITUDE";
    private final String RADIUS_KEY = "RADIUS";
    private String currentUserLunch;
    private String currentUserId;
    private String currentUserName;
    private String currentUserPicture;
    private List<String> currentUserFavorites;
    private String currentUserEmail;


    public MainActivity() {
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        lunchViewModel = new ViewModelProvider(this).get(LunchViewModel.class);
        prefManager = new PreferenceManager(this);
        permissionManager = new PermissionManager();
        permissionManager.registerForPermissionResult(this);
        locationManager = new LocationManager(this);
        restaurantViewModel =
                new ViewModelProvider(this).get(RestaurantViewModel.class);
        restaurantViewModel.setListener(this);
        restaurantViewModel.listenToLunches();
        restaurantViewModel.listenToRatings();
        restaurantViewModel.listenToUsersSnippet();
        restaurantViewModel.listenToCurrentUserDoc();
        initRestaurantList();
        this.configureViews();
        this.setUpNavigation();
        this.updateUI();

//        lunchViewModel.retrieveAllUsers();

        lunchViewModel.getCurrentUserLunch().observe(this, new Observer<Lunch>() {
            @Override
            public void onChanged(Lunch lunch) {
                setCurrentUserLunch(lunch.getRestaurantId());

            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        prefManager.clearLunch();
    }

    private void configureViews() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation_view);
        mNavController = Navigation.findNavController(this, R.id.main_frame_layout);
        mDrawerLayout = findViewById(R.id.drawer_layout);
        mNavigationView = findViewById(R.id.navigation_view);
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

        mNavController.addOnDestinationChangedListener((controller, destination, arguments) -> {

            if (destination.getId() == R.id.navigation_your_lunch) {
                Objects.requireNonNull(getSupportActionBar()).hide();
                mBottomNavigationView.setVisibility(View.GONE);
            } else {
                Objects.requireNonNull(getSupportActionBar()).show();
                mBottomNavigationView.setVisibility(View.VISIBLE);
            }
        });


    }

    private void updateUI() {
        getCurrentUserData();
    }

    private void updateNavDrawerWithUserData() {
        View headerView = mNavigationView.getHeaderView(0);
        TextView navUsernameTV = headerView.findViewById(R.id.drawer_username_textview);
        TextView navUserMailTV = headerView.findViewById(R.id.drawer_user_mail);
        ImageView navImageView = headerView.findViewById(R.id.drawer_profile_image);
        String username =
                TextUtils.isEmpty(currentUserName) ? getString(R.string.username_not_found) :
                        currentUserName;
        String userMail =
                TextUtils.isEmpty(currentUserEmail) ?
                        getString(R.string.user_mail_not_found) :
                        currentUserEmail;
        navUsernameTV.setText(username);
        navUserMailTV.setText(userMail);
        Glide.with(this).load(currentUserPicture).circleCrop().placeholder(R.drawable.restaurant_image_placeholder).
                into(navImageView);

        //TODO : Handle null User

    }

    private void getCurrentUserData() {
        mUserViewModel.getCurrentUserData().observe(this, userEntity -> {
            updateUserData(userEntity);
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
                Restaurant restaurant =
                        restaurantViewModel.getUserLunchRestaurant(prefManager.getLunchRestaurantId());
                Bundle args = new Bundle();
                args.putParcelable("restaurant", restaurant);
                mNavController.navigate(item.getItemId(), args);

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

    public void updateUserData(UserEntity currentUserEntity) {
        this.setCurrentUserId(currentUserEntity.getuId());
        this.setCurrentUserName(currentUserEntity.getUserName());
        this.setCurrentUserPicture(currentUserEntity.getUrlPicture());
        this.setCurrentUserEmail(currentUserEntity.getUserEmail());
        this.setCurrentUserFavorites(currentUserEntity.getFavoriteRestaurant());

//        prefManager.saveUserId(currentUserEntity.getuId());
//        prefManager.saveUserName(currentUserEntity.getUserName());
//        prefManager.saveUserPictureUrl(currentUserEntity.getUrlPicture());
//        prefManager.saveFavoriteRestaurants(currentUserEntity.getFavoriteRestaurant());
//        prefManager.saveUserMail(currentUserEntity.getUserEmail());
//        mCurrentUserEntity = currentUserEntity;
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

    private void initRestaurantList() {
        RADIUS = prefManager.getRadius();
        locationManager.getLastLocation();
    }

    private void updateRestaurantsList(double latitude, double longitude) {

        restaurantViewModel.updateRestaurantsWithPlaces(latitude, longitude, RADIUS, getString(R.string.google_map_key));
    }

    @Override
    public void onLocationFetched(Location location) {
        this.setCurrentLat(location.getLatitude());
        this.setCurrentLong(location.getLongitude());
        updateRestaurantsList(currentLat, currentLong);
    }

    @Override
    public void onLocationError(Exception e) {

    }

    @Override
    public void onRatingsUpdate(DocumentSnapshot ratingsDoc) {
        restaurantViewModel.updateRestaurantsWithRating();
    }

    @Override
    public void onLunchesUpdate(DocumentSnapshot lunchesDoc) {
        restaurantViewModel.updateRestaurantsWithLunches();
        lunchViewModel.updateLunches();
    }

    @Override
    public void onCurrentUserUpdate(DocumentSnapshot userDoc) {
        mUserViewModel.getCurrentUserDataFromDatabase();
    }

    @Override
    public void onUsersSnippetUpdate(DocumentSnapshot userSnippetDoc) {
        lunchViewModel.retrieveAllUsers();
    }

    @Override
    public void onReviewsUpdate(DocumentSnapshot reviewsDoc) {

    }

    public double getCurrentLat() {
        return currentLat;
    }

    public void setCurrentLat(double currentLat) {
        this.currentLat = currentLat;
    }

    public double getCurrentLong() {
        return currentLong;
    }

    public void setCurrentLong(double currentLong) {
        this.currentLong = currentLong;
    }

    public String getCurrentUserLunch() {
        return currentUserLunch;
    }

    public void setCurrentUserLunch(String currentUserLunch) {
        this.currentUserLunch = currentUserLunch;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public String getCurrentUserName() {
        return currentUserName;
    }

    public void setCurrentUserName(String currentUserName) {
        this.currentUserName = currentUserName;
    }

    public String getCurrentUserPicture() {
        return currentUserPicture;
    }

    public void setCurrentUserPicture(String currentUserPicture) {
        this.currentUserPicture = currentUserPicture;
    }

    public List<String> getCurrentUserFavorites() {
        return currentUserFavorites;
    }

    public void setCurrentUserFavorites(List<String> currentUserFavorites) {
        this.currentUserFavorites = currentUserFavorites;
    }

    public String getCurrentUserEmail() {
        return currentUserEmail;
    }

    public void setCurrentUserEmail(String currentUserEmail) {
        this.currentUserEmail = currentUserEmail;
    }
}