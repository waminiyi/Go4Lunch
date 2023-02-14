package com.waminiyi.go4lunch.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.databinding.RestaurantItemBinding;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.util.RestaurantClickListener;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class RestaurantListViewHolder extends RecyclerView.ViewHolder {

    private final RestaurantItemBinding binding;

    /**
     * Instantiates a new RestaurantListViewHolder.
     *
     * @param binding: the Restaurant item data binding
     */

    public RestaurantListViewHolder(@NonNull RestaurantItemBinding binding) {
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
            imgUrl = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400" +
                    "&photoreference=" + restaurant.getPhotoReference() + "&key=" +
                    context.getString(R.string.google_map_key);
        } else {
            imgUrl =
                    "https://images.pexels.com/photos/262978/pexels-photo-262978.jpeg?auto=compress&cs=tinysrgb&w=1260&h=750&dpr=2";
        }

        Glide.with(context).load(imgUrl).fitCenter().placeholder(R.drawable.restaurant_image_placeholder).
                into(binding.restaurantImage);

//        Glide.with(context).load(imgUrl).into(restaurantImg);

        binding.restaurantName.setText(restaurant.getName());

        String[] address = restaurant.getAddress().split(",");
        binding.restaurantAddress.setText(address[0]);
        showOpening(restaurant);
        showLunch(restaurant);
        showDistance(restaurant);
        showRating(restaurant);

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
                             final RestaurantClickListener listener) {
        binding.favoriteImageButton.setOnClickListener(view -> {
            listener.onFavoriteButtonClick(restaurant);
        });

        this.itemView.setOnClickListener(view -> {
            listener.onRestaurantClick(restaurant);
        });
    }

    public void removeListeners() {
        binding.favoriteImageButton.setOnClickListener(null);
        binding.lunchCount.setOnClickListener(null);
        this.itemView.setOnClickListener(null);
    }


}
