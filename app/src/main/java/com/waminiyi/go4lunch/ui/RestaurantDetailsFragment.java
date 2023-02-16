package com.waminiyi.go4lunch.ui;

import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.BuildConfig;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.databinding.FragmentRestaurantDetailsBinding;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;
import com.waminiyi.go4lunch.viewmodel.ReviewViewModel;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class RestaurantDetailsFragment extends Fragment implements FirebaseHelper.LunchListener,
        FirebaseHelper.ReviewListener {


    private static final String RESTAURANT = "restaurant";
    private String phoneNumber;
    private Uri websiteUri;
    private Restaurant restaurant;
    private FragmentRestaurantDetailsBinding binding;
    private LunchViewModel lunchViewModel;
    private UserViewModel userViewModel;
    private Lunch currentUserLunch;
    private UserEntity currentUser;
    private ReviewViewModel reviewViewModel;
    private final String MAPS_API_KEY = BuildConfig.MAPS_API_KEY;

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
        reviewViewModel = new ViewModelProvider(requireActivity()).get(ReviewViewModel.class);
        this.initData();
        this.observeData();
        this.fetchPlaceDetails();
        this.updateUi();
        this.setListeners();
    }

    private void initData() {
        lunchViewModel.getCurrentRestaurantLunchesFromDb(restaurant.getId());
        reviewViewModel.getAllReviewsFromDb(restaurant.getId());
        reviewViewModel.getCurrentRestaurantRatingFromDb(restaurant.getId());
        reviewViewModel.getCurrentUserReviewFromDb(restaurant.getId());
    }

    private void observeData() {
        reviewViewModel.setReviewListener(this);
        lunchViewModel.setLunchListener(this);

        reviewViewModel.listenToRatings();
        lunchViewModel.listenToLunches();
        reviewViewModel.listenToRestaurantReviews(restaurant.getId());

        lunchViewModel.getCurrentUserLunch().observe(getViewLifecycleOwner(), lunch -> {
            this.currentUserLunch = lunch;
            updateLunchButton();
        });

        userViewModel.getCurrentUserData().observe(getViewLifecycleOwner(), userEntity -> {
            currentUser = userEntity;
            if (currentUser != null) {
                updateFavoriteButton();
            }

        });
    }

    private void updateUi() {
        binding.tvRestaurantDetailsName.setText(restaurant.getName());
        binding.tvRestaurantDetailsAddress.setText(restaurant.getAddress());

        String imgUrl;
        if (restaurant.getPhotoReference() != null) {
            imgUrl =
                    getString(R.string.place_image_url) + restaurant.getPhotoReference() + "&key=" +
                            MAPS_API_KEY;
        } else {
            imgUrl = getString(R.string.restaurant_image_placeholder_url);
        }

        Glide.with(this).load(imgUrl).fitCenter().placeholder(R.drawable.restaurant_image_placeholder).
                into(binding.restaurantDetailsImage);
        updateLunchButton();
    }

    private void setListeners() {
        binding.callButton.setOnClickListener(view -> {
            if (phoneNumber != null) {
                dialPhoneNumber(phoneNumber);
            }else {
                String message = "Oups! This restaurant didn't provide a phone number.";
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.likeButton.setOnClickListener(view -> updateUserFavorites());

        binding.websiteButton.setOnClickListener(view -> {
            if (websiteUri != null) {
                openWebPage(websiteUri);
            } else {
                String message = "Oups! This restaurant doesn't have a website. Try to call. ";
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.buttonSetLunch.setOnClickListener(view -> updateUserLunch());

        binding.closeButton.setOnClickListener(view -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void updateLunchButton() {
        if (currentUserLunch != null && currentUserLunch.getRestaurantId().equals(restaurant.getId())) {
            binding.buttonSetLunch.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
        } else {
            binding.buttonSetLunch.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        }
    }

    private void updateFavoriteButton() {
        Drawable[] drawable = binding.likeButton.getCompoundDrawables();

        if (currentUser.getFavoriteRestaurant().contains(restaurant.getId())) {
            drawable[1].setColorFilter(getResources().getColor(R.color.isFavoriteColor),
                    PorterDuff.Mode.MULTIPLY);
        } else {
            drawable[1].setColorFilter(getResources().getColor(R.color.colorPrimary),
                    PorterDuff.Mode.MULTIPLY);
        }
        binding.likeButton.setCompoundDrawables(null, drawable[1], null, null);
    }


    private void updateUserLunch() {
        binding.buttonSetLunch.setEnabled(false);

        Lunch lunch =
                new Lunch(currentUser.getuId(), currentUser.getUserName(), currentUser.getUrlPicture(),
                        restaurant.getId(),
                        restaurant.getName());

        if (currentUserLunch == null) {
            lunchViewModel.setCurrentUserLunch(lunch);
        } else if (currentUserLunch.getRestaurantId().equals(restaurant.getId())) {
            lunchViewModel.deleteCurrentUserLunch(currentUserLunch);
        } else {
            lunchViewModel.deleteCurrentUserLunch(currentUserLunch);
            lunchViewModel.setCurrentUserLunch(lunch);
        }

        updateLunchButton();
        Handler handler = new Handler();
        handler.postDelayed(() -> binding.buttonSetLunch.setEnabled(true), 1000);
    }


    private void updateUserFavorites() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.updateUserFavorites(restaurant.getId());
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


    private void openWebPage(Uri webPage) {
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onRatingsUpdate(DocumentSnapshot ratingsDoc) {
        reviewViewModel.getCurrentRestaurantRatingFromDb(restaurant.getId());
    }

    @Override
    public void onLunchesUpdate(DocumentSnapshot lunchesDoc) {
        lunchViewModel.getCurrentUserLunchFromDb();
        lunchViewModel.getCurrentRestaurantLunchesFromDb(restaurant.getId());
        lunchViewModel.getLunchesFromDb();
    }

    @Override
    public void onReviewsUpdate() {
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            reviewViewModel.getAllReviewsFromDb(restaurant.getId());
            reviewViewModel.getCurrentUserReviewFromDb(restaurant.getId());

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