package com.waminiyi.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.manager.PreferenceManager;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.viewmodel.RestaurantViewModel;

import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private RestaurantViewModel restaurantViewModel;
    private List<Restaurant> currentRestaurantList;
    private GoogleMap map;
    private double currentLat;
    private double currentLong;
    private PreferenceManager locationPrefManager;

    public MapViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationPrefManager = new PreferenceManager(requireContext());
    }


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        restaurantViewModel =
                new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        SupportMapFragment supportMapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map_fragment_container);

        Objects.requireNonNull(supportMapFragment).getMapAsync(this);

        view.findViewById(R.id.center_on_user).setOnClickListener(view1 -> {
            markUserPosition(currentLat, currentLong);
        });

        return view;
    }


    private void markUserPosition(double latitude, double longitude) {

        LatLng currentLocation = new LatLng(latitude, longitude);
        MarkerOptions options = new MarkerOptions();
        options.position(currentLocation)
                .title(getString(R.string.you_are_here))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.human_location));

        map.addMarker(options);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 14));
    }

    private void showNearbyRestaurant(List<Restaurant> restaurantList) {

        map.clear();
        for (Restaurant restaurant : restaurantList) {
            LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
            MarkerOptions options = new MarkerOptions();
            //set position
            options.position(latLng);

            //set title
            options.title(restaurant.getName());
            if (restaurant.isOpenNow()) {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.open_restau_marker));

            } else {
                options.icon(BitmapDescriptorFactory.fromResource(R.drawable.closed_restau_marker));
            }

            //add marker on map
            Marker marker = map.addMarker(options);
            Objects.requireNonNull(marker).setTag(restaurant);

        }

        MainActivity activity = (MainActivity) requireActivity();
        currentLat = restaurantViewModel.getLatitude();
        currentLong = restaurantViewModel.getLongitude();

        markUserPosition(currentLat, currentLong);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        map.setOnMarkerClickListener(this);
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            requireContext(), R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        restaurantViewModel.getRestaurantLiveList().observe(getViewLifecycleOwner(), restaurantList -> {
            currentRestaurantList = restaurantList;
            showNearbyRestaurant(currentRestaurantList);
        });
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker.getTag() != null) {
            Restaurant restaurant = (Restaurant) marker.getTag();
            MapViewFragmentDirections.MapToLunchAction action =
                    MapViewFragmentDirections.mapToLunchAction(restaurant);
            NavHostFragment.findNavController(this).navigate(action);
        }
        return false;
    }
}