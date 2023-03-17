package com.waminiyi.go4lunch.ui;

import static android.content.ContentValues.TAG;

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
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

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
import com.waminiyi.go4lunch.databinding.ActivityRestaurantDetailsBinding;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.manager.GoNotificationManager;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.User;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.util.Constants;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;
import com.waminiyi.go4lunch.viewmodel.ReviewViewModel;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class RestaurantDetailsActivity extends AppCompatActivity implements FirebaseHelper.LunchListener,
        FirebaseHelper.ReviewListener, FirebaseHelper.UserListener {

    private String phoneNumber;
    private Uri websiteUri;
    private String restaurantId;
    private String restaurantName;
    private String restaurantPhoto;
    private String restaurantAddress;
    private ActivityRestaurantDetailsBinding binding;
    private LunchViewModel lunchViewModel;
    private UserViewModel userViewModel;
    private Lunch currentUserLunch;
    private UserEntity currentUser;
    private ReviewViewModel reviewViewModel;
    private final String MAPS_API_KEY = BuildConfig.MAPS_API_KEY;
    private PhotoMetadata photoMetadata;
    private PlacesClient placesClient;
    private List<User> lunches;
    @Inject
    GoNotificationManager mNotificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRestaurantDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        Intent intent = getIntent();

        restaurantId = intent.getStringExtra(Constants.RESTAURANT_ID);
        restaurantName = intent.getStringExtra(Constants.RESTAURANT_NAME);
        restaurantAddress = intent.getStringExtra(Constants.RESTAURANT_ADDRESS);
        restaurantPhoto = intent.getStringExtra(Constants.RESTAURANT_PHOTO);


        lunchViewModel = new ViewModelProvider(this).get(LunchViewModel.class);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        reviewViewModel = new ViewModelProvider(this).get(ReviewViewModel.class);



        //Initialize the places API if needed
        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.google_api_key));
        }
        this.observeData();
        this.fetchPlaceDetails();
        this.updateUi();
        this.setListeners();
        placesClient = Places.createClient(this);

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
//        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    private void observeData() {
        reviewViewModel.setReviewListener(this);
        lunchViewModel.setLunchListener(this);
        userViewModel.setUserListener(this);

        userViewModel.listenToUsersSnippet();
        userViewModel.listenToCurrentUserDoc();

        reviewViewModel.listenToRatings();
        lunchViewModel.listenToLunches();
        reviewViewModel.listenToRestaurantReviews(restaurantId);

        lunchViewModel.getCurrentUserLunch().observe(this, lunch -> {
            this.currentUserLunch = lunch;
            updateLunchButton();
        });

        userViewModel.getCurrentUserData().observe(this, userEntity -> {
            currentUser = userEntity;
            if (currentUser != null) {
                updateFavoriteButton();
            }

        });

        lunchViewModel.getCurrentRestaurantLunches().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> userList) {
                lunches = userList;
            }
        });
    }

    private void updateUi() {
        binding.tvRestaurantDetailsName.setText(restaurantName);
        binding.tvRestaurantDetailsAddress.setText(restaurantAddress);
        if (restaurantPhoto != null) {
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

        binding.closeButton.setOnClickListener(view ->finish());
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
        } else if (currentUserLunch.getRestaurantId().equals(restaurantId)) {
            lunchViewModel.deleteCurrentUserLunch(currentUserLunch);//No more lunch
            if (mNotificationManager.isNotificationAlreadyScheduled(this)) {
                mNotificationManager.cancelLunchNotification(this);
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
        if (currentUser.getFavoriteRestaurant().contains(restaurantId)) {
            userViewModel.removeRestaurantFromUserFavorite(restaurantId);
        } else {
            userViewModel.addRestaurantToUserFavorite(restaurantId);
        }

    }


    private void fetchPlaceDetails() {

        // Specify the fields to return.
        List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME,
                Place.Field.ADDRESS, Place.Field.PHOTO_METADATAS,
                Place.Field.PHONE_NUMBER,
                Place.Field.WEBSITE_URI);


        final PlacesClient placesClient = Places.createClient(this);

        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request =
                FetchPlaceRequest.newInstance(restaurantId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();
            phoneNumber = place.getPhoneNumber();
            websiteUri = place.getWebsiteUri();
            restaurantName = place.getName();
            String[] address = Objects.requireNonNull(place.getAddress()).split(",");
            restaurantAddress = address[0] + "," + address[1];
            photoMetadata =
                    place.getPhotoMetadatas() != null ? place.getPhotoMetadatas().get(0) : null;

            updateUi();

        }).addOnFailureListener((exception) -> {

            // TODO: Handle error with given status code.
        });
    }


    private void openWebPage(Uri webPage) {
        Intent intent = new Intent(Intent.ACTION_VIEW, webPage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
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
        getSupportFragmentManager().beginTransaction().replace(R.id.lunch_and_review,
                new LunchFragment()).commit();
    }

    private void showReviewsFragment() {
        getSupportFragmentManager().beginTransaction().replace(R.id.lunch_and_review,
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

//        StringBuilder notificationContent =
//                new StringBuilder("Hey " + currentUser.getUserName() + " ! It's your " +
//                        "lunch time at " + lunch.getRestaurantName() + " \nThe " +
//                        "address is : " +
//                        " " + restaurantAddress + ".\n");
//        if (lunches.size() > 1) {
//            notificationContent.append("You are going to lunch with: \n");
//            for (User user : lunches) {
//                if (!user.getUserId().equals(currentUser.getUserId())) {
//                    notificationContent.append("> ").append(user.getUserName()).append(
//                            "\n" +
//                                    " ");
//                }
//            }
//        }
//        mNotificationManager.scheduleLunchNotification(requireContext(),
//                "It's Lunch time !", String.valueOf(notificationContent), lunch.getRestaurantId());
        mNotificationManager.scheduleLunchNotification(this,
                currentUser.getUserId(), currentUser.getUserName(), restaurantId, restaurantName, restaurantAddress);


    }

    @Override
    public void onCurrentUserUpdate(DocumentSnapshot userDoc) {
        userViewModel.parseCurrentUserDoc(userDoc);
    }

    @Override
    public void onUsersSnippetUpdate(DocumentSnapshot userSnippetDoc) {
        lunchViewModel.parseUsersSnippetDoc(userSnippetDoc);
    }
}