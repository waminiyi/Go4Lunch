package com.waminiyi.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.adapter.LunchAdapter;
import com.waminiyi.go4lunch.databinding.FragmentWorkmatesBinding;
import com.waminiyi.go4lunch.helper.FirebaseHelper;
import com.waminiyi.go4lunch.model.UserLunch;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;
import com.waminiyi.go4lunch.viewmodel.StateViewModel;

import java.util.ArrayList;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkmatesFragment} factory method to
 * create an instance of this fragment.
 */
@AndroidEntryPoint
public class WorkmatesFragment extends Fragment implements LunchAdapter.ClickListener, FirebaseHelper.LunchListener {

    private LunchViewModel lunchViewModel;
    private StateViewModel mStateViewModel;
    private List<UserLunch> currentUsersLunchList = new ArrayList<>();
    private LunchAdapter usersLunchAdapter;
    private LinearLayoutManager layoutManager;
    private FragmentWorkmatesBinding binding;

    public WorkmatesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);

        lunchViewModel = new ViewModelProvider(requireActivity()).get(LunchViewModel.class);
        mStateViewModel = new ViewModelProvider(requireActivity()).get(StateViewModel.class);
        layoutManager = new LinearLayoutManager(requireContext());
        binding.usersRecyclerView.setLayoutManager(layoutManager);
        usersLunchAdapter = new LunchAdapter(currentUsersLunchList, this);
        binding.usersRecyclerView.setAdapter(usersLunchAdapter);
        this.observeData();

        return binding.getRoot();
    }

    private void observeData() {
        lunchViewModel.setLunchListener(this);
        lunchViewModel.listenToLunches();
        lunchViewModel.getAllUsersLunches().observe(getViewLifecycleOwner(), lunchList -> {
            currentUsersLunchList = lunchList;
            usersLunchAdapter.updateLunches(currentUsersLunchList);
            if (mStateViewModel.getSavedUserListPosition() != 0) {
                binding.usersRecyclerView.scrollToPosition(mStateViewModel.getSavedUserListPosition());
            }
        });
    }

    @Override
    public void onLunchesUpdate(DocumentSnapshot lunchesDoc) {
        lunchViewModel.parseLunchesDoc(lunchesDoc);
    }

    @Override
    public void onLunchesCountUpdate(DocumentSnapshot lunchesCountDoc) {

    }

    @Override
    public void onStop() {
        super.onStop();
        mStateViewModel.saveUserListPosition(layoutManager.findFirstVisibleItemPosition());
    }

    @Override
    public void onLunchClick(int position) {
        Toast.makeText(requireContext(), usersLunchAdapter.getItemAt(position).getUserName() + " clicked ",
                Toast.LENGTH_SHORT).show();

        ((MainActivity) requireActivity()).openDetails(usersLunchAdapter.getItemAt(position).getRestaurantId(), null);
    }
}