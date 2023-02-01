package com.waminiyi.go4lunch.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.adapter.RestaurantListAdapter;
import com.waminiyi.go4lunch.adapter.UserListAdapter;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.model.User;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;
import com.waminiyi.go4lunch.viewmodel.RestaurantViewModel;

import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkmatesFragment} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class WorkmatesFragment extends Fragment {

    private LunchViewModel lunchViewModel;
    private List<User> currentUserList;
    private RecyclerView recyclerView;
    private UserListAdapter userAdapter;

    public WorkmatesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_workmates, container, false);
        lunchViewModel =
                new ViewModelProvider(requireActivity()).get(LunchViewModel.class);

        lunchViewModel.updateUsersList();
        recyclerView = view.findViewById(R.id.users_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        userAdapter = new UserListAdapter();
        recyclerView.setAdapter(userAdapter);

        lunchViewModel.getUsersLunches().observe(getViewLifecycleOwner(), userList -> {
            currentUserList=userList;
            userAdapter.updateUsers(currentUserList);
        });

        return view;
    }
}