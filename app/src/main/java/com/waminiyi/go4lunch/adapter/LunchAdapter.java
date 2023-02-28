package com.waminiyi.go4lunch.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.model.UserLunch;

import java.util.List;

public class LunchAdapter extends RecyclerView.Adapter<LunchViewHolder> {
    private List<UserLunch> currentLunchesList;
    /**
     * Listener for Click events
     */
    private final ClickListener eventListener;


    public LunchAdapter(@NonNull final List<UserLunch> lunches,
                        ClickListener eventListener) {
        this.currentLunchesList = lunches;
        this.eventListener = eventListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateLunches(@NonNull final List<UserLunch> lunches) {
        this.currentLunchesList = lunches;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public LunchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.user_lunch_item, parent,
                        false);
        return new LunchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LunchViewHolder holder, int position) {
        holder.bind(currentLunchesList.get(position));
        holder.setListeners(eventListener);
    }

    @Override
    public int getItemCount() {
        return currentLunchesList.size();
    }

    @Override
    public void onViewRecycled(@NonNull LunchViewHolder holder) {
        super.onViewRecycled(holder);
        holder.removeListeners();
    }

    public interface ClickListener {
        /**
         * Called when the user view  is clicked
         *
         * @param position : position of the userLunch clicked
         */
        void onLunchClick(int position);

    }

    public UserLunch getItemAt(int position){
        return currentLunchesList.get(position);
    }
}
