package com.waminiyi.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.adapter.ReviewAdapter;
import com.waminiyi.go4lunch.databinding.FragmentReviewBinding;
import com.waminiyi.go4lunch.model.Rating;
import com.waminiyi.go4lunch.model.Review;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.viewmodel.ReviewViewModel;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ReviewFragment extends Fragment implements ReviewAdapter.DeleteClickListener {

    private ReviewViewModel reviewViewModel;
    private List<Review> reviewsList = new ArrayList<>();
    private ReviewAdapter reviewAdapter;
    private com.waminiyi.go4lunch.databinding.FragmentReviewBinding binding;
    private Rating currentRating;
    private Review currentUserReview;
    private UserViewModel userViewModel;
    private UserEntity currentUser;
    private String restaurantId;
    private String restaurantName;
    private static final String CURRENT_USER = "currentUser";
    private static final String RESTAURANT_NAME = "restaurantName";
    private static final String RESTAURANT_ID = "restaurantId";


    public ReviewFragment() {
        // Required empty public constructor
    }

    public static ReviewFragment newInstance(String restaurantId, String restaurantName,
                                             UserEntity userEntity) {
        ReviewFragment fragment = new ReviewFragment();
        Bundle args = new Bundle();
        args.putString(RESTAURANT_ID, restaurantId);
        args.putString(RESTAURANT_NAME, restaurantName);
        args.putParcelable(CURRENT_USER, userEntity);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentUser = getArguments().getParcelable(CURRENT_USER);
            restaurantId = getArguments().getString(RESTAURANT_ID);
            restaurantName = getArguments().getString(RESTAURANT_NAME);
        }
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        reviewViewModel = new ViewModelProvider(requireActivity()).get(ReviewViewModel.class);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentReviewBinding.inflate(inflater, container, false);
        reviewAdapter = new ReviewAdapter(reviewsList, this, currentUser.getuId());
        binding.reviewRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.reviewRecyclerview.setAdapter(reviewAdapter);

        observeData();

        return binding.getRoot();
    }

    private void observeData() {

        userViewModel.getCurrentUserData().observe(getViewLifecycleOwner(), userEntity ->
                currentUser = userEntity);

        reviewViewModel.getCurrentUserReview().observe(getViewLifecycleOwner(), review -> {
            currentUserReview = review;
            updateAddReviewView();
        });

        reviewViewModel.getCurrentRestaurantRating().observe(getViewLifecycleOwner(), rating -> {
            currentRating = rating;
            updateRatingView();
        });

        reviewViewModel.getCurrentRestaurantReviews().observe(getViewLifecycleOwner(), reviews -> {
            reviewsList = reviews;
            updateReviews();
        });
    }

    private void updateRatingView() {
        if (currentRating != null && currentRating.getRatingCount() != 0) {
            binding.restaurantRatingAverage.setVisibility(View.VISIBLE);
            int count = currentRating.getRatingCount();
            String ratingCount = getResources().getQuantityString(R.plurals.numberOfReviews,
                    count, count);

            binding.restaurantRatingCount.setText(ratingCount);

            float ratingAverage =
                    BigDecimal.valueOf(((float) currentRating.getRatingSum()) / ((float) currentRating.getRatingCount())).setScale(1,
                            RoundingMode.HALF_UP).floatValue();

            binding.restaurantRatingAverage.setText(String.valueOf(ratingAverage));
            binding.restaurantDetailsRating.setRating(ratingAverage);

        } else {
            binding.restaurantRatingCount.setText(R.string.no_review);
            binding.restaurantRatingAverage.setVisibility(View.GONE);
            binding.restaurantDetailsRating.setRating(0);
        }
    }

    private void updateReviews() {
        if (reviewsList.size() == 0) {
            binding.reviewRecyclerview.setVisibility(View.GONE);
        } else {
            int i = reviewsList.indexOf(currentUserReview);
            if (i != -1)
                Collections.swap(reviewsList, i, 0);

            binding.reviewRecyclerview.setVisibility(View.VISIBLE);
            reviewAdapter.updateReviews(reviewsList);
        }
    }

    private void updateAddReviewView() {
        if (currentUserReview != null) {
            binding.addReview.setVisibility(View.GONE);
        } else {
            binding.addReview.setVisibility(View.VISIBLE);
            binding.saveReviewButton.setVisibility(View.GONE);
            binding.addReviewRating.setRating(0);
            binding.addReviewItemContent.setText("");
            binding.addReviewRating.setOnRatingBarChangeListener((ratingBar, value, b) -> {
                if (value == 0) {
                    binding.saveReviewButton.setVisibility(View.GONE);
                } else {
                    binding.saveReviewButton.setVisibility(View.VISIBLE);
                }
            });

            binding.saveReviewButton.setOnClickListener(view -> {

                String content = binding.addReviewItemContent.getText().toString().trim();
                int rating = (int) binding.addReviewRating.getRating();
                Review review = new Review(currentUser.getuId(),
                        currentUser.getUserName(), currentUser.getUrlPicture(),
                        restaurantId, restaurantName, content, rating);

                reviewViewModel.addUserReview(review);
                binding.addReview.setVisibility(View.GONE);

            });
        }
    }

    @Override
    public void onReviewDelete(Review review) {
        reviewViewModel.deleteUserReview(review);
    }

}