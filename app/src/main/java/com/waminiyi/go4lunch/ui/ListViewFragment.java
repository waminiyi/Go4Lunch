package com.waminiyi.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.adapter.RestaurantAdapter;
import com.waminiyi.go4lunch.databinding.FragmentListViewBinding;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.Restaurant;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;
import com.waminiyi.go4lunch.viewmodel.RestaurantViewModel;
import com.waminiyi.go4lunch.viewmodel.ReviewViewModel;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ListViewFragment extends Fragment implements RestaurantAdapter.ClickListener,
        FirebaseHelper.ReviewListener, FirebaseHelper.LunchListener {
    private RestaurantViewModel restaurantViewModel;
    private ReviewViewModel reviewViewModel;
    private LunchViewModel lunchViewModel;
    private UserViewModel userViewModel;
    private List<Restaurant> currentRestaurantList = new ArrayList<>();
    private RestaurantAdapter restaurantAdapter;

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
        restaurantAdapter = new RestaurantAdapter(currentRestaurantList, this);
        binding.restaurantRecyclerView.setAdapter(restaurantAdapter);
        restaurantViewModel =
                new ViewModelProvider(requireActivity()).get(RestaurantViewModel.class);
        lunchViewModel = new ViewModelProvider(requireActivity()).get(LunchViewModel.class);
        reviewViewModel = new ViewModelProvider(requireActivity()).get(ReviewViewModel.class);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        this.observeData();

        return binding.getRoot();
    }

    @Override
    public void onFavoriteButtonClick(Restaurant restaurant) {
        MainActivity activity = (MainActivity) requireActivity();
        activity.updateUserFavorites(restaurant.getId());

    }

    @Override
    public void onRestaurantClick(Restaurant restaurant) {

        ListViewFragmentDirections.ListToLunchAction action =
                ListViewFragmentDirections.listToLunchAction(restaurant);
        NavHostFragment.findNavController(this).navigate(action);
    }


    @Override
    public void onRatingsUpdate(DocumentSnapshot ratingsDoc) {
        restaurantViewModel.updateRestaurantsWithRating(ratingsDoc);
    }

    @Override
    public void onReviewsUpdate() {
    }

    @Override
    public void onLunchesUpdate(DocumentSnapshot lunchesDoc) {

    }

    @Override
    public void onLunchesCountUpdate(DocumentSnapshot lunchesCountDoc) {
        restaurantViewModel.updateRestaurantsWithLunchesCount(lunchesCountDoc);
    }

    private void observeData() {
        reviewViewModel.setReviewListener(this);
        lunchViewModel.setLunchListener(this);
        lunchViewModel.listenToLunches();
        lunchViewModel.listenToLunchesCount();
        reviewViewModel.listenToRatings();
        restaurantViewModel.getRestaurantLiveList().observe(getViewLifecycleOwner(), restaurantList -> {
            currentRestaurantList = restaurantList;
            restaurantAdapter.updateRestaurants(currentRestaurantList);
        });
    }
}