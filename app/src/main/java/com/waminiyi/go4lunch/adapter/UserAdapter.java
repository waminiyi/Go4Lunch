package com.waminiyi.go4lunch.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private List<User> userList;
    /**
     * Listener for Click events
     */
    private final ClickListener eventListener;


    public UserAdapter(@NonNull final List<User> users,
                       ClickListener eventListener) {
        this.userList = users;
        this.eventListener = eventListener;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateUsers(@NonNull final List<User> users) {
        this.userList = users;
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent,
                        false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        holder.bind(userList.get(position));
        holder.setListeners(eventListener);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public void onViewRecycled(@NonNull UserViewHolder holder) {
        super.onViewRecycled(holder);
        holder.removeListeners();
    }

    public interface ClickListener {
        /**
         * Called when the user view  is clicked
         *
         * @param position : position of the user clicked
         */
        void onUserClick(int position);

    }

    public User getItemAt(int position){
        return userList.get(position);
    }
}
