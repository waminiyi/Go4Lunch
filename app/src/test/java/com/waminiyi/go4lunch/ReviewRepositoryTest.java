//package com.waminiyi.go4lunch;
//
//
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.when;
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
//import androidx.lifecycle.LiveData;
//
//import com.google.firebase.Timestamp;
//import com.google.firebase.firestore.DocumentSnapshot;
//import com.waminiyi.go4lunch.helper.FirebaseHelper;
//import com.waminiyi.go4lunch.model.Lunch;
//import com.waminiyi.go4lunch.model.Rating;
//import com.waminiyi.go4lunch.model.Review;
//import com.waminiyi.go4lunch.model.User;
//import com.waminiyi.go4lunch.repository.ReviewRepository;
//
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.junit.runners.JUnit4;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//
//@RunWith(JUnit4.class)
//public class ReviewRepositoryTest {
//
//    FirebaseHelper mockedHelper = mock(FirebaseHelper.class);
//    ReviewRepository reviewRepo = new ReviewRepository(mockedHelper);
//    Review review = new Review("id", "James", "url.com", "rid", "Good food", "This is a review", 3);
//    Rating rating = new Rating(6, 2);
//    DocumentSnapshot mockedRatingDoc = mock(DocumentSnapshot.class);
//
//
//    @Rule
//    public InstantTaskExecutorRule taskRule = new InstantTaskExecutorRule();
//
//    @Before
//    public void setUp() {
//
//        when(mockedRatingDoc.get("rid", Rating.class)).thenReturn(rating);
//        when(mockedUserSnippetDoc.get(id2, User.class)).thenReturn(user2);
//        when(mockedHelper.getCurrentUserUID()).thenReturn(id);
//        when(mockedLunchDoc.get(id, Lunch.class)).thenReturn(lunch);
//        when(mockedLunchDoc.get(id2, Lunch.class)).thenReturn(lunch2);
//        Timestamp timestamp= Timestamp.now();
//    }
//
//
//    /**
//     * Verify that the addUserReview method of ReviewRepository calls the FirebaseHelper
//     * addUserReview method
//     */
//    @Test
//    public void addUserReviewTest(Review review) {
//        firebaseHelper.addUserReview(review);
//    }
//
//    public void deleteUserReview(Review review) {
//        firebaseHelper.deleteUserReview(review);
//    }
//
//    public LiveData<Review> getCurrentUserReview() {
//        return currentUserReview;
//    }
//
//    public void getCurrentRestaurantReviewsFromDb(String restaurantId) {
//        firebaseHelper.getRestaurantReviews(restaurantId).addOnSuccessListener(documentSnapshot -> {
//
//            Map<String, Object> map = documentSnapshot.getData();
//            List<Review> reviews = new ArrayList<>();
//            if (map != null && map.size() != 0) {
//                for (Map.Entry<String, Object> entry : map.entrySet()) {
//                    Review review = documentSnapshot.get(entry.getKey(), Review.class);
//                    if (review != null) {
//
//                        reviews.add(review);
//                        usersReviews.postValue(reviews);
//                    }
//                }
//            } else {
//                usersReviews.postValue(reviews);
//            }
//        });
//    }
//
////    public void getCurrentUserReviewFromDb(String restaurantId) {
////        firebaseHelper.getRestaurantReviews(restaurantId).addOnSuccessListener(documentSnapshot -> {
////            Review review =
////                    documentSnapshot.get(Objects.requireNonNull(firebaseHelper.getCurrentUserUID()), Review.class);
////            currentUserReview.postValue(review);
////        });
////    }
////
////    public LiveData<List<Review>> getCurrentRestaurantReviews() {
////        return usersReviews;
////    }
////
////    public void parseRatingsDoc(String restaurantId, DocumentSnapshot ratingDoc) {
////        Rating rating = ratingDoc.get(restaurantId, Rating.class);
////        currentRestaurantRating.postValue(rating);
////    }
////
////    public LiveData<Rating> getCurrentRestaurantRating() {
////        return currentRestaurantRating;
////    }
////
////    public void setReviewListener(FirebaseHelper.ReviewListener listener) {
////        firebaseHelper.setReviewListener(listener);
////    }
////
////    public void listenToRatings() {
////        firebaseHelper.listenToRatings();
////    }
////
////    public void listenToRestaurantReviews(String restaurantId) {
////        firebaseHelper.listenToRestaurantReviews(restaurantId);
////    }
//
//}
