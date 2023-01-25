package com.waminiyi.go4lunch.util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;

import com.waminiyi.go4lunch.R;

import java.util.Objects;

/**Custom progress dialog showing a lottie animation
 *
 */
public class ProgressDialog extends DialogFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        return inflater.inflate(R.layout.loading_layout, container);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Setting the dialog width and height
        int width = getResources().getDisplayMetrics().widthPixels;
        int height =getResources().getDisplayMetrics().heightPixels;
        Objects.requireNonNull(getDialog()).getWindow().setLayout(width, height);
    }
}
