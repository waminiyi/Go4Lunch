package com.waminiyi.go4lunch.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListViewHolder> {
    private List<User> currentUsersList;


    public UserListAdapter() {
        currentUsersList=new ArrayList<>();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateUsers(@NonNull final List<User> users) {
        this.currentUsersList = users;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.user_lunch_item, parent,
                        false);
        return new UserListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserListViewHolder holder, int position) {
        holder.bind(currentUsersList.get(position));
    }

    @Override
    public int getItemCount() {
        return currentUsersList.size();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull UserListViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull UserListViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }
}
