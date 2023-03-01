package com.waminiyi.go4lunch.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Rating;
import com.waminiyi.go4lunch.model.Review;
import com.waminiyi.go4lunch.repository.ReviewRepository;

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

    public void parseReviewsDoc(DocumentSnapshot reviewsDoc) {
        reviewRepository.parseReviewsDoc(reviewsDoc);
    }

    public LiveData<List<Review>> getCurrentRestaurantReviews() {
        return reviewRepository.getCurrentRestaurantReviews();
    }

    public void parseRatingsDoc(String restaurantId, DocumentSnapshot ratingDoc) {
        reviewRepository.parseRatingsDoc(restaurantId,ratingDoc);
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
