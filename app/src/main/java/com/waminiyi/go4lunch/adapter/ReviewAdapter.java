package com.waminiyi.go4lunch.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waminiyi.go4lunch.databinding.ReviewItemBinding;
import com.waminiyi.go4lunch.model.Review;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewViewHolder> {

    private List<Review> reviewsList;
    private final DeleteClickListener listener;
    private final String currentUserId;

    public ReviewAdapter(List<Review> reviewsList, DeleteClickListener listener, String userId) {
        this.reviewsList = reviewsList;
        this.listener = listener;
        this.currentUserId=userId;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateReviews(@NonNull final List<Review> reviews) {
        this.reviewsList = reviews;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ReviewItemBinding binding =
                ReviewItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false
                );

        return new ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        holder.bind(reviewsList.get(position), currentUserId, listener );
    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

    @Override
    public void onViewRecycled(@NonNull ReviewViewHolder holder) {
        super.onViewRecycled(holder);
    }

    public interface DeleteClickListener {
        void onReviewDelete(Review review);
    }
}
