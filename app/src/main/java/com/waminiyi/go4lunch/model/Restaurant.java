package com.waminiyi.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.Objects;

public class Restaurant implements Parcelable {

    /** A textual identifier that uniquely identifies a restaurant. */
    private String id;

    /**The human-readable name for the restaurant*/
    private String name;

    /** The rating for this restaurant based on aggregated user reviews. */
    private float rating;

    /** The list of user that are intending to lunch on the restaurant */
    private int lunchCount;

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

    public Restaurant() {
    }

    protected Restaurant(Parcel in) {
        id = in.readString();
        name = in.readString();
        rating = in.readFloat();
        lunchCount = in.readInt();
        address = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        openNow = in.readByte() != 0;
        photoReference = in.readString();
        distance = in.readInt();
    }

    public static final Creator<Restaurant> CREATOR = new Creator<Restaurant>() {
        @Override
        public Restaurant createFromParcel(Parcel in) {
            return new Restaurant(in);
        }

        @Override
        public Restaurant[] newArray(int size) {
            return new Restaurant[size];
        }
    };

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

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int getLunchCount() {
        return lunchCount;
    }

    public void setLunchCount(int lunchCount) {
        this.lunchCount = lunchCount;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeFloat(rating);
        parcel.writeInt(lunchCount);
        parcel.writeString(address);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeByte((byte) (openNow ? 1 : 0));
        parcel.writeString(photoReference);
        parcel.writeInt(distance);
    }
}
