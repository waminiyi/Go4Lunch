package com.waminiyi.go4lunch.helper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.model.Review;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.util.SnapshotListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

public class FirebaseHelper {
    private static final String ALL_USERS_FIELD = "all";
    private final FirebaseAuth firebaseAuth;
    private final CollectionReference usersCollectionRef;
    private final DocumentReference usersSnippetDocRef;
    private final DocumentReference restaurantRatingsRef;
    private final CollectionReference lunchesCollectionRef;
    private final CollectionReference reviewsCollectionRef;
    private final DocumentReference usersIdSnippetDocRef;
    private final DocumentReference lunchesDocRef;
    private SnapshotListener listener;

    private final String DATE;
    private final String LUNCH_COUNT;

    @Inject
    public FirebaseHelper(FirebaseAuth firebaseAuth, FirebaseFirestore database) {
        this.firebaseAuth = firebaseAuth;
        this.usersCollectionRef = database.collection("users");
        this.usersSnippetDocRef = database.collection("snippets").document("allUsers");
        this.restaurantRatingsRef =
                database.collection("restaurants").document("restaurantRatings");
        this.lunchesCollectionRef = database.collection("lunches");
        this.usersIdSnippetDocRef = database.collection("snippets").document("usersId");
        this.reviewsCollectionRef = database.collection("reviews");
        this.DATE = getDate();
        LUNCH_COUNT = DATE + "_lunch-count";
        this.lunchesDocRef = lunchesCollectionRef.document(DATE);

    }

    private String getDate() {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        return df.format(c);
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    @Nullable
    public String getCurrentUserUID() {
        return Objects.requireNonNull(getCurrentUser()).getUid();
    }

    public Task<DocumentSnapshot> getCurrentUserDoc() {
        return usersCollectionRef.document(Objects.requireNonNull(getCurrentUserUID())).get();
    }


    public void createNewUserInDatabase(@NonNull FirebaseUser user) {

        String uid = user.getUid();
        String username = user.getDisplayName();
        String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
        String userEmail = (user.getEmail() != null) ? user.getEmail() : null;
        String userPhone = (user.getPhoneNumber() != null) ? user.getPhoneNumber() : null;

        UserEntity userEntityToCreate =
                new UserEntity(uid, username, userEmail, userPhone, urlPicture);
        usersCollectionRef.document(uid).set(userEntityToCreate);
        addUserDataToSnippet(userEntityToCreate);

    }

    public void addUserDataToSnippet(@NonNull UserEntity userEntity) {

        usersIdSnippetDocRef.update(ALL_USERS_FIELD, FieldValue.arrayUnion(userEntity.getuId()));
        usersSnippetDocRef.update(userEntity.getuId(), userEntity.toUser());
    }

    public void logOut() {
        firebaseAuth.signOut();
    }

    public Task<DocumentSnapshot> getRestaurantNotes() {
        return restaurantRatingsRef.get();
    }

    public Task<DocumentSnapshot> getLunches() {
        return lunchesCollectionRef.document(DATE).get();
    }


    public void setCurrentUserLunch(Lunch lunch, Restaurant restaurant) {

        Map<String, Lunch> update = new HashMap<>();
        update.put(Objects.requireNonNull(getCurrentUserUID()), lunch);
        lunchesCollectionRef.document(DATE).set(update, SetOptions.merge());

        addLunchToCount(restaurant);
    }

    public void deleteCurrentUserLunch(String userId, String restaurantId) {

        Map<String, Object> updates = new HashMap<>();
        updates.put(userId, FieldValue.delete());

        lunchesCollectionRef.document(DATE).update(updates);
        removeLunchFromCount(restaurantId);
    }


    public Task<DocumentSnapshot> retrieveAllUsersFromDb() {
        return usersSnippetDocRef.get();
    }

    public Task<DocumentSnapshot> getLunchesCount() {
        return lunchesCollectionRef.document(LUNCH_COUNT).get();
    }

    private void addLunchToCount(Restaurant restaurant) {
        Map<String, Integer> update = new HashMap<>();
        update.put(restaurant.getId(), restaurant.getLunchCount() + 1);
        lunchesCollectionRef.document(LUNCH_COUNT).set(update, SetOptions.merge());
    }

    private void removeLunchFromCount(String restaurantId) {
        lunchesCollectionRef.document(LUNCH_COUNT).update(restaurantId,
                FieldValue.increment(-1));
    }


    public void setListener(SnapshotListener listener) {
        this.listener = listener;
    }

    private final EventListener<DocumentSnapshot> lunchesListener =
            (value, error) -> {
                if (error != null) {
                    return;
                }

                listener.onLunchesUpdate(value);

            };

    private final EventListener<DocumentSnapshot> ratingsListener =
            (value, error) -> {
                if (error != null) {
                    return;
                }
                listener.onRatingsUpdate(value);

            };
    private final EventListener<DocumentSnapshot> currentUserListener =
            (value, error) -> {
                if (error != null) {
                    return;
                }
                listener.onCurrentUserUpdate(value);

            };

    private final EventListener<DocumentSnapshot> usersListener =
            (value, error) -> {
                if (error != null) {
                    return;
                }
                listener.onUsersSnippetUpdate(value);

            };

    private final EventListener<DocumentSnapshot> reviewsListener =
            (value, error) -> {
                if (error != null) {
                    return;
                }
                listener.onReviewsUpdate(value);

            };

    public void listenToLunches() {
        lunchesDocRef.addSnapshotListener(lunchesListener);
    }

    public void listenToRatings() {
        restaurantRatingsRef.addSnapshotListener(ratingsListener);
    }

    public void listenToCurrentUserDoc() {
        usersCollectionRef.document(Objects.requireNonNull(getCurrentUserUID())).addSnapshotListener(currentUserListener);
    }

    public void listenToUsersSnippet() {
        usersIdSnippetDocRef.addSnapshotListener(usersListener);
    }

    public void listenToRestaurantReviews(String restaurantId) {
        reviewsCollectionRef.document(restaurantId).addSnapshotListener(reviewsListener);
    }

    public void removeRestaurantReviewsListener(String restaurantId) {
        reviewsCollectionRef.document(restaurantId).addSnapshotListener(reviewsListener).remove();
    }

    public Task<DocumentSnapshot> getRestaurantReviews(String restaurantId) {
        return reviewsCollectionRef.document(restaurantId).get();
    }

    public void addUserReview(Review review) {
        Map<String, Review> update = new HashMap<>();
        update.put(review.getUserId(), review);
        reviewsCollectionRef.document(review.getRestaurantId()).set(update, SetOptions.merge());
        restaurantRatingsRef.update(
                review.getRestaurantId() + ".ratingCount", FieldValue.increment(1),
                review.getRestaurantId() + ".ratingSum", FieldValue.increment(review.getRating())
        );
    }

    public void deleteUserReview(Review review) {
        Map<String, Object> updates = new HashMap<>();
        updates.put(review.getUserId(), FieldValue.delete());
        reviewsCollectionRef.document(review.getRestaurantId()).update(updates);
        restaurantRatingsRef.update(
                review.getRestaurantId() + ".ratingCount", FieldValue.increment(-1),
                review.getRestaurantId() + ".ratingSum", FieldValue.increment(-review.getRating())
        );

    }

    public void addNewRatingToRestaurantDoc(Review review) {
        restaurantRatingsRef.update(
                review.getRestaurantId() + ".ratingCount", FieldValue.increment(1),
                review.getRestaurantId() + ".ratingSum", FieldValue.increment(review.getRating())
        );
    }

    public void updateRestaurantRatingSum(String restaurantId, int oldRating, int newRating) {
        restaurantRatingsRef.update(restaurantId + ".ratingSum", FieldValue.increment(newRating - oldRating)
        );
    }

    private void removeRatingFromRestaurantDoc(Review review) {
        restaurantRatingsRef.update(
                review.getRestaurantId() + ".ratingCount", FieldValue.increment(-1),
                review.getRestaurantId() + ".ratingSum", FieldValue.increment(-review.getRating())
        );
    }

}
