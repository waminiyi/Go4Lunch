package com.waminiyi.go4lunch.ui;

import android.os.Bundle;

import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.firebase.auth.UserProfileChangeRequest;
import com.waminiyi.go4lunch.R;
import com.waminiyi.go4lunch.util.Constants;
import com.waminiyi.go4lunch.viewmodel.UserViewModel;

public class SettingsFragment extends PreferenceFragmentCompat {

    private UserViewModel mUserViewModel;

    public SettingsFragment() {
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        mUserViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        EditTextPreference namePref = findPreference(Constants.NAME);
        if (namePref != null) {
            namePref.setText(mUserViewModel.getCurrentUser().getDisplayName());
            namePref.setOnPreferenceChangeListener((preference, newValue) -> {
                if (newValue != null) {
                    mUserViewModel.updateUserName((String) newValue);
                    UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                            .setDisplayName((String) newValue)
                            .build();
                    mUserViewModel.updateProfile(profileUpdates);
                }
                return true;
            });
        }
    }

}