package com.waminiyi.go4lunch.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.adapter.LunchListAdapter;
import com.waminiyi.go4lunch.model.Lunch;
import com.waminiyi.go4lunch.util.LunchClickListener;
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
public class WorkmatesFragment extends Fragment implements LunchClickListener {

    private LunchViewModel lunchViewModel;
    private List<Lunch> currentLunchList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LunchListAdapter userAdapter;
    private final String TAG = "WorkmatesFragment";

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

        recyclerView = view.findViewById(R.id.users_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        userAdapter = new LunchListAdapter(currentLunchList,TAG,this);
        recyclerView.setAdapter(userAdapter);

        lunchViewModel.getUsersLunches().observe(getViewLifecycleOwner(), lunchList -> {
            currentLunchList =lunchList;
            userAdapter.updateLunches(currentLunchList);
        });

        return view;
    }

    @Override
    public void onLunchClick(Lunch lunch) {
        Toast.makeText(requireContext(), lunch.getUserName() + " clicked ",
                Toast.LENGTH_SHORT).show();
    }
}