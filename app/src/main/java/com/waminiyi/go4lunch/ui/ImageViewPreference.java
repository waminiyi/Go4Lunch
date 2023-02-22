package com.waminiyi.go4lunch.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import com.bumptech.glide.Glide;
import com.waminiyi.go4lunch.R;

public class ImageViewPreference extends Preference {

    private ImageView imageView;
    View.OnClickListener imageClickListener;
    private Bitmap imageBitmap;
    private String pictureUrl;

    public ImageViewPreference(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    //onBindViewHolder() will be called after we call setImageClickListener() from SettingsFragment
    @Override
    public void onBindViewHolder(@NonNull PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        imageView = (ImageView) holder.findViewById(R.id.settings_profile_image);
        imageView.setOnClickListener(imageClickListener);
        Glide.with(imageView.getContext()).load(pictureUrl).circleCrop().into(imageView);
//        imageView.setImageBitmap(imageBitmap);
    }

    public void setImageClickListener(View.OnClickListener onClickListener) {
        imageClickListener = onClickListener;
    }

    public void setBitmap(Bitmap bitmap) {
        imageBitmap = bitmap;
    }

    public void setImage(String pictureUrl) {
       this.pictureUrl=pictureUrl;
    }

    public ImageView getImageView(){
        return imageView;
    }
}
