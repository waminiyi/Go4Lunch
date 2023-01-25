package com.waminiyi.go4lunch.model;

public class Rating {
    private int ratingSum;
    private int ratingCount;

    public Rating(int ratingSum, int ratingCount) {
        this.ratingSum = ratingSum;
        this.ratingCount = ratingCount;
    }

    public Rating() {
    }

    public int getRatingSum() {
        return ratingSum;
    }

    public void setRatingSum(int ratingSum) {
        this.ratingSum = ratingSum;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }
}
