package com.waminiyi.go4lunch.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.model.User;

public class UserViewHolder extends RecyclerView.ViewHolder {

    /**
     * The image showing the picture of the user
     */
    private final AppCompatImageView userImg;

    /**
     * The TextView displaying the name of the restaurant
     */
    private final TextView userTv;

    /**
     * Instantiates a new LunchViewHolder.
     *
     * @param itemView the view of the User item
     */
    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        userImg = itemView.findViewById(R.id.user_picture);
        userTv = itemView.findViewById(R.id.user_lunch);

    }

    /**
     * Binds a User to the item view.
     *
     * @param user: the user to bind in the item view
     */
    public void bind(User user) {
        Context context = this.itemView.getContext();
        Glide.with(context).load(user.getUrlPicture()).circleCrop().placeholder(R.drawable.ic_person).into(userImg);
        String lunchString = user.getUserName() + " is going ";
        this.userTv.setText(lunchString);
    }

    public void setListeners(final UserAdapter.ClickListener listener) {
        this.itemView.setOnClickListener(view -> listener.onUserClick(this.getAdapterPosition()));
    }

    public void removeListeners() {
        this.itemView.setOnClickListener(null);
    }

}
