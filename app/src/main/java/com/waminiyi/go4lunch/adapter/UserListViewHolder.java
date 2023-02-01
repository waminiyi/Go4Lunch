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

public class UserListViewHolder extends RecyclerView.ViewHolder {

    /**
     * The image showing the picture of the restaurant
     */
    private final AppCompatImageView userImg;

    /**
     * The TextView displaying the name of the restaurant
     */
    private final TextView userLunch;

//    /**
//     * The listener for when a task needs to be deleted
//     */
//    private final DeleteTaskListener deleteTaskListener;

    /**
     * Instantiates a new UserListViewHolder.
     *
     * @param itemView the view of the User item
     */
    public UserListViewHolder(@NonNull View itemView) {
        super(itemView);

        userImg = itemView.findViewById(R.id.user_picture);
        userLunch = itemView.findViewById(R.id.user_lunch);

//        imgDelete.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                final Object tag = view.getTag();
//                if (tag instanceof Task) {
//                    TaskViewHolder.this.deleteTaskListener.onDeleteTask((Task) tag);
//                }
//            }
//        });
    }

    /**
     * Binds a User to the item view.
     *
     * @param user: the restaurant to bind in the item view
     */
    public void bind(User user) {
        Context context = this.itemView.getContext();
        Glide.with(context).load(user.getUrlPicture()).circleCrop().placeholder(R.drawable.ic_person).into(userImg);  //TODO: placeholder
        String lunch = user.getUserName();

        if (user.getUserLunch() != null) {
            lunch = lunch + " is eating at " + user.getUserLunch();
        } else {
            lunch = lunch + " has not decided yet ";
        }
        userLunch.setText(lunch);

    }

}
