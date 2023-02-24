package com.waminiyi.go4lunch.manager;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class FilePermissionObserver implements DefaultLifecycleObserver {

    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<String> mRequestFilePermission;
    private FilePermissionListener mFilePermissionListener;

    public FilePermissionObserver(@NonNull ActivityResultRegistry registry) {
        mRegistry = registry;
    }

    public void onCreate(@NonNull LifecycleOwner owner) {

        mRequestFilePermission = mRegistry.register("filePermission", owner,
                new ActivityResultContracts.RequestPermission(), isFilePermissionGranted -> {

                    if (isFilePermissionGranted) {
                        mFilePermissionListener.onFilePermissionGranted();
                    } else {
                        mFilePermissionListener.onFilePermissionDenied();
                    }
                });
    }

    public void requestPermission() {
        mRequestFilePermission.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE);
    }

    public boolean isFilePermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Interface that should be implemented by the activity / fragment that shows
     * call the manager
     */
    public interface FilePermissionListener {
        void onFilePermissionGranted();

        void onFilePermissionDenied();
    }

    public void setFilePermissionListener(FilePermissionListener filePermissionListener) {
        mFilePermissionListener = filePermissionListener;
    }
}
