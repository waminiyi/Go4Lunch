package com.waminiyi.go4lunch.ui;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.manager.FileObserver;
import com.waminiyi.go4lunch.manager.FilePermissionObserver;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.UUID;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsActivity extends AppCompatActivity implements FilePermissionObserver.FilePermissionListener,
        FileObserver.OnImageSelectedListener {
    private FileObserver mFileObserver;
    private FilePermissionObserver mFilePermissionObserver;
    private UserViewModel mUserViewModel;
    private ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        profileImage = findViewById(R.id.settings_profile_picture);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mFilePermissionObserver =
                new FilePermissionObserver(getActivityResultRegistry());
        getLifecycle().addObserver(mFilePermissionObserver);
        mFileObserver =
                new FileObserver(getActivityResultRegistry());
        getLifecycle().addObserver(mFileObserver);

        profileImage.setOnClickListener(v -> {
            if (mFilePermissionObserver.isFilePermissionGranted(this)) {
                mFileObserver.selectImage();
            } else {
                mFilePermissionObserver.requestPermission();
            }

        });

        mFileObserver.setListener(this);
        mFilePermissionObserver.setFilePermissionListener(this);

        updateImageView(mUserViewModel.getCurrentUser().getPhotoUrl());

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.setting_layout, new SettingsFragment()).commit();

    }

    private void updateImageView(Uri uri) {
        Glide.with(this) //SHOWING PREVIEW OF IMAGE
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(profileImage);
    }

    private void handleImage(Uri uri) {

        //TODO: Delete existing user picture from firestore storage;

        uploadImage(uri, "profile").addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri1 -> {
            mUserViewModel.updateUserPic(uri1.toString());
            UserProfileChangeRequest profileUpdates =
                    new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri1)
                            .build();
            mUserViewModel.updateProfile(profileUpdates);
        }));


    }

    public UploadTask uploadImage(Uri imageUri, String profile) {
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        StorageReference mImageRef =
                FirebaseStorage.getInstance().getReference(profile + "/" + uuid);
        return mImageRef.putFile(imageUri);
    }


    @Override
    public void onImageSelected(@NonNull Uri uri) {
        handleImage(uri);
        updateImageView(uri);
    }

    @Override
    public void onFilePermissionGranted() {
        mFileObserver.selectImage();
    }

    @Override
    public void onFilePermissionDenied() {

    }
}