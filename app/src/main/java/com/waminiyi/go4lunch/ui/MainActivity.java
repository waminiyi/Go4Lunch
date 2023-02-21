package com.waminiyi.go4lunch.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.BuildConfig;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.databinding.ActivityMainBinding;
import com.waminiyi.go4lunch.databinding.DrawerHeaderBinding;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.manager.LocationManager;
import com.waminiyi.go4lunch.manager.PermissionManager;
import com.waminiyi.go4lunch.manager.PreferenceManager;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.util.FilterMethod;
import com.waminiyi.go4lunch.util.SortMethod;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;
import com.waminiyi.go4lunch.viewmodel.RestaurantViewModel;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        LocationManager.LocationListener, FirebaseHelper.UserListener, PermissionManager.PermissionListener {

    private NavController navController;
    private ActionBarDrawerToggle mToggle;
    private static final String RESTAURANT = "restaurant";
    private UserEntity currentUserEntity;
    private PreferenceManager prefManager;
    private LunchViewModel lunchViewModel;
    private RestaurantViewModel restaurantViewModel;
    private UserViewModel userViewModel;
    private PermissionManager permissionManager;
    private final String MAPS_API_KEY = BuildConfig.MAPS_API_KEY;

    private LocationManager locationManager;
    private final String CRUISE = "indian";
    private final String MAP_KEY = "MAP";
    private final String LATITUDE_KEY = "LATITUDE";
    private final String LONGITUDE_KEY = "LONGITUDE";
    private final String RADIUS_KEY = "RADIUS";
    private Lunch currentUserLunch;
    private ActivityMainBinding binding;

    public MainActivity() {
    }

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        this.verifyPermission();
    }

    private void verifyPermission() {
        permissionManager = new PermissionManager();
        permissionManager.registerForPermissionResult(this);
        if (permissionManager.isPermissionGranted(this)) {
            this.initActivity();
        } else {
            permissionManager.requestPermission();
        }
    }

    private void initActivity() {
        this.initVariables();
        this.initRestaurantList();
        this.setUpNavigation();
        this.observeData();

    }

    private void initVariables() {
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        lunchViewModel = new ViewModelProvider(this).get(LunchViewModel.class);
        restaurantViewModel =
                new ViewModelProvider(this).get(RestaurantViewModel.class);
        prefManager = new PreferenceManager(this);
        locationManager = new LocationManager(this);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.main_frame_layout);
        navController = Objects.requireNonNull(navHostFragment).getNavController();
        navController.setGraph(R.navigation.mobile_navigation);
    }

    private void observeData() {

        userViewModel.setUserListener(this);
        userViewModel.listenToUsersSnippet();
        userViewModel.listenToCurrentUserDoc();

        lunchViewModel.getCurrentUserLunch().observe(this, lunch ->
                this.currentUserLunch = lunch);

        userViewModel.getCurrentUserData().observe(this, userEntity -> {
            this.currentUserEntity = userEntity;
            updateNavDrawerWithUserData();
        });
    }

    private void setUpNavigation() {
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(R.id.navigation_map_view,
                        R.id.navigation_list_view, R.id.navigation_workmates).setDrawerLayout(binding.drawerLayout)
                        .build();
        mToggle =
                new ActionBarDrawerToggle(this, binding.drawerLayout, R.string.nav_drawer_open,
                        R.string.nav_drawer_close);

        binding.drawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();

        binding.navigationView.setNavigationItemSelectedListener(this);

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {

            if (destination.getId() == R.id.navigation_your_lunch) {
                Objects.requireNonNull(getSupportActionBar()).hide();
                binding.bottomNavigationView.setVisibility(View.GONE);
            } else {
                Objects.requireNonNull(getSupportActionBar()).show();
                binding.bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });

    }

    private void updateNavDrawerWithUserData() {
        DrawerHeaderBinding headerBinding =
                DrawerHeaderBinding.bind(binding.navigationView.getHeaderView(0));

        String username =
                TextUtils.isEmpty(currentUserEntity.getUserName()) ? getString(R.string.username_not_found) :
                        currentUserEntity.getUserName();
        String userMail =
                TextUtils.isEmpty(currentUserEntity.getUserEmail()) ?
                        getString(R.string.user_mail_not_found) :
                        currentUserEntity.getUserEmail();

        headerBinding.drawerUsernameTextview.setText(username);
        headerBinding.drawerUserMail.setText(userMail);
        Glide.with(this).load(currentUserEntity.getUrlPicture()).circleCrop().placeholder(R.drawable.restaurant_image_placeholder).
                into(headerBinding.drawerProfileImage);

        //TODO : Handle null User
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        item.setChecked(!item.isChecked());

        switch (item.getItemId()) {
            case R.id.sort_by_distance:
                restaurantViewModel.updateSortingMethod(SortMethod.NEAREST);
                break;
            case R.id.sort_by_rating:
                restaurantViewModel.updateSortingMethod(SortMethod.RATING);
                break;
            case R.id.reset_sorting:
                restaurantViewModel.updateSortingMethod(SortMethod.NONE);
                break;
            case R.id.filter_by_favorite:
                restaurantViewModel.updateFilteringMethod(FilterMethod.FAVORITE);
                break;
            case R.id.filter_open_now:
                restaurantViewModel.updateFilteringMethod(FilterMethod.OPEN);
                break;
            case R.id.filter_chinese:
                restaurantViewModel.updateRestaurantsWithPlaces(getString(R.string.chinese));
                break;
            case R.id.filter_french:
                restaurantViewModel.updateRestaurantsWithPlaces(getString(R.string.french));
                break;
            case R.id.filter_indian:
                restaurantViewModel.updateRestaurantsWithPlaces(getString(R.string.indian));
                break;
            case R.id.filter_japanese:
                restaurantViewModel.updateRestaurantsWithPlaces(getString(R.string.japanese));
                break;
            case R.id.filter_korean:
                restaurantViewModel.updateRestaurantsWithPlaces(getString(R.string.korean));
                break;
            case R.id.reset_cooking:
                restaurantViewModel.updateRestaurantsWithPlaces();
                break;
            case R.id.clear_filter:
                restaurantViewModel.clearFilteringMethod();
                break;

        }
        return super.onOptionsItemSelected(item);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_your_lunch:

                if (currentUserLunch != null) {
                    Restaurant restaurant =
                            restaurantViewModel.getRestaurantById(currentUserLunch.getRestaurantId());
                    Bundle args = new Bundle();
                    args.putParcelable(RESTAURANT, restaurant);
                    navController.navigate(item.getItemId(), args);
                } else {
                    String message = getString(R.string.no_indicated_lunch_message);
                    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
                    navController.navigate(R.id.navigation_list_view);
                }

                break;
            case R.id.navigation_settings:
                navigateToSettings();
                break;
            case R.id.navigation_logout:
                this.logOut();
                break;
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_filter_menu, menu);
        return true;
    }


    @Override
    public void onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private void logOut() {
        userViewModel.logOut();
        Intent logInIntent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(logInIntent);
        finish();
    }

    private void navigateToSettings() {
        Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    private void initRestaurantList() {
        restaurantViewModel.updateSearchRadius(prefManager.getRadius());
        locationManager.getLastLocation();

    }

    public void updateUserFavorites(String restaurantId) {

        if (currentUserEntity.getFavoriteRestaurant().contains(restaurantId)) {
            userViewModel.removeRestaurantFromUserFavorite(restaurantId);
        } else {
            userViewModel.addRestaurantToUserFavorite(restaurantId);
        }
    }


    @Override
    public void onLocationFetched(Location location) {
        restaurantViewModel.updateCurrentLocation(location.getLatitude(), location.getLongitude());
        restaurantViewModel.updateRestaurantsWithPlaces();

    }

    @Override
    public void onLocationError(Exception e) {
        //TODO
    }

    @Override
    public void onCurrentUserUpdate(DocumentSnapshot userDoc) {
        userViewModel.parseCurrentUserDoc(userDoc);
        restaurantViewModel.updateRestaurantsWithFavorites(userDoc);
    }

    @Override
    public void onUsersSnippetUpdate(DocumentSnapshot userSnippetDoc) {
        lunchViewModel.parseUsersSnippetDoc(userSnippetDoc);
    }

    @Override
    public void onPermissionGranted() {
        this.initActivity();
    }

    @Override
    public void onPermissionDenied() {
        Toast.makeText(this, getString(R.string.authorization_denied_message), Toast.LENGTH_LONG).show();
        Handler handler = new Handler();
        handler.postDelayed(this::finish, 3000);
    }


}