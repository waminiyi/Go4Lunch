package com.waminiyi.go4lunch.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Objects;

public class Review {
    private String userId;
    private String userName;
    private String userPictureUrl;
    private String restaurantId;
    private String content;
    private int rating;

    @ServerTimestamp
    private Timestamp updatedAt;

    public Review() {
    }

    public Review(String userId, String userName, String userPictureUrl, String restaurantId, String content, int rating) {
        this.userId = userId;
        this.userName = userName;
        this.userPictureUrl = userPictureUrl;
        this.restaurantId = restaurantId;
        this.content = content;
        this.rating = rating;
        this.updatedAt = Timestamp.now();
    }

    public String getUserId() {
        return userId;
    }


    public String getUserName() {
        return userName;
    }


    public String getUserPictureUrl() {
        return userPictureUrl;
    }


    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getRating() {
        return rating;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Review)) return false;
        Review review = (Review) o;
        return getUserId().equals(review.getUserId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserId());
    }
}
