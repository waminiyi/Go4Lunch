package com.waminiyi.go4lunch.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Rating;
import com.waminiyi.go4lunch.model.Review;
import com.waminiyi.go4lunch.util.SnapshotListener;

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

    public void getAllReviewsFromDb(String restaurantId) {
        firebaseHelper.getRestaurantReviews(restaurantId).addOnSuccessListener(documentSnapshot -> {

            Map<String, Object> map = documentSnapshot.getData();
            List<Review> reviews = new ArrayList<>();
            if (map != null && map.size() != 0) {
                for (Map.Entry<String, Object> entry : map.entrySet()) {
                    Review review = documentSnapshot.get(entry.getKey(), Review.class);
                    if (review != null) {

                        reviews.add(review);
                        usersReviews.postValue(reviews);
                    }
                }
            } else {
                usersReviews.postValue(reviews);
            }
        });
    }

    public void getCurrentUserReviewFromDb(String restaurantId) {
        firebaseHelper.getRestaurantReviews(restaurantId).addOnSuccessListener(documentSnapshot -> {
            Review review = documentSnapshot.get(Objects.requireNonNull(firebaseHelper.getCurrentUserUID()), Review.class);
            currentUserReview.postValue(review);
        });
    }


    public LiveData<List<Review>> getAllReviews() {
        return usersReviews;
    }

    public void getCurrentRestaurantRatingFromDb(String restaurantId) {
        firebaseHelper.getRestaurantNotes().addOnSuccessListener(documentSnapshot -> {
            Rating rating = documentSnapshot.get(restaurantId, Rating.class);
            currentRestaurantRating.postValue(rating);
        });
    }

    public LiveData<Rating> getCurrentRestaurantRating() {
        return currentRestaurantRating;
    }

    public void setListener(SnapshotListener listener) {
        firebaseHelper.setListener(listener);
    }

    public void listenToRestaurantReviews(String restaurantId) {
        firebaseHelper.listenToRestaurantReviews(restaurantId);
    }

    public void removeRestaurantReviewsListener(String restaurantId) {
        firebaseHelper.removeRestaurantReviewsListener(restaurantId);
    }

}
