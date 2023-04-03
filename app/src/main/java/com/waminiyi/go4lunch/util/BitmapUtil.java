package com.waminiyi.go4lunch.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.waminiyi.go4lunch.R;

public class BitmapUtil {
    public BitmapDescriptor bitmapDescriptorFromVector(Context context, @ColorInt int colorID) {
        Drawable background = ContextCompat.getDrawable(context, R.drawable.restaurant_marker_background);
        background.setColorFilter(colorID, PorterDuff.Mode.MULTIPLY);
        background.setBounds(0, 0, background.getIntrinsicWidth(), background.getIntrinsicHeight());
        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.restaurant_marker_foreground);
        vectorDrawable.setColorFilter(colorID, PorterDuff.Mode.MULTIPLY);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight() );
        Bitmap bitmap = Bitmap.createBitmap(background.getIntrinsicWidth(), background.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        background.draw(canvas);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

//    public BitmapDescriptor bitmapDescriptorFromVector(Context context, @ColorInt int colorID) {
//        Drawable vectorDrawable = ContextCompat.getDrawable(context, R.drawable.restaurant_marker_background);
//        vectorDrawable.setColorFilter(colorID, PorterDuff.Mode.MULTIPLY);
//        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth() ,
//                vectorDrawable.getIntrinsicHeight());
//        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bitmap);
//        vectorDrawable.draw(canvas);
//        return BitmapDescriptorFactory.fromBitmap(bitmap);
//    }
}
