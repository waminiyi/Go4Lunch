package com.waminiyi.go4lunch.util;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.model.Restaurant;

public interface SnapshotListener {
//
    void onUsersUpdate(DocumentSnapshot userDoc);
//
    void onRatingsUpdate(DocumentSnapshot ratingsDoc);

    void onLunchesUpdate(DocumentSnapshot lunchesDoc);

    void onCurrentUserUpdate(DocumentSnapshot userDoc);

    void onUsersSnippetUpdate(DocumentSnapshot userSnippetDoc);

    void onReviewsUpdate(DocumentSnapshot reviewsDoc);

}
