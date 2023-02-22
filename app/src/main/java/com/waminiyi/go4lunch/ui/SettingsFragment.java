package com.waminiyi.go4lunch.ui;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.model.UserEntity;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.UUID;

public class SettingsFragment extends PreferenceFragmentCompat {

    private UserViewModel mUserViewModel;
    private UserEntity user = new UserEntity();
    private EditTextPreference namePref;
    private ImageViewPreference imageViewPreference;
    private ActivityResultLauncher<String> requestPermissionLauncher;
    private static final int RC_CHOOSE_PHOTO = 200;
    private Uri uriImageSelected;


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        mUserViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        requestPermissionLauncher =
                this.registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                    if (isGranted) {
                        pickPhoto();
                    } else {

                    }
                });


        namePref = findPreference("name");
        if (namePref != null) {
            namePref.setDefaultValue(mUserViewModel.getCurrentUser().getDisplayName());
            namePref.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue != null) {
                    mUserViewModel.updateUserName((String) newValue);
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName((String) newValue)
                            .build();
                    mUserViewModel.updateProfile(profileUpdates);
                }
                return true;
            });
        }

        imageViewPreference = findPreference("profile_image");
        if (imageViewPreference != null) {
            imageViewPreference.setImage(mUserViewModel.getCurrentUser().getPhotoUrl().toString());
            imageViewPreference.setImageClickListener(v -> {

                if (ActivityCompat.checkSelfPermission(requireContext(),
                        android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    pickPhoto();
                } else {
                    requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }

            });
        }
    }

    private void pickPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RC_CHOOSE_PHOTO);
    }

    private void handleResponse(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_CHOOSE_PHOTO) {
            if (resultCode == RESULT_OK) { //SUCCESS
                this.uriImageSelected = data.getData();
                Glide.with(this) //SHOWING PREVIEW OF IMAGE
                        .load(this.uriImageSelected)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imageViewPreference.getImageView());
//TODO: Delete existing user picture from firestore storage;

                uploadImage(uriImageSelected, "profile").addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                mUserViewModel.updateUserPic(uri.toString());
                                UserProfileChangeRequest profileUpdates =
                                        new UserProfileChangeRequest.Builder()
                                                .setPhotoUri(uri)
                                                .build();
                                mUserViewModel.updateProfile(profileUpdates);
                            }
                        });
                    }
                });
            } else {
                Toast.makeText(requireContext(), "No image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public UploadTask uploadImage(Uri imageUri, String profile) {
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        StorageReference mImageRef =
                FirebaseStorage.getInstance().getReference(profile + "/" + uuid);
        return mImageRef.putFile(imageUri);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponse(requestCode, resultCode, data);
    }


    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mUserViewModel.getCurrentUserData().observe(getViewLifecycleOwner(),
                newUser -> {
                    user = newUser;
//                    updateNamePreferenceText(user.getUserName());
//                    updateProfileImage(user.getUrlPicture());
                });


        return super.onCreateView(inflater, container, savedInstanceState);
    }

    private void updateNamePreferenceText(String name) {
        EditTextPreference namePref = findPreference("name");
        if (namePref != null) {
            namePref.setText(name);
            namePref.setDefaultValue(mUserViewModel.getCurrentUser().getDisplayName());

            namePref.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue != null) {
                    mUserViewModel.updateUserName((String) newValue);
                }
                return false;
            });
        }
    }

    private void updateProfileImage(String url) {
        ImageViewPreference imageViewPreference = findPreference("profile_image");
        if (imageViewPreference != null) {

            imageViewPreference.setImageClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    //do whatever you want on image click here
                    Toast.makeText(getContext(), "Image Clicked", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }
}