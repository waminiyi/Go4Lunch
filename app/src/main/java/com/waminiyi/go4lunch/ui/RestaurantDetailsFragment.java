package com.waminiyi.go4lunch.ui;

import static android.content.ContentValues.TAG;
import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.api.ApiException;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPhotoRequest;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.BuildConfig;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.databinding.FragmentRestaurantDetailsBinding;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.manager.GoNotificationManager;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.model.User;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;
import com.waminiyi.go4lunch.viewmodel.ReviewViewModel;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class RestaurantDetailsFragment extends Fragment implements FirebaseHelper.LunchListener,
        FirebaseHelper.ReviewListener {


    private static final String RESTAURANT = "restaurant";
    private static final String RESTAURANT_ID = "restaurantId";
    private String phoneNumber;
    private Uri websiteUri;
    private String restaurantId;
    private String restaurantName;
    private String restaurantPhoto;
    private String restaurantAddress;
    private Restaurant restaurant;
    private FragmentRestaurantDetailsBinding binding;
    private LunchViewModel lunchViewModel;
    private UserViewModel userViewModel;
    private Lunch currentUserLunch;
    private UserEntity currentUser;
    private ReviewViewModel reviewViewModel;
    private final String MAPS_API_KEY = BuildConfig.MAPS_API_KEY;
    private PhotoMetadata photoMetadata;
    private PlacesClient placesClient;


    @Inject
    GoNotificationManager mNotificationManager;

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

    /**
     * @param restaurantId : ID of Restaurant that we want to show details for
     * @return A new instance of fragment RestaurantDetailsFragment.
     */
    public static RestaurantDetailsFragment newInstance(String restaurantId) {
        RestaurantDetailsFragment fragment = new RestaurantDetailsFragment();
        Bundle args = new Bundle();
        args.putString(RESTAURANT_ID, restaurantId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            restaurant = getArguments().getParcelable(RESTAURANT);
            if (restaurant != null) {
                restaurantId = restaurant.getId();
                restaurantName = restaurant.getName();
                restaurantAddress = restaurant.getAddress();
                restaurantPhoto = restaurant.getPhotoReference();
            } else {
                restaurantId = getArguments().getString(RESTAURANT_ID);
            }
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
        placesClient = Places.createClient(requireContext());

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
        this.observeData();
        this.fetchPlaceDetails();
        this.updateUi();
        this.setListeners();
    }


    private void observeData() {
        reviewViewModel.setReviewListener(this);
        lunchViewModel.setLunchListener(this);

        reviewViewModel.listenToRatings();
        lunchViewModel.listenToLunches();
        reviewViewModel.listenToRestaurantReviews(restaurantId);

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
        binding.tvRestaurantDetailsName.setText(restaurantName);
        binding.tvRestaurantDetailsAddress.setText(restaurantAddress);
        if (restaurant != null && restaurantPhoto != null) {
            String imgUrl =
                    getString(R.string.place_image_url) + restaurantPhoto + "&key=" +
                            MAPS_API_KEY;

            Glide.with(this).load(imgUrl).fitCenter().placeholder(R.drawable.restaurant_image_placeholder).
                    into(binding.restaurantDetailsImage);
        } else if (photoMetadata != null) {
            // Get the attribution text.
            final String attributions = photoMetadata.getAttributions();

            // Create a FetchPhotoRequest.
            final FetchPhotoRequest photoRequest = FetchPhotoRequest.builder(photoMetadata)
                    .setMaxWidth(500)
                    .setMaxHeight(300)
                    .build();
            placesClient.fetchPhoto(photoRequest).addOnSuccessListener(fetchPhotoResponse -> {
                Bitmap bitmap = fetchPhotoResponse.getBitmap();
                Glide.with(this)
                        .asBitmap()
                        .load(bitmap)
                        .into(binding.restaurantDetailsImage);
            }).addOnFailureListener(exception -> {
                if (exception instanceof ApiException) {
                    Log.e(TAG, "Place not found: " + exception.getMessage());
                }

            });

        } else {
            String imgUrl = getString(R.string.restaurant_image_placeholder_url);
            Glide.with(this).load(imgUrl).fitCenter().placeholder(R.drawable.restaurant_image_placeholder).
                    into(binding.restaurantDetailsImage);
        }
        updateLunchButton();
    }

    private void setListeners() {
        binding.callButton.setOnClickListener(view -> {
            if (phoneNumber != null) {
                dialPhoneNumber(phoneNumber);
            } else {
                String message = getString(R.string.no_phone_number);
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.likeButton.setOnClickListener(view -> updateUserFavorites());

        binding.websiteButton.setOnClickListener(view -> {
            if (websiteUri != null) {
                openWebPage(websiteUri);
            } else {
                String message = getString(R.string.no_website);
                Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show();
            }
        });

        binding.buttonSetLunch.setOnClickListener(view -> updateUserLunch());

        binding.closeButton.setOnClickListener(view -> NavHostFragment.findNavController(this).navigateUp());
    }

    private void updateLunchButton() {
        if (currentUserLunch != null && currentUserLunch.getRestaurantId().equals(restaurantId)) {
            binding.buttonSetLunch.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FF9800")));
        } else {
            binding.buttonSetLunch.setImageTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
        }
    }

    private void updateFavoriteButton() {
        Drawable[] drawable = binding.likeButton.getCompoundDrawables();

        if (currentUser.getFavoriteRestaurant().contains(restaurantId)) {
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
                new Lunch(currentUser.getUserId(),
                        restaurantId,
                        restaurantName);

        if (currentUserLunch == null) {
            lunchViewModel.setCurrentUserLunch(lunch); //Lunch changed
            setNotificationForLunch(lunch);
        } else if (currentUserLunch.getRestaurantId().equals(restaurantName)) {
            lunchViewModel.deleteCurrentUserLunch(currentUserLunch);//No more lunch
            if (mNotificationManager.isNotificationAlreadyScheduled(requireContext())) {
                mNotificationManager.cancelLunchNotification(requireContext());
            }
        } else {
            lunchViewModel.deleteCurrentUserLunch(currentUserLunch);
            lunchViewModel.setCurrentUserLunch(lunch);//Lunch changed
            setNotificationForLunch(lunch);
        }

        updateLunchButton();
        Handler handler = new Handler();
        handler.postDelayed(() -> binding.buttonSetLunch.setEnabled(true), 1000);
    }


    private void updateUserFavorites() {
        MainActivity activity = (MainActivity) requireActivity();
        activity.updateUserFavorites(restaurantId);
    }


    private void fetchPlaceDetails() {

        // Specify the fields to return.
        List<Place.Field> placeFields;

        if (restaurant != null) {
            placeFields =
                    Arrays.asList(Place.Field.PHONE_NUMBER, Place.Field.WEBSITE_URI);
        } else {
            placeFields =
                    Arrays.asList(Place.Field.NAME,
                            Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS,
                            Place.Field.PHONE_NUMBER,
                            Place.Field.WEBSITE_URI);
        }

        final PlacesClient placesClient = Places.createClient(requireContext());

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request =
                FetchPlaceRequest.newInstance(restaurantId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            phoneNumber = place.getPhoneNumber();
            websiteUri = place.getWebsiteUri();
            if (restaurant == null) {
                restaurantName = place.getName();
                restaurantAddress = place.getAddress();

                photoMetadata =
                        place.getPhotoMetadatas() != null ? place.getPhotoMetadatas().get(0) : null;

                updateUi();
            }
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
        reviewViewModel.parseRatingsDoc(restaurantId, ratingsDoc);
    }


    @Override
    public void onLunchesUpdate(DocumentSnapshot lunchesDoc) {
        lunchViewModel.parseLunchesDoc(lunchesDoc);
        lunchViewModel.getCurrentRestaurantLunchesFromDb(restaurantId);

    }

    @Override
    public void onLunchesCountUpdate(DocumentSnapshot lunchesCountDoc) {

    }

    @Override
    public void onReviewsUpdate(DocumentSnapshot reviewsDoc) {
        reviewViewModel.parseReviewsDoc(reviewsDoc);
    }

    private void showLunchesFragment() {
        getChildFragmentManager().beginTransaction().replace(R.id.lunch_and_review,
                new LunchFragment()).commit();
    }

    private void showReviewsFragment() {
        getChildFragmentManager().beginTransaction().replace(R.id.lunch_and_review,
                ReviewFragment.newInstance(restaurantId, restaurantName,
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

    private void setNotificationForLunch(Lunch lunch) {
        final List<User> workmates = new ArrayList<>();

        lunchViewModel.getCurrentRestaurantLunches().observe(getViewLifecycleOwner(), userList ->
                {
                    StringBuilder notificationContent =
                            new StringBuilder("Hey " + currentUser.getUserName() + " ! It's your " +
                                    "lunch time at " + lunch.getRestaurantName() + " \nThe " +
                                    "address is : " +
                                    " " + restaurantAddress + ".\n");
                    if (userList.size() > 1) {
                        notificationContent.append("You are going to lunch with: \n");
                        for (User user : userList) {
                            if (!user.getUserId().equals(currentUser.getUserId())) {
                                notificationContent.append("> ").append(user.getUserName()).append(
                                        "\n" +
                                                " ");
                            }
                        }
                    }
                    mNotificationManager.scheduleLunchNotification(requireContext(),
                            "It's Lunch time !", String.valueOf(notificationContent));
                }
        );

        mNotificationManager.scheduleLunchNotification(requireContext(), currentUser.getUserName(), lunch.getRestaurantName());
    }

}