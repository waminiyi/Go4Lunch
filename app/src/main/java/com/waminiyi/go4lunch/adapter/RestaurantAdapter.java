package com.waminiyi.go4lunch.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waminiyi.go4lunch.databinding.RestaurantItemBinding;
import com.waminiyi.go4lunch.model.Restaurant;

import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantViewHolder> {

    private List<Restaurant> restaurantList;

    /**
     * Listener for Click events
     */
    private final ClickListener eventListener;

    /**
     * Instantiates a new RestaurantAdapter.
     */
    public RestaurantAdapter(List<Restaurant> restaurantList, ClickListener eventListener) {
        this.restaurantList = restaurantList;
        this.eventListener = eventListener;
    }

    /**
     * Updates the list of tasks the adapter deals with.
     *
     * @param restaurantList: the list of restaurants the adapter deals with to set
     */
    @SuppressLint("NotifyDataSetChanged")
    public void updateRestaurants(@NonNull final List<Restaurant> restaurantList) {
        this.restaurantList = restaurantList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RestaurantItemBinding binding =
                RestaurantItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false
                );

        return new RestaurantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        holder.bind(restaurantList.get(position));
        holder.setListeners(restaurantList.get(position), eventListener);
    }


    @Override
    public int getItemCount() {
        return restaurantList.size();
    }


    @Override
    public void onViewRecycled(@NonNull RestaurantViewHolder holder) {
        super.onViewRecycled(holder);
        holder.removeListeners();
    }

    public interface ClickListener {
        /**
         * Called when the favorite button is clicked
         *
         * @param restaurant we want to add to favorites
         */
        void onFavoriteButtonClick(Restaurant restaurant);

        /**
         * Called when the restaurant view is clicked
         *
         * @param restaurant we want show details for
         */
        void onRestaurantClick(Restaurant restaurant);
    }

}
