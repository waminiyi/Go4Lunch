package com.waminiyi.go4lunch.manager;

import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class FileObserver implements DefaultLifecycleObserver {
    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<String> mGetContent;
    private OnImageSelectedListener mListener;


    public FileObserver(@NonNull ActivityResultRegistry registry) {
        mRegistry = registry;
    }

    public void onCreate(@NonNull LifecycleOwner owner) {

        mGetContent = mRegistry.register("image", owner, new ActivityResultContracts.GetContent(),
                uri -> {
                    // Handle the returned Uri
                    if (uri != null) {
                        mListener.onImageSelected(uri);
                    }
                });
    }

    public void selectImage() {
        // Open the activity to select an image
        mGetContent.launch("image/*");
    }

    /**
     * Interface that should be implemented by the activity / fragment that shows
     * call the manager
     * it passes the selected image uri to the activity/fragment
     */
    public interface OnImageSelectedListener {
        void onImageSelected(@NonNull Uri uri);
    }

    public void setListener(OnImageSelectedListener listener) {
        mListener = listener;
    }
}
