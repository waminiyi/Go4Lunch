package com.waminiyi.go4lunch.repository;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.UserEntity;

import javax.inject.Inject;

public class UserRepository {
    /**
     * FirebaseHelper instance
     */
    private final FirebaseHelper firebaseHelper;

    /**
     * MutableLivedata holding current UserEntity object
     */
    public MutableLiveData<UserEntity> currentUserEntity = new MutableLiveData<>();

    @Inject
    public UserRepository(FirebaseHelper firebaseHelper) {
        this.firebaseHelper = firebaseHelper;
    }


    /**
     * Get the current connected user as FirebaseUser
     *
     * @return FirebaseUser
     */
    public FirebaseUser getCurrentUser() {
        return firebaseHelper.getCurrentUser();
    }

    /**
     * indicates if the current user is logged in
     */
    public Boolean isCurrentUserLogged() {
        return (this.getCurrentUser() != null);
    }

    /**
     * Create a new UserEntity object in the firestore database
     *
     * @param user: FirebaseUser
     */
    public void createNewUserInDatabase(@NonNull FirebaseUser user) {
        firebaseHelper.createNewUserInDatabase(user);
    }

    /**
     * @return LiveData<UserEntity>
     */
    public LiveData<UserEntity> getCurrentUserData() {
        return currentUserEntity;
    }

    /**
     * parse the provided DocumentSnapshot into UserEntity object*
     *
     * @param userDoc: DocumentSnapshot
     */
    public void parseCurrentUserDoc(DocumentSnapshot userDoc) {
        UserEntity userEntity = userDoc.toObject(UserEntity.class);
        currentUserEntity.postValue(userEntity);
    }

    /**
     * Log out the current user from firebase
     */
    public void logOut() {
        firebaseHelper.logOut();
    }

    /**
     * @return String: current connected user's unique ID
     */
    @Nullable
    public String getCurrentUserUID() {
        return firebaseHelper.getCurrentUserUID();
    }

    /**
     * Set listener for current user Document and userSnippet Document
     *
     * @param listener: FirebaseHelper.UserListener
     */
    public void setUserListener(FirebaseHelper.UserListener listener) {
        firebaseHelper.setUserListener(listener);
    }

    /**
     * listen to changes on the current user document and fire FirebaseHelper.UserListener when
     * there is an event
     */
    public void listenToCurrentUserDoc() {
        firebaseHelper.listenToCurrentUserDoc();
    }

    /**
     * listen to changes on the userSnippet document and fire FirebaseHelper.UserListener when
     * there is an event
     */
    public void listenToUsersSnippet() {
        firebaseHelper.listenToUsersSnippet();
    }

    /**
     * add the restaurantId to the user's favorites restaurants list
     *
     * @param restaurantId: String representing the unique ID of the restaurant
     */
    public void addRestaurantToUserFavorite(String restaurantId) {
        firebaseHelper.addRestaurantToUserFavorite(restaurantId);
    }

    /**
     * remove the restaurantId from the user's favorites restaurants list
     *
     * @param restaurantId: String representing the unique ID of the restaurant
     */
    public void removeRestaurantFromUserFavorite(String restaurantId) {
        firebaseHelper.removeRestaurantFromUserFavorite(restaurantId);
    }

    public void updateProfile(UserProfileChangeRequest profileUpdates) {

        firebaseHelper.updateProfile(profileUpdates);
    }

    public void updateUserName(String name) {
        firebaseHelper.updateUserName(name);
    }

    public void updateUserPic(String pictureUrl) {
        firebaseHelper.updateUserPic(pictureUrl);
    }

    public void updateUserTeam(String team) {
        firebaseHelper.updateUserTeam(team);
    }


}
