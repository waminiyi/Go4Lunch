package com.waminiyi.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.adapter.LunchAdapter;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.viewmodel.LunchViewModel;

import java.util.ArrayList;
import java.util.List;

public class LunchFragment extends Fragment implements LunchAdapter.ClickListener {

    private LunchViewModel lunchViewModel;
    private List<Lunch> currentLunchList = new ArrayList<>();
    private RecyclerView recyclerView;
    private TextView tv;
    private LunchAdapter userAdapter;
    private final String TAG = "DetailsFragment";


    public LunchFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_lunch, container, false);
        lunchViewModel =
                new ViewModelProvider(requireActivity()).get(LunchViewModel.class);
        tv = view.findViewById(R.id.no_lunch);

        recyclerView = view.findViewById(R.id.restaurant_lunch_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        userAdapter = new LunchAdapter(currentLunchList, TAG, this);
        recyclerView.setAdapter(userAdapter);

        lunchViewModel.getCurrentRestaurantLunches().observe(getViewLifecycleOwner(),
                lunchList -> {
                    currentLunchList = lunchList;
                    updateLunches();
                });

        return view;
    }

    private void updateLunches() {
        if (currentLunchList.size() == 0) {
            tv.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            userAdapter.updateLunches(currentLunchList);
        }
    }

    @Override
    public void onLunchClick(Lunch lunch) {

    }
}