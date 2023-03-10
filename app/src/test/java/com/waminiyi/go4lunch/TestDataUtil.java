package com.waminiyi.go4lunch;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.maps.model.Geometry;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.NearbyPlaceSearchResponse;
import com.waminiyi.go4lunch.model.NearbyPlaceSearchResult;
import com.waminiyi.go4lunch.model.OpeningHours;
import com.waminiyi.go4lunch.model.Photo;
import com.waminiyi.go4lunch.model.Rating;
import com.waminiyi.go4lunch.model.Restaurant;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executor;

public class TestDataUtil {

    NearbyPlaceSearchResponse fakeResponse = new NearbyPlaceSearchResponse();
    NearbyPlaceSearchResponse fakeNextPageResponse = new NearbyPlaceSearchResponse();
    NearbyPlaceSearchResult result = new NearbyPlaceSearchResult();
    NearbyPlaceSearchResult result2 = new NearbyPlaceSearchResult();
    NearbyPlaceSearchResult result3 = new NearbyPlaceSearchResult();
    NearbyPlaceSearchResult result4 = new NearbyPlaceSearchResult();
    NearbyPlaceSearchResult result5 = new NearbyPlaceSearchResult();
    NearbyPlaceSearchResult[] results = new NearbyPlaceSearchResult[4];
    NearbyPlaceSearchResult[] nextResults = new NearbyPlaceSearchResult[1];
    Geometry geometry = new Geometry();


    String realId = "ChIJM-99jHbzikcRi0QEn14wpZI", realId2 = "ChIJVxTZV3bzikcRHXm_Ae0Db68",
            realId3 =
                    "ChIJAQBk7nbzikcRSD-EF0vlUoI", realId4 = "ChIJ5UV8-XbzikcRyv3a0vUFygI";

    DocumentSnapshot mockedRatingDoc = mock(DocumentSnapshot.class);

    DocumentSnapshot mockedUserDoc = mock(DocumentSnapshot.class);

    DocumentSnapshot mockedLunchDoc = mock(DocumentSnapshot.class);


    public TestDataUtil() {
    }

    public List<Restaurant> getFakeRestaurants() {
        return Arrays.asList(new Restaurant("a", "The Hungry Bear", 2.2f, 25, "123 Main St", 37.7749
                        , -122.4194, true, "photo_ref_1", 2, true),
                new Restaurant("b", "The Crispy Chicken", 2.8f, 12, "456 Elm St", 37.7750,
                        -122.4189, false, "photo_ref_2", 4, false),
                new Restaurant("c", "Pizza Palace", 1.5f, 50, "789 Oak St", 37.7751, -122.4184,
                        true, "photo_ref_3", 1, true),
                new Restaurant("d", "Sushi Spot", 3.0f, 18, "321 Pine St", 37.7752, -122.4179, true,
                        "photo_ref_4", 3, false));
    }

    public void buildMockedResponse() {
        geometry.location = new com.google.maps.model.LatLng(45.19, 5.69);
        Photo photo = new Photo();
        Photo[] photos = new Photo[1];
        photos[0] = photo;
        photo.photoReference = "photo_ref";
        OpeningHours open = new OpeningHours();
        OpeningHours closed = new OpeningHours();
        open.openNow = true;
        closed.openNow = false;

        result.geometry = geometry;
        result.placeId = "a";
        result.name = "The Hungry Bear";
        result.vicinity = "123 Main St";
        result.openingHours = open;
        result.photos = photos;

        result2.geometry = geometry;
        result2.placeId = "b";
        result2.name = "The Crispy Chicken";
        result2.vicinity = "456 Elm St";
        result2.openingHours = closed;
        result2.photos = photos;

        result3.geometry = geometry;
        result3.placeId = "c";
        result3.name = "Pizza Palace";
        result3.vicinity = "789 Oak St";
        result3.openingHours = open;
        result3.photos = photos;

        result4.geometry = geometry;
        result4.placeId = "d";
        result4.name = "Sushi Spot";
        result4.vicinity = "321 Pine St";
        result4.openingHours = open;
        result4.photos = photos;

        result5.geometry = geometry;
        result5.placeId = "e";
        result5.name = "Sushi house";
        result5.vicinity = "345 Pine St";
        result5.openingHours = open;
        result5.photos = photos;

        results[0] = result;
        results[1] = result2;
        results[2] = result3;
        results[3] = result4;

        nextResults[0] = result5;

        fakeResponse.nextPageToken = "token";
        fakeResponse.results = results;

        fakeNextPageResponse.nextPageToken = null;
        fakeNextPageResponse.results = nextResults;

    }


    public void setUpHelper(FirebaseHelper mockedHelper) {

        when(mockedRatingDoc.get("a", Rating.class)).thenReturn(new Rating(6, 3));
        when(mockedRatingDoc.get("b", Rating.class)).thenReturn(new Rating(6, 2));

        when(mockedLunchDoc.getLong("a")).thenReturn(4L);
        when(mockedLunchDoc.getLong("b")).thenReturn(3L);
        when(mockedLunchDoc.getLong("c")).thenReturn(2L);

        when(mockedUserDoc.get("favoriteRestaurant")).thenReturn(Arrays.asList("a", "c", "e",realId2, realId3));

        when(mockedRatingDoc.get(realId, Rating.class)).thenReturn(new Rating(6, 3));
        when(mockedRatingDoc.get(realId2, Rating.class)).thenReturn(new Rating(6, 2));

        when(mockedLunchDoc.getLong(realId)).thenReturn(4L);
        when(mockedLunchDoc.getLong(realId4)).thenReturn(3L);
        when(mockedLunchDoc.getLong(realId3)).thenReturn(2L);

        when(mockedHelper.getLunchesCount()).thenReturn(lunchTask);
        when(mockedHelper.getRestaurantNotes()).thenReturn(ratingTask);
        when(mockedHelper.getCurrentUserDoc()).thenReturn(userTask);
    }

    Task<DocumentSnapshot> lunchTaskForResult = Tasks.forResult(mockedLunchDoc);
    Task<DocumentSnapshot> ratingTaskForResult = Tasks.forResult(mockedRatingDoc);
    Task<DocumentSnapshot> userTaskForResult = Tasks.forResult(mockedUserDoc);

    Task<DocumentSnapshot> lunchTask = new Task<DocumentSnapshot>() {
        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnSuccessListener(@NonNull OnSuccessListener<? super DocumentSnapshot> onSuccessListener) {
            onSuccessListener.onSuccess(lunchTaskForResult.getResult());
            return lunchTaskForResult.addOnSuccessListener(onSuccessListener);
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super DocumentSnapshot> onSuccessListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super DocumentSnapshot> onSuccessListener) {
            return null;
        }

        @Nullable
        @Override
        public Exception getException() {
            return null;
        }

        @Override
        public DocumentSnapshot getResult() {
            return null;
        }

        @Override
        public <X extends Throwable> DocumentSnapshot getResult(@NonNull Class<X> aClass) throws X {
            return null;
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public boolean isSuccessful() {
            return true;
        }

    };

    Task<DocumentSnapshot> userTask = new Task<DocumentSnapshot>() {
        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnSuccessListener(@NonNull OnSuccessListener<? super DocumentSnapshot> onSuccessListener) {
            onSuccessListener.onSuccess(userTaskForResult.getResult());
            return userTaskForResult.addOnSuccessListener(onSuccessListener);
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super DocumentSnapshot> onSuccessListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super DocumentSnapshot> onSuccessListener) {
            return null;
        }

        @Nullable
        @Override
        public Exception getException() {
            return null;
        }

        @Override
        public DocumentSnapshot getResult() {
            return null;
        }

        @Override
        public <X extends Throwable> DocumentSnapshot getResult(@NonNull Class<X> aClass) throws X {
            return null;
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public boolean isSuccessful() {
            return true;
        }

    };

    Task<DocumentSnapshot> ratingTask = new Task<DocumentSnapshot>() {
        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnFailureListener(@NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnFailureListener(@NonNull Activity activity, @NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnFailureListener(@NonNull Executor executor, @NonNull OnFailureListener onFailureListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnSuccessListener(@NonNull OnSuccessListener<? super DocumentSnapshot> onSuccessListener) {
            onSuccessListener.onSuccess(ratingTaskForResult.getResult());
            return ratingTaskForResult.addOnSuccessListener(onSuccessListener);
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnSuccessListener(@NonNull Activity activity, @NonNull OnSuccessListener<? super DocumentSnapshot> onSuccessListener) {
            return null;
        }

        @NonNull
        @Override
        public Task<DocumentSnapshot> addOnSuccessListener(@NonNull Executor executor, @NonNull OnSuccessListener<? super DocumentSnapshot> onSuccessListener) {
            return null;
        }

        @Nullable
        @Override
        public Exception getException() {
            return null;
        }

        @Override
        public DocumentSnapshot getResult() {
            return null;
        }

        @Override
        public <X extends Throwable> DocumentSnapshot getResult(@NonNull Class<X> aClass) throws X {
            return null;
        }

        @Override
        public boolean isCanceled() {
            return false;
        }

        @Override
        public boolean isComplete() {
            return true;
        }

        @Override
        public boolean isSuccessful() {
            return true;
        }

    };

}
