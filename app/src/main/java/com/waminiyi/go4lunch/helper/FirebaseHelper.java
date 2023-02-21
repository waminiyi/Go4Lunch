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
import com.waminiyi.go4lunch.model.Review;
import com.waminiyi.go4lunch.model.UserEntity;

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
    private final DocumentReference lunchesCountDocRef;
    private UserListener userListener;
    private LunchListener lunchListener;
    private ReviewListener reviewListener;

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
        this.LUNCH_COUNT = DATE + "_lunch-count";
        this.lunchesDocRef = lunchesCollectionRef.document(DATE);
        this.lunchesCountDocRef = lunchesCollectionRef.document(LUNCH_COUNT);


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

//        usersIdSnippetDocRef.update(ALL_USERS_FIELD, FieldValue.arrayUnion(userEntity.getuId()));
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


    public void setCurrentUserLunch(Lunch lunch) {

        Map<String, Lunch> lunchUpdate = new HashMap<>();
        lunchUpdate.put(lunch.getUserId(), lunch);
        lunchesCollectionRef.document(DATE).set(lunchUpdate, SetOptions.merge());

        Map<String, FieldValue> countUpdate = new HashMap<>();
        countUpdate.put(lunch.getRestaurantId(), FieldValue.increment(1));

        lunchesCollectionRef.document(LUNCH_COUNT).set(countUpdate,
                SetOptions.merge());

    }


    public void deleteCurrentUserLunch(Lunch lunch) {

        Map<String, Object> updates = new HashMap<>();
        updates.put(lunch.getUserId(), FieldValue.delete());

        lunchesCollectionRef.document(DATE).update(updates);
        lunchesCollectionRef.document(LUNCH_COUNT).update(lunch.getRestaurantId(),
                FieldValue.increment(-1));
    }


    public Task<DocumentSnapshot> retrieveAllUsersFromDb() {
        return usersSnippetDocRef.get();
    }

    public Task<DocumentSnapshot> getLunchesCount() {
        return lunchesCollectionRef.document(LUNCH_COUNT).get();
    }


    private void removeLunchFromCount(String restaurantId) {
        lunchesCollectionRef.document(LUNCH_COUNT).update(restaurantId,
                FieldValue.increment(-1));
    }

    public void setUserListener(UserListener listener) {
        this.userListener = listener;
    }

    public void setLunchListener(LunchListener listener) {
        this.lunchListener = listener;
    }

    public void setReviewListener(ReviewListener listener) {
        this.reviewListener = listener;
    }


    private final EventListener<DocumentSnapshot> lunchesSnapShotListener =
            (value, error) -> {
                if (error != null) {
                    return;
                }

                lunchListener.onLunchesUpdate(value);

            };
    private final EventListener<DocumentSnapshot> lunchesCountSnapShotListener =
            (value, error) -> {
                if (error != null) {
                    return;
                }

                lunchListener.onLunchesCountUpdate(value);

            };

    private final EventListener<DocumentSnapshot> ratingsSnapShotListener =
            (value, error) -> {
                if (error != null) {
                    return;
                }
                reviewListener.onRatingsUpdate(value);

            };
    private final EventListener<DocumentSnapshot> currentUserSnapShotListener =
            (value, error) -> {
                if (error != null) {
                    return;
                }
                userListener.onCurrentUserUpdate(value);

            };

    private final EventListener<DocumentSnapshot> usersSnapShotListener =
            (value, error) -> {
                if (error != null) {
                    return;
                }
                userListener.onUsersSnippetUpdate(value);

            };

    private final EventListener<DocumentSnapshot> reviewsSnapShotListener =
            (value, error) -> {
                if (error != null) {
                    return;
                }
                reviewListener.onReviewsUpdate();

            };

    public void listenToLunches() {
        lunchesDocRef.addSnapshotListener(lunchesSnapShotListener);
    }

    public void listenToLunchesCount() {
        lunchesCountDocRef.addSnapshotListener(lunchesCountSnapShotListener);
    }

    public void listenToRatings() {
        restaurantRatingsRef.addSnapshotListener(ratingsSnapShotListener);
    }

    public void listenToCurrentUserDoc() {
        usersCollectionRef.document(Objects.requireNonNull(getCurrentUserUID())).addSnapshotListener(currentUserSnapShotListener);
    }

    public void listenToUsersSnippet() {
        usersSnippetDocRef.addSnapshotListener(usersSnapShotListener);
    }

    public void listenToRestaurantReviews(String restaurantId) {
        reviewsCollectionRef.document(restaurantId).addSnapshotListener(reviewsSnapShotListener);
    }

    public void removeRestaurantReviewsListener(String restaurantId) {
        reviewsCollectionRef.document(restaurantId).addSnapshotListener(reviewsSnapShotListener).remove();
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

    public void addRestaurantToUserFavorite(String restaurantId) {
        usersCollectionRef.document(Objects.requireNonNull(getCurrentUserUID())).update("favoriteRestaurant",
                FieldValue.arrayUnion(restaurantId));
    }

    public void removeRestaurantFromUserFavorite(String restaurantId) {
        usersCollectionRef.document(Objects.requireNonNull(getCurrentUserUID())).update("favoriteRestaurant",
                FieldValue.arrayRemove(restaurantId));
    }


    public interface ReviewListener {
        void onRatingsUpdate(DocumentSnapshot ratingsDoc);

        void onReviewsUpdate();
    }

    public interface UserListener {
        void onCurrentUserUpdate(DocumentSnapshot userDoc);

        void onUsersSnippetUpdate(DocumentSnapshot userSnippetDoc);
    }

    public interface LunchListener {
        void onLunchesUpdate(DocumentSnapshot lunchesDoc);

        void onLunchesCountUpdate(DocumentSnapshot lunchesCountDoc);

    }

}
