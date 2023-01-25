package com.waminiyi.go4lunch.manager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.waminiyi.go4lunch.R;

public class PermissionManager {

    private ActivityResultLauncher<String> requestPermissionLauncher;

    public void registerForPermissionResult(FragmentActivity activity) {
        this.requestPermissionLauncher =
                activity.registerForActivityResult(new ActivityResultContracts.RequestPermission(),
                        isGranted -> {
                            if (isGranted) {
                                //if the permission is granted, we get the current location and saved it
                                ((PermissionListener)activity).onPermissionGranted();
                            } else {
                                //
                                if (shouldRequestPermission(activity)) {
                                    /*if the permission was not granted but the user didn't click on "never
                                     * ask again", show why we request the permission
                                     */
                                    showPermissionPurpose(activity);
                                } else {
                                    /*Never ask again selected, or device policy prohibits the app from
                                     *having that permission. Request user to set a default location
                                     */
//                                    setUpDefaultLocation(fragmentManager);
                                    ((PermissionListener)activity).onPermissionDenied();
                                }
                            }
                        });
    }


    public boolean isPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context,
                android.Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public boolean shouldRequestPermission(FragmentActivity activity) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity,
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void requestPermission() {
        requestPermissionLauncher.launch(
                android.Manifest.permission.ACCESS_FINE_LOCATION);
    }

    public void showPermissionPurpose(FragmentActivity activity) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(activity).setMessage(R.string.permission_purpose)
                        .setTitle(R.string.permission_purpose_dialog_title)
                        .setCancelable(false)
                        .setPositiveButton(R.string.give_permission, (dialog, which) -> requestPermission())
                        .setNegativeButton(R.string.deny, (dialog, which) -> {
                            dialog.dismiss();
                            ((PermissionListener)activity).onPermissionDenied();
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    /**
     * Interface that should be implemented by the activity / fragment that shows
     * call the manager
     */
    public interface PermissionListener {
        void onPermissionGranted();

        void onPermissionDenied();

    }
}
