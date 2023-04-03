package com.waminiyi.go4lunch.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.waminiyi.go4lunch.R;
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
    SearchView searchView;

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

        MenuHost menuHost = requireActivity();
        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                searchView = (SearchView) menu.findItem(R.id.search_workmate).getActionView();
                searchView.setIconified(false);
                searchView.setQueryHint(getString(R.string.find_workmate));

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        usersLunchAdapter.getFilter().filter(query);
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.trim().length() > 0) {
                            usersLunchAdapter.getFilter().filter(newText);
                        } else {
                            usersLunchAdapter.updateLunches(currentUsersLunchList);
                        }
                        return true;
                    }
                });
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                return true;
            }
        });

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
        UserLunch userLunch = usersLunchAdapter.getItemAt(position);

        if (userLunch.getRestaurantId() != null) {
            ((MainActivity) requireActivity()).openDetails(userLunch.getRestaurantId(),
                    userLunch.getRestaurantName(), null, null);
        }
    }

}