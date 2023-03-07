package com.waminiyi.go4lunch.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.MobileNavigationDirections;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.databinding.ActivityMainBinding;
import com.waminiyi.go4lunch.databinding.DrawerHeaderBinding;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.manager.LocationManager;
import com.waminiyi.go4lunch.manager.LocationPermissionObserver;
import com.waminiyi.go4lunch.manager.NetworkStateManager;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.util.FilterMethod;
import com.waminiyi.go4lunch.util.NetworkMonitoringUtil;
import com.waminiyi.go4lunch.util.SortMethod;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;
import com.waminiyi.go4lunch.viewmodel.RestaurantViewModel;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        LocationManager.LocationListener, FirebaseHelper.UserListener,
        LocationPermissionObserver.PermissionListener,
        SharedPreferences.OnSharedPreferenceChangeListener {

    private NavController navController;
    private ActionBarDrawerToggle mToggle;
    private static final String RESTAURANT = "restaurant";
    private UserEntity currentUserEntity;
    private LunchViewModel lunchViewModel;
    private RestaurantViewModel restaurantViewModel;
    private UserViewModel userViewModel;
    private LocationPermissionObserver mLocationPermissionObserver;
    private SharedPreferences preferences;
    private ActivityResultLauncher<Intent> mPlaceSearchLauncher;
    LatLng mLatLng;


    private LocationManager locationManager;

    private Lunch currentUserLunch;
    private ActivityMainBinding binding;

    @Inject
    NetworkMonitoringUtil mNetworkMonitoringUtil;
    @Inject
    NetworkStateManager mNetworkStateManager;

    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        mLocationPermissionObserver = new LocationPermissionObserver(getActivityResultRegistry());
        getLifecycle().addObserver(mLocationPermissionObserver);
        mLocationPermissionObserver.setListener(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.verifyPermission();
        mNetworkMonitoringUtil.registerNetworkCallbackEvents();

        mNetworkStateManager.getNetworkConnectivityStatus().observe(this, isConnected -> {
            if (isConnected) {
                Toast.makeText(this, "Network available", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Network unavailable", Toast.LENGTH_LONG).show();
            }
        });

        registerForPlaceSearchResult();

    }


    private void registerForPlaceSearchResult() {
        mPlaceSearchLauncher =
                registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                        result -> {
                            if (result.getResultCode() == Activity.RESULT_OK) {
                                Intent data = result.getData();
                                if (data != null) {
                                    Place place = Autocomplete.getPlaceFromIntent(data);
//                                    openDetailsFragmentByPlaceId(place.getId());
                                    openDetails(place.getId(), null);
                                }
                            }
                        });
    }

    @Override
    public void onResume() {
        super.onResume();
        preferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        preferences.unregisterOnSharedPreferenceChangeListener(this);
    }

    private void verifyPermission() {

        if (mLocationPermissionObserver.isPermissionGranted(this)) {
            this.initActivity();
        } else {
            mLocationPermissionObserver.requestPermission();
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
                binding.sortAndFilterLayout.setVisibility(View.GONE);
            } else {
                binding.bottomNavigationView.setVisibility(View.VISIBLE);
                Objects.requireNonNull(getSupportActionBar()).show();
                if (destination.getId() == R.id.navigation_workmates) {
                    binding.sortAndFilterLayout.setVisibility(View.GONE);
                } else {
                    binding.sortAndFilterLayout.setVisibility(View.VISIBLE);
                    if (destination.getId() == R.id.navigation_map_view) {
                        binding.sortSpinner.setVisibility(View.GONE);
                    } else {
                        binding.sortSpinner.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    private void setUpSpinners() {
        Spinner sortSpinner = (Spinner) binding.sortSpinner;
        Spinner filterSpinner = (Spinner) binding.filterSpinner;

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> sortAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_array, android.R.layout.simple_spinner_item);

        ArrayAdapter<CharSequence> filterAdapter = ArrayAdapter.createFromResource(this,
                R.array.filter_array, android.R.layout.simple_spinner_item);

        // Specify the layout to use when the list of choices appears
        sortAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Apply the adapter to the spinner
        sortSpinner.setAdapter(sortAdapter);
        filterSpinner.setAdapter(filterAdapter);

        String defaultSorting = preferences.getString("sort", getString(R.string.distance));

        if (defaultSorting.equals(getString(R.string.distance))) {
            sortSpinner.setSelection(0);
        } else {
            sortSpinner.setSelection(1);
        }


        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String option = (String) parent.getItemAtPosition(position);
                if (option.equals(getString(R.string.distance))) {
                    restaurantViewModel.updateSortingMethod(SortMethod.NEAREST);
                } else if (option.equals(getString(R.string.rating))) {
                    restaurantViewModel.updateSortingMethod(SortMethod.RATING);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String option = (String) parent.getItemAtPosition(position);
                if (option.equals(getString(R.string.favorites))) {
                    restaurantViewModel.updateFilteringMethod(FilterMethod.FAVORITE);
                } else if (option.equals(getString(R.string.open_now))) {
                    restaurantViewModel.updateFilteringMethod(FilterMethod.OPEN);
                } else if (option.equals(getString(R.string.clear_filter))) {
                    restaurantViewModel.clearFilteringMethod();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        Glide.with(this).load(currentUserEntity.getUserPictureUrl()).circleCrop().placeholder(R.drawable.restaurant_image_placeholder).
                into(headerBinding.drawerProfileImage);

        //TODO : Handle null User
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.search) {
            if (Objects.requireNonNull(navController.getCurrentDestination()).getId()==R.id.navigation_workmates){

            }else{
                launchPlaceSearchActivity();
            }

        }
        return super.onOptionsItemSelected(item);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_your_lunch:

                if (currentUserLunch != null) {
//                    Restaurant restaurant =
//                            restaurantViewModel.getRestaurantById(currentUserLunch.getRestaurantId());
//                    MapViewFragmentDirections.MapToLunchAction lunchAction=
//                            MapViewFragmentDirections.mapToLunchAction(null,currentUserLunch.getRestaurantId());

//                    Bundle args = new Bundle();
//                    args.putParcelable(RESTAURANT, restaurant);
//                    navController.navigate(item.getItemId(), args);
//                    navController.navigate(lunchAction);
                    openDetails(currentUserLunch.getRestaurantId(), null);
                } else {
                    String message = getString(R.string.no_indicated_lunch_message);
                    Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
                    navController.navigate(R.id.navigation_list_view);
                }

                break;
            case R.id.navigation_settings:
                navigateToSettings();
//                navController.navigate(R.id.settings_fragment);
                break;
            case R.id.navigation_logout:
                this.logOut();
                break;
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void openDetails(String restaurantId, Restaurant restaurant) {
        Bundle args = new Bundle();
        args.putParcelable(RESTAURANT, restaurant);
        args.putString("restaurantId", restaurantId);
        navController.navigate(R.id.navigation_your_lunch, args);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_bar_menu, menu);
        return true;
    }

    private void launchPlaceSearchActivity() {
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        }

        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME);

        RectangularBounds bounds =
                RectangularBounds.newInstance(getBounds(mLatLng,
                        500));

        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields)
                .setHint(getString(R.string.search_a_restaurant))
                .setCountry("FR")
                .setTypesFilter(Collections.singletonList("restaurant"))
                .setLocationRestriction(bounds)
                .build(this);
        mPlaceSearchLauncher.launch(intent);

    }

    private void openDetailsFragmentByPlaceId(String placeId) {

        MobileNavigationDirections.DetailsByRestaurantId openDetailsById =
                MobileNavigationDirections.detailsByRestaurantId(placeId, null);

//        Bundle args = new Bundle();
//        Restaurant r= new Restaurant();
//        args.putParcelable(RESTAURANT, r);
//        args.putString("restaurantId", placeId);
        navController.navigate(openDetailsById);

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
        restaurantViewModel.updateSearchRadius(Integer.parseInt(preferences.getString("radius",
                "1000")));
        locationManager.getCurrentLocation();

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
        mLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        restaurantViewModel.updateCurrentLocation(location.getLatitude(), location.getLongitude());
        restaurantViewModel.updateRestaurantsWithPlaces();
        this.setUpSpinners();

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
    public void onLocationPermissionGranted() {
        this.initActivity();
    }

    @Override
    public void onLocationPermissionDenied() {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals("radius")) {
            restaurantViewModel.updateSearchRadius(Integer.parseInt(preferences.getString("radius",
                    "1000")));
            restaurantViewModel.updateRestaurantsWithPlaces();
        }

    }

    private LatLngBounds getBounds(LatLng location, int mDistanceInMeters) {
        double latRadian = Math.toRadians(location.latitude);

        double degLatKm = location.latitude;
        double degLongKm = location.longitude * Math.cos(latRadian);
        double deltaLat = mDistanceInMeters / 1000.0 / degLatKm;
        double deltaLong = mDistanceInMeters / 1000.0 / degLongKm;

        double minLat = location.latitude - deltaLat;
        double minLong = location.longitude - deltaLong;
        double maxLat = location.latitude + deltaLat;
        double maxLong = location.longitude + deltaLong;

        return new LatLngBounds(new LatLng(minLat, minLong), new LatLng(maxLat, maxLong));
    }


}