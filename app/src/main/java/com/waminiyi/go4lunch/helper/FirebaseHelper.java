package com.waminiyi.go4lunch.helper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.waminiyi.go4lunch.model.UserEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class FirebaseHelper {
    private static volatile FirebaseHelper instance;
    private static final String ALL_USERS_FIELD = "all";
    private final FirebaseFirestore database = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final CollectionReference usersCollectionRef = database.collection("users");
    private final DocumentReference usersSnippetDocRef = database.collection("snippets").document("users");

    private FirebaseHelper() {
    }

    public static FirebaseHelper getInstance() {
        FirebaseHelper result = instance;
        if (result != null) {
            return result;
        }
        synchronized (FirebaseHelper.class) {
            if (instance == null) {
                instance = new FirebaseHelper();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    @Nullable
    public String getCurrentUserUID() {
        return Objects.requireNonNull(getCurrentUser()).getUid();
    }

    public Task<DocumentSnapshot> getCurrentUserData() {
        return usersCollectionRef.document(Objects.requireNonNull(getCurrentUserUID())).get();
    }

    public void createNewUser(@NonNull FirebaseUser user) {

            String uid = user.getUid();
            String username = user.getDisplayName();
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String userEmail = (user.getEmail() != null) ? user.getEmail() : null;
            String userPhone = (user.getPhoneNumber() != null) ? user.getPhoneNumber() : null;

            UserEntity userEntityToCreate = new UserEntity(uid, username, userEmail, userPhone, urlPicture);
            usersCollectionRef.document(uid).set(userEntityToCreate);
            addUserDataToSnippet(userEntityToCreate);

    }

    public void addUserDataToSnippet(UserEntity userEntity) {
        usersSnippetDocRef.get().addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            if (document.exists()) {
                usersSnippetDocRef.update(ALL_USERS_FIELD, FieldValue.arrayUnion(userEntity.toUser()));
            } else {
                Map<String, Object> data = new HashMap<>();
                data.put(ALL_USERS_FIELD, Collections.singletonList(userEntity.toUser()));
                usersSnippetDocRef.set(data);
            }
        });
    }

    public void logOut() {
        mAuth.signOut();
    }
}
