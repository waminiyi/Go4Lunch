package com.waminiyi.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.adapter.RestaurantListAdapter;
import com.waminiyi.go4lunch.databinding.FragmentListViewBinding;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.util.RestaurantClickListener;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;
import com.waminiyi.go4lunch.viewmodel.RestaurantViewModel;
import com.waminiyi.go4lunch.viewmodel.ReviewViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ListViewFragment extends Fragment implements RestaurantClickListener,
        FirebaseHelper.ReviewListener, FirebaseHelper.LunchListener {
    private RestaurantViewModel restaurantViewModel;
    private ReviewViewModel reviewViewModel;
    private LunchViewModel lunchViewModel;
    private List<Restaurant> currentRestaurantList = new ArrayList<>();
    private RestaurantListAdapter restaurantAdapter;

    public ListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        FragmentListViewBinding binding =
                FragmentListViewBinding.inflate(inflater, container, false);
        binding.restaurantRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        restaurantAdapter = new RestaurantListAdapter(currentRestaurantList, this);
        binding.restaurantRecyclerView.setAdapter(restaurantAdapter);
        restaurantViewModel =
                new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        lunchViewModel = new ViewModelProvider(requireActivity()).get(LunchViewModel.class);
        reviewViewModel = new ViewModelProvider(requireActivity()).get(ReviewViewModel.class);
        this.observeData();

        return binding.getRoot();
    }

    @Override
    public void onFavoriteButtonClick(Restaurant restaurant) {
        Toast.makeText(requireContext(), restaurant.getName() + " added to favorite ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRestaurantClick(Restaurant restaurant) {

        ListViewFragmentDirections.ListToLunchAction action =
                ListViewFragmentDirections.listToLunchAction(restaurant);
        NavHostFragment.findNavController(this).navigate(action);
    }


    @Override
    public void onRatingsUpdate(DocumentSnapshot ratingsDoc) {
        restaurantViewModel.updateRestaurantsWithRating();
    }

    @Override
    public void onReviewsUpdate() {
    }

    @Override
    public void onLunchesUpdate(DocumentSnapshot lunchesDoc) {
        restaurantViewModel.updateRestaurantsWithLunches();
    }

    private void observeData() {
        reviewViewModel.setReviewListener(this);
        lunchViewModel.setLunchListener(this);
        lunchViewModel.listenToLunches();
        reviewViewModel.listenToRatings();
        restaurantViewModel.getRestaurantLiveList().observe(getViewLifecycleOwner(), restaurantList -> {
            currentRestaurantList = restaurantList;
            restaurantAdapter.updateRestaurants(currentRestaurantList);
        });
    }
}