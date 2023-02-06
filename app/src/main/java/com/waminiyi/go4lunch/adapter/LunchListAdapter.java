package com.waminiyi.go4lunch.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.util.LunchClickListener;

import java.util.List;

public class LunchListAdapter extends RecyclerView.Adapter<LunchListViewHolder> {
    private List<Lunch> currentLunchesList;
    private final String TAG;
    /**
     * Listener for Click events
     */
    private final LunchClickListener eventListener;


    public LunchListAdapter(@NonNull final List<Lunch> lunches, String TAG,
                            LunchClickListener eventListener) {
        this.currentLunchesList = lunches;
        this.TAG = TAG;
        this.eventListener = eventListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateLunches(@NonNull final List<Lunch> lunches) {
        this.currentLunchesList = lunches;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public LunchListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.user_lunch_item, parent,
                        false);
        return new LunchListViewHolder(view, TAG);
    }

    @Override
    public void onBindViewHolder(@NonNull LunchListViewHolder holder, int position) {
        holder.bind(currentLunchesList.get(position));
        holder.setListeners(currentLunchesList.get(position), eventListener);
    }

    @Override
    public int getItemCount() {
        return currentLunchesList.size();
    }

    @Override
    public void onViewRecycled(@NonNull LunchListViewHolder holder) {
        super.onViewRecycled(holder);
        holder.removeListeners();
    }
}
