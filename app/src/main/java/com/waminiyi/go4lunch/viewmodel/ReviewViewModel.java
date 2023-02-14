package com.waminiyi.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Rating;
import com.waminiyi.go4lunch.model.Review;
import com.waminiyi.go4lunch.repository.ReviewRepository;
import com.waminiyi.go4lunch.util.SnapshotListener;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class ReviewViewModel extends ViewModel {

    private final ReviewRepository reviewRepository;

    @Inject
    public ReviewViewModel(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public void addUserReview(Review review) {
        reviewRepository.addUserReview(review);
    }

    public void deleteUserReview(Review review) {
        reviewRepository.deleteUserReview(review);
    }


    public LiveData<Review> getCurrentUserReview() {
        return reviewRepository.getCurrentUserReview();
    }

    public void getAllReviewsFromDb(String restaurantId) {
        reviewRepository.getAllReviewsFromDb(restaurantId);
    }

    public void getCurrentUserReviewFromDb(String restaurantId) {
        reviewRepository.getCurrentUserReviewFromDb(restaurantId);
    }

    public LiveData<List<Review>> getAllReviews() {
        return reviewRepository.getAllReviews();
    }

    public void getCurrentRestaurantRatingFromDb(String restaurantId) {
        reviewRepository.getCurrentRestaurantRatingFromDb(restaurantId);
    }

    public LiveData<Rating> getCurrentRestaurantRating() {
        return reviewRepository.getCurrentRestaurantRating();
    }

       public void setReviewListener(FirebaseHelper.ReviewListener listener) {
        reviewRepository.setReviewListener(listener);
    }
    public void listenToRatings() {
        reviewRepository.listenToRatings();
    }

    public void listenToRestaurantReviews(String restaurantId) {
        reviewRepository.listenToRestaurantReviews(restaurantId);
    }

}
