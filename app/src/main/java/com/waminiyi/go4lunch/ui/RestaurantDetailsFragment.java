package com.waminiyi.go4lunch.ui;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;

import com.bumptech.glide.Glide;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.adapter.ViewPagerAdapter;
import com.waminiyi.go4lunch.databinding.FragmentRestaurantDetailsBinding;
import com.waminiyi.go4lunch.manager.LunchPreferenceManager;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class RestaurantDetailsFragment extends Fragment {


    private static final String RESTAURANT = "restaurant";
    private static final String RESTAURANT_ID = "restaurantId";
    private String phoneNumber;
    private Uri websiteUri;
    private Restaurant restaurant;
    private FragmentRestaurantDetailsBinding binding;
    private LunchViewModel lunchViewModel;
    private UserViewModel mUserViewModel;
    private LunchPreferenceManager lunchManager;
    private String USER_LUNCH;

    public RestaurantDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * @param restaurant : Restaurant that we want to show details for
     * @return A new instance of fragment RestaurantDetailsFragment.
     */
    public static RestaurantDetailsFragment newInstance(Restaurant restaurant) {
        RestaurantDetailsFragment fragment = new RestaurantDetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(RESTAURANT, restaurant);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurant = getArguments().getParcelable(RESTAURANT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRestaurantDetailsBinding.inflate(inflater);
        //Initialize the places API if needed
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        }
        lunchManager = new LunchPreferenceManager(requireContext());
        USER_LUNCH = lunchManager.getLunchRestaurantId();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        lunchViewModel = new ViewModelProvider(this).get(LunchViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        ViewPager2 viewPager = view.findViewById(R.id.pager);
        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(requireActivity());
        viewPager.setAdapter(pagerAdapter);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            if (position == 0) {
                tab.setText("Lunches");
                tab.setIcon(R.drawable.ic_lunch);
            } else
                tab.setText("Reviews");
        }).attach();
        fetchPlaceDetails(RESTAURANT_ID);
        updateUi();
        setListeners();
    }

    private void updateUi() {
        binding.restaurantDetailsName.setText(restaurant.getName());
        binding.restaurantDetailsAddress.setText(restaurant.getAddress());

        String imgUrl;
        if (restaurant.getPhotoReference() != null) {
            imgUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400" +
                    "&photoreference=" + restaurant.getPhotoReference() + "&key=" +
                    getString(R.string.google_map_key);
        } else {
            imgUrl =
                    "https://images.pexels.com/photos/262978/pexels-photo-262978.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2";
        }

        Glide.with(this).load(imgUrl).fitCenter().placeholder(R.drawable.restaurant_image_placeholder).
                into(binding.restaurantDetailsImage);
        updateLunchButton();
    }

    private void setListeners() {
        binding.callTextView.setOnClickListener(view -> {

        });

        binding.likeTextView.setOnClickListener(view -> {

        });

        binding.websiteTextView.setOnClickListener(view -> {

        });

        binding.lunchButton.setOnClickListener(view -> {
            String userId = mUserViewModel.getCurrentUserUID();
            Lunch lunch = new Lunch(userId, restaurant.getId(), restaurant.getName());

            if (USER_LUNCH == null) {
                USER_LUNCH = restaurant.getId();
                lunchManager.saveCurrentLunch(lunch);
                lunchViewModel.setCurrentUserLunch(lunch, restaurant);
            } else if (USER_LUNCH.equals(restaurant.getId())) {
                USER_LUNCH = null;
                lunchViewModel.deleteCurrentUserLunch(userId, restaurant.getId());
                lunchManager.clearLunch();
            } else {
                lunchViewModel.deleteCurrentUserLunch(userId, USER_LUNCH);

                USER_LUNCH = restaurant.getId();
                lunchManager.saveCurrentLunch(lunch);
                lunchViewModel.setCurrentUserLunch(lunch, restaurant);

            }
            updateLunchButton();
        });
    }

    private void updateLunchButton() {
        if (USER_LUNCH != null && USER_LUNCH.equals(restaurant.getId())) {
            binding.lunchButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
        } else {
            binding.lunchButton.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        }
    }

    private void fetchPlaceDetails(String placeId) {

        // Specify the fields to return.
        final List<Place.Field> placeFields =
                Arrays.asList(Place.Field.ID, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI);
        final PlacesClient placesClient = Places.createClient(requireContext());

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            phoneNumber = place.getPhoneNumber();
            websiteUri = place.getWebsiteUri();
        }).addOnFailureListener((exception) -> {

            // TODO: Handle error with given status code.
        });
    }


}