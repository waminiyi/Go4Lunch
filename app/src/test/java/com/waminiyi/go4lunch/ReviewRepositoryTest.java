package com.waminiyi.go4lunch;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Rating;
import com.waminiyi.go4lunch.model.Review;
import com.waminiyi.go4lunch.repository.ReviewRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(JUnit4.class)
public class ReviewRepositoryTest {

    FirebaseHelper mockedHelper = mock(FirebaseHelper.class);
    ReviewRepository reviewRepo = new ReviewRepository(mockedHelper);
    Review review = new Review("id", "James", "url.com", "rid", "This is a review", 3);
    Review review2 = new Review("id2", "Bond", "url2.com", "rid", "This is another review", 2);
    Rating rating = new Rating(6, 2);
    DocumentSnapshot mockedRatingDoc = mock(DocumentSnapshot.class);
    DocumentSnapshot mockedReviewDoc = mock(DocumentSnapshot.class);
    Map<String, Object> reviewData = new HashMap<>();


    @Rule
    public InstantTaskExecutorRule taskRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        reviewData.put("id", review);
        reviewData.put("id2", review2);


        when(mockedRatingDoc.get("rid", Rating.class)).thenReturn(rating);
        when(mockedReviewDoc.getData()).thenReturn(reviewData);
        when(mockedReviewDoc.get("id", Review.class)).thenReturn(review);
        when(mockedReviewDoc.get("id2", Review.class)).thenReturn(review2);
        when(mockedHelper.getCurrentUserUID()).thenReturn("id");
    }


    /**
     * Verify that the addUserReview method of ReviewRepository calls the FirebaseHelper
     * addUserReview method
     */
    @Test
    public void addUserReviewTest() {
        reviewRepo.addUserReview(review);
        verify(mockedHelper).addUserReview(review);
    }

    /**
     * Verify that the deleteUserReview method of ReviewRepository calls the FirebaseHelper
     * deleteUserReview method
     */
    @Test
    public void deleteUserReview() {
        reviewRepo.deleteUserReview(review);
        verify(mockedHelper).deleteUserReview(review);
    }


    /**
     * Verify that the  parseReviewsDoc method of ReviewRepository parse the document into
     * Review and that the getCurrentRestaurantReviews method return the parsed data
     */

    @Test
    public void parseReviewsDocTest() {
        List<Review> reviews = reviewRepo.getCurrentRestaurantReviews().getValue();
        assertNull(reviews);

        reviewRepo.parseReviewsDoc(mockedReviewDoc);
        reviews = reviewRepo.getCurrentRestaurantReviews().getValue();

        assertNotNull(reviews);
        assertEquals("id2", reviews.get(0).getUserId());
        assertEquals(2, reviews.get(0).getRating());

        assertEquals("id", reviews.get(1).getUserId());
        assertEquals(3, reviews.get(1).getRating());

    }

    /**
     * Verify that the  getCurrentUserReview method of ReviewRepository return the current user's
     * review
     */

    @Test
    public void getCurrentUserReviewTest() {

        Review currentReview = reviewRepo.getCurrentUserReview().getValue();
        assertNull(currentReview);

        reviewRepo.parseReviewsDoc(mockedReviewDoc);
        currentReview = reviewRepo.getCurrentUserReview().getValue();

        assertNotNull(currentReview);
        assertEquals(mockedHelper.getCurrentUserUID(), currentReview.getUserId());
    }


    /**
     * Verify that the  parseRatingsDoc method of ReviewRepository parse the document into
     * Rating and that the getCurrentRestaurantRating method return the parsed data
     */

    @Test
    public void parseRatingsDocTest() {
        Rating currentRating = reviewRepo.getCurrentRestaurantRating().getValue();
        assertNull(currentRating);

        reviewRepo.parseRatingsDoc("rid", mockedRatingDoc);

        currentRating = reviewRepo.getCurrentRestaurantRating().getValue();

        assertNotNull(currentRating);
        assertEquals(rating.getRatingSum(), currentRating.getRatingSum());
        assertEquals(rating.getRatingCount(), currentRating.getRatingCount());

    }

}
