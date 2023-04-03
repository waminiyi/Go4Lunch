package com.waminiyi.go4lunch.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.waminiyi.go4lunch.BuildConfig;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.databinding.RestaurantItemBinding;
import com.waminiyi.go4lunch.model.Restaurant;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RestaurantViewHolder extends RecyclerView.ViewHolder {

    private final RestaurantItemBinding binding;
    private final String MAPS_API_KEY = BuildConfig.MAPS_API_KEY;

    /**
     * Instantiates a new RestaurantViewHolder.
     *
     * @param binding: the Restaurant item data binding
     */

    public RestaurantViewHolder(@NonNull RestaurantItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    /**
     * Binds a task to the item view.
     *
     * @param restaurant: the restaurant to bind in the item view
     */
    public void bind(final Restaurant restaurant) {

        Context context = binding.getRoot().getContext();
        String imgUrl;
        if (restaurant.getPhotoReference() != null) {
            imgUrl =
                    context.getString(R.string.place_image_url) + restaurant.getPhotoReference() + "&key=" +
                            MAPS_API_KEY;
        } else {
            imgUrl = context.getString(R.string.restaurant_image_placeholder_url);
        }

        Glide.with(context).load(imgUrl).centerCrop().placeholder(R.drawable.restaurant_image_placeholder).
                into(binding.restaurantImage);

        binding.restaurantName.setText(restaurant.getName());

        String[] address = restaurant.getAddress().split(",");
        binding.restaurantAddress.setText(address[0]);
        showOpening(restaurant);
        showLunch(restaurant);
        showDistance(restaurant);
        showRating(restaurant);
        if (restaurant.isUserFavorite()) {
            binding.favoriteImageButton.setImageTintList(ColorStateList.valueOf(Color.parseColor(context.getString(R.string.isFavoriteColor))));
        } else {
            binding.favoriteImageButton.setImageTintList(ColorStateList.valueOf(Color.parseColor(
                    context.getString(R.string.isNotFavoriteColor))));
        }

    }

    private void showRating(Restaurant restaurant) {
        float rating = restaurant.getRating();
        binding.restaurantItemRating.setRating(rating);
    }


    private void showOpening(Restaurant restaurant) {
        if (restaurant.isOpenNow()) {
            binding.openNow.setText(R.string.open_now);
            binding.openNow.setTypeface(null, Typeface.ITALIC);
            binding.openNow.setTextColor(Color.parseColor("#056363"));
        } else {
            binding.openNow.setText(R.string.closed);
            binding.openNow.setTypeface(null, Typeface.NORMAL);
            binding.openNow.setTextColor(Color.parseColor("red"));
        }
    }

    private void showLunch(Restaurant restaurant) {
        if (restaurant.getLunchCount() != 0) {
            String lunchCount = "(" + restaurant.getLunchCount() + ")";
            binding.lunchCount.setText(lunchCount);
            binding.lunchCount.setVisibility(View.VISIBLE);
        } else {
            binding.lunchCount.setVisibility(View.INVISIBLE);
        }
    }

    private void showDistance(Restaurant restaurant) {
        int dist = restaurant.getDistance();
        String distanceString;
        if (dist < 1000) {
            distanceString = dist + " m";
        } else {
            double d = (double) dist / 1000;
            distanceString =
                    new BigDecimal(d).setScale(2, RoundingMode.HALF_UP).doubleValue() + " km";
        }
        binding.distance.setText(distanceString);
    }

    public void setListeners(final Restaurant restaurant,
                             final RestaurantAdapter.ClickListener listener) {
        binding.favoriteImageButton.setOnClickListener(view -> listener.onFavoriteButtonClick(restaurant));

        this.itemView.setOnClickListener(view -> listener.onRestaurantClick(restaurant));
    }

    public void removeListeners() {
        binding.favoriteImageButton.setOnClickListener(null);
        binding.lunchCount.setOnClickListener(null);
        this.itemView.setOnClickListener(null);
    }


}
