package com.waminiyi.go4lunch.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.adapter.RestaurantListAdapter;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.util.RestaurantClickListener;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;
import com.waminiyi.go4lunch.viewmodel.RestaurantViewModel;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ListViewFragment extends Fragment implements RestaurantClickListener {
    private RestaurantViewModel restaurantViewModel;
    private List<Restaurant> currentRestaurantList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RestaurantListAdapter restaurantAdapter;
    private LunchViewModel lunchViewModel;
    private UserViewModel mUserViewModel;


    public ListViewFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View view = inflater.inflate(R.layout.fragment_list_view, container, false);
        recyclerView = view.findViewById(R.id.restaurant_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        restaurantAdapter = new RestaurantListAdapter(currentRestaurantList, this);
        recyclerView.setAdapter(restaurantAdapter);
        restaurantViewModel =
                new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);

        restaurantViewModel.getRestaurantLiveList().observe(getViewLifecycleOwner(), restaurantList -> {
            currentRestaurantList = restaurantList;
            restaurantAdapter.updateRestaurants(currentRestaurantList);
        });
        lunchViewModel = new ViewModelProvider(this).get(LunchViewModel.class);
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        return view;
    }

    @Override
    public void onFavoriteButtonClick(Restaurant restaurant) {
        Toast.makeText(requireContext(), restaurant.getName() + " added to favorite ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLunchTextViewClick(Restaurant restaurant) {
        Toast.makeText(requireContext(), restaurant.getName() + " added as lunch ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRestaurantClick(Restaurant restaurant) {

        ListViewFragmentDirections.ListToLunchAction action =
                ListViewFragmentDirections.listToLunchAction(restaurant);
        NavHostFragment.findNavController(this).navigate(action);

    }
}