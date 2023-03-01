package com.waminiyi.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserEntity implements Parcelable {

    private String userId;
    private String userName;
    @Nullable
    private String userEmail;
    @Nullable
    private String userPictureUrl;
    @Nullable
    private String userPhone;

    private ArrayList<String> favoriteRestaurant;

    public UserEntity() {
    }

    public UserEntity(String userId, String userName, @Nullable String userEmail, @Nullable String userPhone, @Nullable String userPictureUrl) {
        this.userId = userId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPictureUrl = userPictureUrl;
        this.userPhone = userPhone;
        this.favoriteRestaurant = new ArrayList<>();
    }

    protected UserEntity(Parcel in) {
        userId = in.readString();
        userName = in.readString();
        userEmail = in.readString();
        userPictureUrl = in.readString();
        userPhone = in.readString();
        favoriteRestaurant = in.createStringArrayList();
    }

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel in) {
            return new UserEntity(in);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    @Nullable
    public String getUserEmail() {
        return userEmail;
    }

    @Nullable
    public String getUserPictureUrl() {
        return userPictureUrl;
    }

    public ArrayList<String> getFavoriteRestaurant() {
        return favoriteRestaurant;
    }

    @Nullable
    public String getUserPhone() {
        return userPhone;
    }

    public User toUser() {
        return new User(userId, userName, userPictureUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(userId);
        parcel.writeString(userName);
        parcel.writeString(userEmail);
        parcel.writeString(userPictureUrl);
        parcel.writeString(userPhone);
        parcel.writeStringList(favoriteRestaurant);
    }
}
