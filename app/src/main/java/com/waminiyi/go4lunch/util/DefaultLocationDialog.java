package com.waminiyi.go4lunch.util;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.waminiyi.go4lunch.R;

import java.util.Arrays;
import java.util.Objects;

/**Dialog fragment that display place autocomplete fragment for place research
 *
 */
public class DefaultLocationDialog extends DialogFragment {

    private LocationDialogListener locationDialogListener;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        View view=inflater.inflate(R.layout.autocomplete_dialog_layout, container);
        // Initialize the AutocompleteSupportFragment.
        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getChildFragmentManager().findFragmentById(R.id.autocomplete_fragment);
        FragmentActivity activity=getActivity();
        // Specify the types of place data to return.
        if (autocompleteFragment != null) {
            autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME));
            // Set up a PlaceSelectionListener to handle the response.
            autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                @Override
                public void onPlaceSelected(@NonNull Place place) {
//                    if (activity!=null){
//                        ((LocationDialogListener)activity).onLocationSelected(place);
//                    }
                    locationDialogListener.onLocationSelected(place);
//                    dismiss();

                }

                @Override
                public void onError(@NonNull Status status) {
//                    if (activity!=null){
//                        ((LocationDialogListener)activity).onError(status);
//                    }
                        locationDialogListener.onError(status);
                }
            });
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        //Setting the dialog width and height
        int width = getResources().getDisplayMetrics().widthPixels;
        int height =getResources().getDisplayMetrics().heightPixels/2;

        Objects.requireNonNull(getDialog()).getWindow().setLayout(width, height);
    }

    /**Interface that should be implemented by the activity that shows the dialog
     * It listen to the place selection and pass the result to the activity
     */
    public interface LocationDialogListener{

         void onLocationSelected(@NonNull Place place);
         void onError(@NonNull Status status);
    }

    public void setLocationDialogListener(LocationDialogListener listener) {
        this.locationDialogListener = listener;
    }
}
