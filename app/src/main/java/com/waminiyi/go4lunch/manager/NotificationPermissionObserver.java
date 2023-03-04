package com.waminiyi.go4lunch.manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.ActivityResultRegistry;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

public class NotificationPermissionObserver implements DefaultLifecycleObserver {

    private final ActivityResultRegistry mRegistry;
    private ActivityResultLauncher<String> mRequestNotificationPermission;
    private NotificationPermissionListener mNotificationPermissionListener;

    public NotificationPermissionObserver(@NonNull ActivityResultRegistry registry) {
        mRegistry = registry;
    }

    public void onCreate(@NonNull LifecycleOwner owner) {

        mRequestNotificationPermission = mRegistry.register("notificationPermission", owner,
                new ActivityResultContracts.RequestPermission(), isNotificationPermissionGranted -> {

                    if (isNotificationPermissionGranted) {
                        mNotificationPermissionListener.onFilePermissionGranted();
                    } else {
                        mNotificationPermissionListener.onFilePermissionDenied();
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void requestPermission() {
        mRequestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public boolean isFilePermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED;
    }


    /**
     * Interface that should be implemented by the activity / fragment that shows
     * call the manager
     */
    public interface NotificationPermissionListener {
        void onFilePermissionGranted();

        void onFilePermissionDenied();
    }

    public void setNotificationPermissionListener(NotificationPermissionListener notificationPermissionListener) {
        mNotificationPermissionListener = notificationPermissionListener;
    }
}
