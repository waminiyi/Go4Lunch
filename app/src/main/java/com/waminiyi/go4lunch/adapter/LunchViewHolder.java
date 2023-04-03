package com.waminiyi.go4lunch.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.model.UserLunch;

public class LunchViewHolder extends RecyclerView.ViewHolder {

    /**
     * The image showing the picture of the user
     */
    private final AppCompatImageView userImg;

    /**
     * The TextView displaying the name of the restaurant
     */
    private final TextView userLunchTv;

    /**
     * Instantiates a new LunchViewHolder.
     *
     * @param itemView the view of the User item
     */
    public LunchViewHolder(@NonNull View itemView) {
        super(itemView);

        userImg = itemView.findViewById(R.id.user_picture);
        userLunchTv = itemView.findViewById(R.id.user_lunch);

    }

    /**
     * Binds a User to the item view.
     *
     * @param userLunch: the lunch to bind in the item view
     */
    public void bind(UserLunch userLunch) {
        Context context = this.itemView.getContext();
        Glide.with(context).load(userLunch.getUserPictureUrl()).circleCrop().placeholder(R.drawable.ic_person).into(userImg);
        String lunchString = userLunch.getUserName();
        if (userLunch.getRestaurantName() != null) {
            lunchString =
                    lunchString + context.getString(R.string.eating_at) + userLunch.getRestaurantName();
            this.userLunchTv.setTypeface(null, Typeface.NORMAL);
            this.userLunchTv.setTextColor(Color.parseColor(context.getString(R.string.userLunchTvColor)));
        } else {
            lunchString = lunchString + context.getString(R.string.has_not_decided);
            this.userLunchTv.setTypeface(null, Typeface.ITALIC);
            this.userLunchTv.setTextColor(Color.parseColor(context.getString(R.string.userNoLunchTvColor)));
        }

        this.userLunchTv.setText(lunchString);

    }

    public void setListeners(final LunchAdapter.ClickListener listener) {
        this.itemView.setOnClickListener(view -> listener.onLunchClick(this.getAdapterPosition()));
    }

    public void removeListeners() {
        this.itemView.setOnClickListener(null);
    }

}
