package com.waminiyi.go4lunch.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class UserEntity implements Parcelable {

    private String uId;
    private String userName;
    @Nullable
    private String userEmail;
    @Nullable
    private String urlPicture;
    @Nullable
    private String userPhone;

    private ArrayList <String> favoriteRestaurant;

    public UserEntity() { }

    public UserEntity(String uId, String userName, @Nullable String userEmail, @Nullable String userPhone, @Nullable String urlPicture) {
        this.uId = uId;
        this.userName = userName;
        this.userEmail = userEmail;
        this.urlPicture = urlPicture;
        this.userPhone = userPhone;
        this.favoriteRestaurant=new ArrayList<>();
    }

    protected UserEntity(Parcel in) {
        uId = in.readString();
        userName = in.readString();
        userEmail = in.readString();
        urlPicture = in.readString();
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

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Nullable
    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }

    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }

    public ArrayList<String> getFavoriteRestaurant() {
        return favoriteRestaurant;
    }

    public void setFavoriteRestaurant(ArrayList<String> favoriteRestaurant) {
        this.favoriteRestaurant = favoriteRestaurant;
    }

    @Nullable
    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(@Nullable String userPhone) {
        this.userPhone = userPhone;
    }

    public User toUser(){
        return new User(uId,userName,urlPicture);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeString(uId);
        parcel.writeString(userName);
        parcel.writeString(userEmail);
        parcel.writeString(urlPicture);
        parcel.writeString(userPhone);
        parcel.writeStringList(favoriteRestaurant);
    }
}
