package com.waminiyi.go4lunch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.model.Rating;
import com.waminiyi.go4lunch.model.Review;
import com.waminiyi.go4lunch.repository.ReviewRepository;
import com.waminiyi.go4lunch.viewmodel.ReviewViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Objects;

@RunWith(JUnit4.class)
public class ReviewViewModelTest {
    ReviewRepository mockedReviewRepo = mock(ReviewRepository.class);
    ReviewViewModel reviewVM = new ReviewViewModel(mockedReviewRepo);

    Review review = new Review("id", "James", "url.com", "rid", "This is a review", 3);
    Review review2 = new Review("id2", "Bond", "url2.com", "rid", "This is another review", 2);
    Rating rating = new Rating(6, 2);
    DocumentSnapshot mockedRatingDoc = mock(DocumentSnapshot.class);
    DocumentSnapshot mockedReviewDoc = mock(DocumentSnapshot.class);


    @Rule
    public InstantTaskExecutorRule taskRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {

        when(mockedReviewRepo.getCurrentUserReview()).thenReturn(new MutableLiveData<>(review));
        when(mockedReviewRepo.getCurrentRestaurantRating()).thenReturn(new MutableLiveData<>(rating));
        when(mockedReviewRepo.getCurrentRestaurantReviews()).thenReturn(new MutableLiveData<>(Arrays.asList(review, review2)));
    }


    /**
     * Verify that the addUserReview method of ReviewViewModel calls the ReviewRepository
     * addUserReview method
     */
    @Test
    public void addUserReviewTest() {
        reviewVM.addUserReview(review);
        verify(mockedReviewRepo).addUserReview(review);
    }

    /**
     * Verify that the deleteUserReview method of ReviewViewModel calls the ReviewRepository's
     * deleteUserReview method
     */
    @Test
    public void deleteUserReview() {
        reviewVM.deleteUserReview(review);
        verify(mockedReviewRepo).deleteUserReview(review);
    }


    /**
     * Verify that the parseReviewsDoc method of ReviewViewModel calls the ReviewRepository's
     * parseReviewsDoc method
     */
    @Test
    public void parseReviewsDocTest() {
        reviewVM.parseReviewsDoc(mockedReviewDoc);
        verify(mockedReviewRepo).parseReviewsDoc(mockedReviewDoc);
    }

    /**
     * Verify that the getCurrentRestaurantReview method of ReviewViewModel returns the result of
     * ReviewRepository's getCurrentRestaurantReview method
     */
    @Test
    public void getCurrentRestaurantReviewTest() {
        assertEquals(Objects.requireNonNull(mockedReviewRepo.getCurrentRestaurantReviews().getValue()).size(),
                Objects.requireNonNull(reviewVM.getCurrentRestaurantReviews().getValue()).size());

        assertEquals(Objects.requireNonNull(mockedReviewRepo.getCurrentRestaurantReviews().getValue()).get(0).getRestaurantId(),
                Objects.requireNonNull(reviewVM.getCurrentRestaurantReviews().getValue()).get(0).getRestaurantId());
    }


    /**
     * Verify that the getCurrentUserReview method of ReviewViewModel returns the result of
     * ReviewRepository's getCurrentUserReview method
     */

    @Test
    public void getCurrentUserReviewTest() {

        assertEquals(Objects.requireNonNull(mockedReviewRepo.getCurrentUserReview().getValue()).getUserId(),
                Objects.requireNonNull(reviewVM.getCurrentUserReview().getValue()).getUserId());
    }


    /**
     * Verify that the  parseRatingsDoc method of ReviewViewModel calls the ReviewRepository's
     * parseRatingsDoc method
     */

    @Test
    public void parseRatingsDocTest() {
        reviewVM.parseRatingsDoc("rid", mockedRatingDoc);
        verify(mockedReviewRepo).parseRatingsDoc("rid", mockedRatingDoc);
    }

    /**
     * Verify that the getCurrentRestaurantRating method of ReviewViewModel returns the result of
     * ReviewRepository's getCurrentRestaurantRating method
     */

    @Test
    public void getCurrentRestaurantRatingTest() {

        assertEquals(Objects.requireNonNull(mockedReviewRepo.getCurrentRestaurantRating().getValue()).getRatingSum(),
                Objects.requireNonNull(reviewVM.getCurrentRestaurantRating().getValue()).getRatingSum());

        assertEquals(Objects.requireNonNull(mockedReviewRepo.getCurrentRestaurantRating().getValue()).getRatingCount(),
                Objects.requireNonNull(reviewVM.getCurrentRestaurantRating().getValue()).getRatingCount());
    }

}
