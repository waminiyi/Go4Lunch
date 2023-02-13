package com.waminiyi.go4lunch.ui;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.bumptech.glide.Glide;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.databinding.FragmentRestaurantDetailsBinding;
import com.waminiyi.go4lunch.manager.PreferenceManager;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.util.SnapshotListener;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;
import com.waminiyi.go4lunch.viewmodel.ReviewViewModel;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class RestaurantDetailsFragment extends Fragment implements SnapshotListener {


    private static final String RESTAURANT = "restaurant";
    private static final String RESTAURANT_ID = "restaurantId";
    private String phoneNumber;
    private Uri websiteUri;
    private Restaurant restaurant;
    private FragmentRestaurantDetailsBinding binding;
    private LunchViewModel lunchViewModel;
    private UserViewModel userViewModel;
    private String currentUserLunch;
    private UserEntity currentUser;
    private ReviewViewModel reviewViewModel;

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

        binding.scrollView.getViewTreeObserver().addOnScrollChangedListener(() -> {
            /* get the maximum height which we have scroll before performing any action */
            int maxDistance = binding.headerLayout.getHeight();
            /* how much we have scrolled */
            int movement = binding.scrollView.getScrollY();

            if (movement >= 0 && movement <= maxDistance) {
                /*for image parallax with scroll */
                binding.headerLayout.setTranslationY((float) movement / 2);
            }
        });
        configureTabLayout();

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        lunchViewModel = new ViewModelProvider(requireActivity()).get(LunchViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);
        this.initData();
        this.observeData();
        this.fetchPlaceDetails();
        this.updateUi();
        this.setListeners();
    }

    private void initData(){
        lunchViewModel.getCurrentRestaurantLunchesFromDb(restaurant.getId());
        reviewViewModel.getAllReviewsFromDb(restaurant.getId());
        reviewViewModel.getCurrentRestaurantRatingFromDb(restaurant.getId());
        reviewViewModel.getCurrentUserReviewFromDb(restaurant.getId());
    }

    private void observeData(){

        reviewViewModel.setListener(this);
        reviewViewModel.listenToRestaurantReviews(restaurant.getId());

        lunchViewModel.getCurrentUserLunch().observe(getViewLifecycleOwner(), lunch -> {
            currentUserLunch = lunch.getRestaurantId();
            updateLunchButton();
            lunchViewModel.getCurrentRestaurantLunchesFromDb(restaurant.getId());
        });

        userViewModel.getCurrentUserData().observe(getViewLifecycleOwner(), userEntity ->
                currentUser = userEntity);
    }

    private void updateUi() {
        binding.tvRestaurantDetailsName.setText(restaurant.getName());
        binding.tvRestaurantDetailsAddress.setText(restaurant.getAddress());

        String imgUrl;
        if (restaurant.getPhotoReference() != null) {
            imgUrl = getString(R.string.place_image_url) + restaurant.getPhotoReference() + "&key=" +
                    getString(R.string.google_map_key);
        } else {
            imgUrl = getString(R.string.restaurant_image_placeholder_url);
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

        binding.buttonSetLunch.setOnClickListener(view -> updateUserLunch());

        binding.closeButton.setOnClickListener(view -> {
            NavHostFragment.findNavController(this).navigateUp();
        });
    }

    private void updateLunchButton() {
        if (currentUserLunch != null && currentUserLunch.equals(restaurant.getId())) {
            binding.buttonSetLunch.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
        } else {
            binding.buttonSetLunch.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        }
    }

    private void updateUserLunch() {
        binding.buttonSetLunch.setEnabled(false);

        Lunch lunch =
                new Lunch(currentUser.getuId(), currentUser.getUserName(), currentUser.getUrlPicture(),
                        restaurant.getId(),
                        restaurant.getName());

        if (currentUserLunch == null) {
            lunchViewModel.setCurrentUserLunch(lunch, restaurant);
        } else if (currentUserLunch.equals(restaurant.getId())) {
            lunchViewModel.deleteCurrentUserLunch(lunch.getUserId(), restaurant.getId());
        } else {
            lunchViewModel.deleteCurrentUserLunch(lunch.getUserId(), currentUserLunch);
            lunchViewModel.setCurrentUserLunch(lunch, restaurant);

        }
        updateLunchButton();
        Handler handler = new Handler();
        handler.postDelayed(() -> binding.buttonSetLunch.setEnabled(true), 1000);
    }


    private void fetchPlaceDetails() {

        // Specify the fields to return.
        final List<Place.Field> placeFields =
                Arrays.asList(Place.Field.ID, Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI);
        final PlacesClient placesClient = Places.createClient(requireContext());

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request =
                FetchPlaceRequest.newInstance(restaurant.getId(), placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            phoneNumber = place.getPhoneNumber();
            websiteUri = place.getWebsiteUri();
        }).addOnFailureListener((exception) -> {

            // TODO: Handle error with given status code.
        });
    }

    @Override
    public void onRatingsUpdate(DocumentSnapshot ratingsDoc) {

    }

    @Override
    public void onLunchesUpdate(DocumentSnapshot lunchesDoc) {

    }

    @Override
    public void onCurrentUserUpdate(DocumentSnapshot userDoc) {

    }

    @Override
    public void onUsersSnippetUpdate(DocumentSnapshot userSnippetDoc) {

    }

    @Override
    public void onReviewsUpdate(DocumentSnapshot reviewsDoc) {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            reviewViewModel.getAllReviewsFromDb(restaurant.getId());
            reviewViewModel.getCurrentUserReviewFromDb(restaurant.getId());
            reviewViewModel.getCurrentRestaurantRatingFromDb(restaurant.getId());

        }, 1000);
    }

    private void showLunchesFragment() {
        getChildFragmentManager().beginTransaction().replace(R.id.lunch_and_review,
                new LunchFragment()).commit();
    }

    private void showReviewsFragment() {
        getChildFragmentManager().beginTransaction().replace(R.id.lunch_and_review,
                ReviewFragment.newInstance(restaurant.getId(), restaurant.getName(),
                        currentUser)).commit();
    }

    private void configureTabLayout() {
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.lunches));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText(R.string.reviews));
        showLunchesFragment();

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int i = tab.getPosition();
                if (i == 1) {
                    showReviewsFragment();

                } else {
                    showLunchesFragment();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}