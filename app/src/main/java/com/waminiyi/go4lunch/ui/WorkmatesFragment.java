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
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;

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
    private List<Lunch> currentLunchList = new ArrayList<>();
    private LunchAdapter userAdapter;

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

        FragmentWorkmatesBinding binding =
                FragmentWorkmatesBinding.inflate(inflater, container, false);

        lunchViewModel =
                new ViewModelProvider(requireActivity()).get(LunchViewModel.class);

        binding.usersRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        String TAG = "WorkmatesFragment";
        userAdapter = new LunchAdapter(currentLunchList, TAG, this);
        binding.usersRecyclerView.setAdapter(userAdapter);
        this.observeData();

        return binding.getRoot();
    }

    @Override
    public void onLunchClick(Lunch lunch) {
        Toast.makeText(requireContext(), lunch.getUserName() + " clicked ",
                Toast.LENGTH_SHORT).show();
    }

    private void observeData() {
        lunchViewModel.setLunchListener(this);
        lunchViewModel.listenToLunches();
        lunchViewModel.getUsersLunches().observe(getViewLifecycleOwner(), lunchList -> {
            currentLunchList = lunchList;
            userAdapter.updateLunches(currentLunchList);
        });
    }

    @Override
    public void onLunchesUpdate(DocumentSnapshot lunchesDoc) {
        lunchViewModel.parseLunchesDoc(lunchesDoc);
    }

    @Override
    public void onLunchesCountUpdate(DocumentSnapshot lunchesCountDoc) {

    }
}