package com.waminiyi.go4lunch.model;

import java.util.List;
import java.util.Objects;

public class Restaurant {

    /** A textual identifier that uniquely identifies a restaurant. */
    private String id;

    /**The human-readable name for the restaurant*/
    private String name;

    /** The sum of all ratings for this restaurant based on aggregated user reviews. */
    private int ratingSum;

    /** The total count of ratings based on aggregated user reviews. */
    private int ratingCount;

    /** The list of user that are intending to lunch on the restaurant */
    private List<UsersSnippet> lunchList;

    /**The human-readable address for the restaurant*/
    private String address;

    /**The restaurant location's latitude*/
    private double latitude;

    /**The restaurant location's longitude*/
    private double longitude;

    /** Indicates that the restaurant is open now */
    private boolean openNow;

    /** Textual reference that uniquely identify a photo of the restaurant */
    private String photoReference;

    /** Distance between the restaurant and the user location*/
    private int distance;

    /** Type of restoration*/
    private String restaurantType;

    public Restaurant() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<UsersSnippet> getLunchList() {
        return lunchList;
    }

    public void setLunchList(List<UsersSnippet> lunchList) {
        this.lunchList = lunchList;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setOpenNow(boolean openNow) {
        this.openNow = openNow;
    }

    public boolean isOpenNow() {
        return openNow;
    }

    public String getPhotoReference() {
        return photoReference;
    }

    public void setPhotoReference(String photoReference) {
        this.photoReference = photoReference;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getRestaurantType() {
        return restaurantType;
    }

    public void setRestaurantType(String restaurantType) {
        this.restaurantType = restaurantType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Restaurant)) return false;
        Restaurant restaurant = (Restaurant) o;
        return getId().equals(restaurant.getId()) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
}
