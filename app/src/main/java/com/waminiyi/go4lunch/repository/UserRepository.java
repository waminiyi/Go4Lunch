package com.waminiyi.go4lunch.repository;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.waminiyi.go4lunch.model.User;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.model.UsersSnippet;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class UserRepository {
    private static volatile UserRepository instance;
    private static final String USERS_COLLECTION_NAME = "users";
    private static final String SNIPPETS_COLLECTION_NAME = "snippets";
    private static final String USERS_SNIPPET_DOCUMENT_NAME = "users";
    private static final String ALL_USERS_FIELD = "all";

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized (UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    @Nullable
    public String getCurrentUserUID() {
        return Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    }

    private CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(USERS_COLLECTION_NAME);
    }

    private CollectionReference getSnippetCollection() {
        return FirebaseFirestore.getInstance().collection(SNIPPETS_COLLECTION_NAME);
    }

    // Create UserEntity in Firestore
    public void createNewUser() {
        FirebaseUser user = getCurrentUser();
        if (user != null) {

            Task<DocumentSnapshot> userData = getUserData();
            // If the user doesn't already exist in Firestore, we create it
            userData.addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (!document.exists()) {
                        String uid = user.getUid();
                        String username = user.getDisplayName();
                        String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
                        String userEmail = (user.getEmail() != null) ? user.getEmail() : null;
                        String userPhone = (user.getPhoneNumber() != null) ? user.getPhoneNumber() : null;

                        UserEntity userEntityToCreate = new UserEntity(uid, username, userEmail, userPhone, urlPicture);
                        getUsersCollection().document(uid).set(userEntityToCreate);
                        addUserDataToSnippet(userEntityToCreate);
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            });
        }
    }

    public Task<DocumentSnapshot> getUserData() {
        String uid = this.getCurrentUserUID();
        if (uid != null) {
            return this.getUsersCollection().document(uid).get();
        } else {
            return null;
        }
    }

    public Task<DocumentSnapshot> getAllUserDataSnippet() {
        return this.getSnippetCollection().document(USERS_SNIPPET_DOCUMENT_NAME).get();
    }


    public void addUserDataToSnippet(UserEntity userEntity) {
        getAllUserDataSnippet().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            if(document.exists()){
                List<User> users= Objects.requireNonNull(document.toObject(UsersSnippet.class)).all;
                if (users != null) {
                    users.add(userEntity.toUser());
                }
                getSnippetCollection().document(USERS_SNIPPET_DOCUMENT_NAME).update(ALL_USERS_FIELD, users);
            }else{
                Map<String, Object> data=new HashMap<>();
                data.put(ALL_USERS_FIELD, Collections.singletonList(userEntity.toUser()));
                getSnippetCollection().document(USERS_SNIPPET_DOCUMENT_NAME).set(data);
            }
        });
    }
}
