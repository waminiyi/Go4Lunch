package com.waminiyi.go4lunch.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.databinding.RestaurantItemBinding;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.util.RestaurantClickListener;

import java.util.List;

public class RestaurantListAdapter extends RecyclerView.Adapter<RestaurantListViewHolder> {

    private List<Restaurant> restaurantList;

    /**
     * Listener for Click events
     */
    private final RestaurantClickListener eventListener;

    /**
     * Instantiates a new RestaurantListAdapter.
     */
    public RestaurantListAdapter(List<Restaurant> restaurantList, RestaurantClickListener eventListener) {
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
    public RestaurantListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RestaurantItemBinding binding =
                RestaurantItemBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false
                );

        return new RestaurantListViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantListViewHolder holder, int position) {
        holder.bind(restaurantList.get(position));
        holder.setListeners(restaurantList.get(position), eventListener);
    }


    @Override
    public int getItemCount() {
        return restaurantList.size();
    }


    @Override
    public void onViewRecycled(@NonNull RestaurantListViewHolder holder) {
        super.onViewRecycled(holder);
        holder.removeListeners();
    }

}
