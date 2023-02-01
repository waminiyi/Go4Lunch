package com.waminiyi.go4lunch.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.waminiyi.go4lunch.ui.LunchFragment;
import com.waminiyi.go4lunch.ui.ReviewFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(FragmentActivity activity) {
        super(activity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        Fragment fragment;
        if (position == 0)
            fragment = new LunchFragment();
        else
            fragment = new ReviewFragment();

        return fragment;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}
