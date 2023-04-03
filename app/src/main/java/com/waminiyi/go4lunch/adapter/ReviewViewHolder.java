package com.waminiyi.go4lunch.adapter;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.Timestamp;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.databinding.ReviewItemBinding;
import com.waminiyi.go4lunch.model.Review;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class ReviewViewHolder extends RecyclerView.ViewHolder {

    private final ReviewItemBinding binding;
    private Locale locale;

    public ReviewViewHolder(@NonNull ReviewItemBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(final Review review, String currentUserId, ReviewAdapter.DeleteClickListener listener) {
        if (review.getUserId().equals(currentUserId)) {
            binding.buttonDelete.setVisibility(View.VISIBLE);
            binding.buttonDelete.setOnClickListener(view -> listener.onReviewDelete(review));
        }else{
            binding.buttonDelete.setVisibility(View.GONE);
        }

        Context context = binding.getRoot().getContext();
        locale = context.getResources().getConfiguration().locale;

        Glide.with(context).load(review.getUserPictureUrl()).fitCenter().circleCrop().into(binding.reviewItemPicture);
        binding.reviewItemName.setText(review.getUserName());

        if (!review.getContent().isEmpty()){
            binding.reviewItemContent.setVisibility(View.VISIBLE);
            binding.reviewItemContent.setText(review.getContent());
        }else{
            binding.reviewItemContent.setVisibility(View.GONE);
        }

        showTimElapsed(review.getUpdatedAt(), context);
        binding.reviewItemRating.setRating(review.getRating());

    }

    private void showTimElapsed(Timestamp timestamp, Context context) {
        String pattern = "dd/MM/yyyy";
        DateFormat df = new SimpleDateFormat(pattern, locale);
        String updatedAt =context.getString(R.string.updatedAt) + df.format(timestamp.toDate());

        binding.reviewItemDuration.setText(updatedAt);
    }

}
