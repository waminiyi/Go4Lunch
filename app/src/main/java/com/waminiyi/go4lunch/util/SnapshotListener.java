package com.waminiyi.go4lunch.util;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.model.Restaurant;

public interface SnapshotListener {

    void onUsersUpdate(DocumentSnapshot userDoc);

    void onNotesUpdate(DocumentSnapshot noteDoc);

    void onLunchesUpdate(DocumentSnapshot userDoc);

}
