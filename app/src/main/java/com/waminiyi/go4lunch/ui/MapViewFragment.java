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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.util.BitmapUtil;
import com.waminiyi.go4lunch.viewmodel.RestaurantViewModel;
import com.waminiyi.go4lunch.viewmodel.StateViewModel;

import java.util.List;
import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MapViewFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private RestaurantViewModel restaurantViewModel;
    private List<Restaurant> currentRestaurantList;
    private StateViewModel mStateViewModel;
    private LatLng currentLocation;
    private GoogleMap map;

    public MapViewFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        restaurantViewModel =
                new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        mStateViewModel = new ViewModelProvider(requireActivity()).get(StateViewModel.class);
        restaurantViewModel.getCurrentLocation().observe(getViewLifecycleOwner(), latLng -> {
            currentLocation = latLng;
            centerOnUser();
        });

        setupMapIfNeeded();
        view.findViewById(R.id.center_on_user).setOnClickListener(view1 -> centerOnUser());

        return view;
    }


    private void markUserPosition(LatLng currentLocation) {

        MarkerOptions options = new MarkerOptions();
        options.position(currentLocation)
                .title(getString(R.string.you_are_here))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_my_location));

        map.addMarker(options);
    }

    private void showNearbyRestaurant(List<Restaurant> restaurantList) {

        map.clear();

        if (restaurantList != null) {
            for (Restaurant restaurant : restaurantList) {
                LatLng latLng = new LatLng(restaurant.getLatitude(), restaurant.getLongitude());
                MarkerOptions options = new MarkerOptions();
                options.position(latLng);
                options.title(restaurant.getName());
                if (restaurant.isOpenNow()) {
                    options.icon(new BitmapUtil().bitmapDescriptorFromVector(requireContext(),
                            getResources().getColor(R.color.green)));

                } else {
                    options.icon(new BitmapUtil().bitmapDescriptorFromVector(requireContext(),
                            getResources().getColor(R.color.red)));
                }

                Marker marker = map.addMarker(options);
                Objects.requireNonNull(marker).setTag(restaurant);
            }
        }

        markUserPosition(currentLocation);
    }

    private void centerOnUser() {
        if (map != null && currentLocation != null) {
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 16));
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;

        CameraPosition position = mStateViewModel.getSavedMapCameraPosition();
        if (position != null) {
            CameraUpdate update = CameraUpdateFactory.newCameraPosition(position);
            map.moveCamera(update);
        }

        map.setOnMarkerClickListener(this);
        try {

            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            requireContext(), R.raw.map_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        if (getView() != null) {
            restaurantViewModel.getRestaurantLiveList().observe(getViewLifecycleOwner(), restaurantList -> {
                currentRestaurantList = restaurantList;
                showNearbyRestaurant(currentRestaurantList);
            });
        }


    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        if (marker.getTag() != null) {
            Restaurant restaurant = (Restaurant) marker.getTag();

            ((MainActivity) requireActivity()).openDetails(restaurant.getId(),
                    restaurant.getName(), restaurant.getAddress(), restaurant.getPhotoReference());

        }
        return false;
    }

    private void setupMapIfNeeded() {
        if (map == null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map_fragment_container);
            Objects.requireNonNull(mapFragment).getMapAsync(this);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mStateViewModel.saveMapCameraPosition(map);
    }


    @Override
    public void onResume() {
        super.onResume();
        setupMapIfNeeded();
    }
}