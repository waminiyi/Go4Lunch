package com.waminiyi.go4lunch.helper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.model.User;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.model.UserMap;
import com.waminiyi.go4lunch.util.SnapshotListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

public class FirebaseHelper {
    private static final String ALL_USERS_FIELD = "all";
    private final FirebaseAuth firebaseAuth;
    private final CollectionReference usersCollectionRef;
    private final DocumentReference usersSnippetDocRef;
    private final DocumentReference restaurantNotesRef;
    private final CollectionReference lunchesCollectionRef;
    private final DocumentReference usersIdSnippetDocRef;
    private SnapshotListener listener;

    private UserMap usersMap;


    private final String DATE;
    private final String LUNCH_COUNT;
    private ArrayList<String> usersId;

    @Inject
    public FirebaseHelper(FirebaseAuth firebaseAuth, FirebaseFirestore database) {
        this.firebaseAuth = firebaseAuth;
        this.usersCollectionRef = database.collection("users");
        this.usersSnippetDocRef = database.collection("snippets").document("allUsers");
        this.restaurantNotesRef = database.collection("restaurants").document("restaurantNotes");
        this.lunchesCollectionRef = database.collection("lunches");
        this.usersIdSnippetDocRef = database.collection("snippets").document("usersId");
        this.DATE = getDate();
        LUNCH_COUNT = DATE + "_lunch-count";
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


    public void createNewUser(@NonNull FirebaseUser user) {

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
        return restaurantNotesRef.get();
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

    private void removeLunchFromCount(String  restaurantId) {
        lunchesCollectionRef.document(LUNCH_COUNT).update(restaurantId,
                FieldValue.increment(-1));
    }

    public Task<DocumentSnapshot> retrieveAllUsersIdFromDb() {
        return usersIdSnippetDocRef.get();
    }

    public ArrayList<String> getAllUsersId() {
        return usersId;
    }

    public UserMap getUsersMap() {
        return usersMap;
    }




    public void setListener(SnapshotListener listener){
        this.listener=listener;
    }


}
