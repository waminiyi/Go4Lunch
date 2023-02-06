package com.waminiyi.go4lunch.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.util.LunchClickListener;

public class LunchListViewHolder extends RecyclerView.ViewHolder {

    private final String TAG;

    /**
     * The image showing the picture of the restaurant
     */
    private final AppCompatImageView userImg;

    /**
     * The TextView displaying the name of the restaurant
     */
    private final TextView userLunch;

//    /**
//     * The listener for when a task needs to be deleted
//     */
//    private final DeleteTaskListener deleteTaskListener;

    /**
     * Instantiates a new LunchListViewHolder.
     *
     * @param itemView the view of the User item
     */
    public LunchListViewHolder(@NonNull View itemView, String TAG) {
        super(itemView);
        this.TAG = TAG;

        userImg = itemView.findViewById(R.id.user_picture);
        userLunch = itemView.findViewById(R.id.user_lunch);

    }

    /**
     * Binds a User to the item view.
     *
     * @param lunch: the lunch to bind in the item view
     */
    public void bind(Lunch lunch) {
        Context context = this.itemView.getContext();
        Glide.with(context).load(lunch.getUserPictureUrl()).circleCrop().placeholder(R.drawable.ic_person).into(userImg);  //TODO: placeholder
        String lunchString = lunch.getUserName();
        if (TAG.equals("DetailsFragment")) {
            lunchString = lunchString + " is going ";
            this.itemView.findViewById(R.id.separator).setVisibility(View.GONE);
//            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(48,48);
//            userImg.setLayoutParams(params);
////            userImg.getLayoutParams().width = 64;
//
//            userImg.requestLayout();
        } else {
            if (lunch.getRestaurantName() != null) {
                lunchString =
                        lunchString + context.getString(R.string.eating_at) + lunch.getRestaurantName();
                userLunch.setTypeface(null, Typeface.NORMAL);
                userLunch.setTextColor(Color.parseColor("#000000"));
            } else {
                lunchString = lunchString + context.getString(R.string.has_not_decided);
                userLunch.setTypeface(null, Typeface.ITALIC);
                userLunch.setTextColor(Color.parseColor("#FF858687"));
            }
        }

        userLunch.setText(lunchString);

    }

    public void setListeners(final Lunch lunch,
                             final LunchClickListener listener) {
        this.itemView.setOnClickListener(view -> {
            listener.onLunchClick(lunch);
        });
    }

    public void removeListeners() {
        this.itemView.setOnClickListener(null);
    }

}
