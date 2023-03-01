package com.waminiyi.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Rating;
import com.waminiyi.go4lunch.model.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

public class ReviewRepository {
    private final FirebaseHelper firebaseHelper;
    private final MutableLiveData<List<Review>> usersReviews = new MutableLiveData<>();
    private final MutableLiveData<Review> currentUserReview = new MutableLiveData<>();
    private final MutableLiveData<Rating> currentRestaurantRating = new MutableLiveData<>();

    @Inject
    public ReviewRepository(FirebaseHelper firebaseHelper) {
        this.firebaseHelper = firebaseHelper;
    }

    public void addUserReview(Review review) {
        firebaseHelper.addUserReview(review);
    }

    public void deleteUserReview(Review review) {
        firebaseHelper.deleteUserReview(review);
    }

    public LiveData<Review> getCurrentUserReview() {
        return currentUserReview;
    }

    public void parseReviewsDoc(DocumentSnapshot reviewsDoc) {
        Map<String, Object> map = reviewsDoc.getData();
        List<Review> reviews = new ArrayList<>();
        if (map != null && map.size() != 0) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                Review review = reviewsDoc.get(entry.getKey(), Review.class);
                if (review != null) {
                    reviews.add(review);
                }
            }
        }
        currentUserReview.postValue(reviewsDoc.get(Objects.requireNonNull(firebaseHelper.getCurrentUserUID()), Review.class));
        usersReviews.postValue(reviews);

    }

    public LiveData<List<Review>> getCurrentRestaurantReviews() {
        return usersReviews;
    }

    public void parseRatingsDoc(String restaurantId, DocumentSnapshot ratingDoc) {
        Rating rating = ratingDoc.get(restaurantId, Rating.class);
        currentRestaurantRating.postValue(rating);
    }

    public LiveData<Rating> getCurrentRestaurantRating() {
        return currentRestaurantRating;
    }

    public void setReviewListener(FirebaseHelper.ReviewListener listener) {
        firebaseHelper.setReviewListener(listener);
    }

    public void listenToRatings() {
        firebaseHelper.listenToRatings();
    }

    public void listenToRestaurantReviews(String restaurantId) {
        firebaseHelper.listenToRestaurantReviews(restaurantId);
    }

}
