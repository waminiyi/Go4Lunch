package com.waminiyi.go4lunch.ui;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.waminiyi.go4lunch.util.Constants;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.Objects;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class SettingsActivity extends AppCompatActivity implements FilePermissionObserver.FilePermissionListener,
        FileObserver.OnImageSelectedListener {
    private FileObserver mFileObserver;
    private FilePermissionObserver mFilePermissionObserver;
    private UserViewModel mUserViewModel;
    private ImageView mProfileImage;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_settings);
        mProfileImage = findViewById(R.id.settings_profile_picture);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mFilePermissionObserver =
                new FilePermissionObserver(getActivityResultRegistry());
        getLifecycle().addObserver(mFilePermissionObserver);
        mFileObserver =
                new FileObserver(getActivityResultRegistry());
        getLifecycle().addObserver(mFileObserver);

        mProfileImage.setOnClickListener(v -> {
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


        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

    }

    private void updateImageView(Uri uri) {
        Glide.with(this)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .into(mProfileImage);
    }

    private void handleImage(Uri uri) {


        uploadImage(uri, Constants.PROFILE).addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri1 -> {
            mUserViewModel.updateUserPic(uri1.toString());
            UserProfileChangeRequest profileUpdates =
                    new UserProfileChangeRequest.Builder()
                            .setPhotoUri(uri1)
                            .build();
            mUserViewModel.updateProfile(profileUpdates);
        }).addOnFailureListener(e -> Log.d(TAG, "failed" + e)));


    }

    public UploadTask uploadImage(Uri imageUri, String profile) {
        String uuid=mUserViewModel.getCurrentUserUID();

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