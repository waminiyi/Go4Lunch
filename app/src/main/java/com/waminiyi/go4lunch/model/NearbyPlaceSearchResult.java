package com.waminiyi.go4lunch.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;
import com.google.maps.model.Geometry;

import java.io.Serializable;
import java.util.Arrays;

public class NearbyPlaceSearchResult implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Geometry information about the result, generally including the location (geocode) of the place
     * and (optionally) the viewport identifying its general area of coverage.
     */
    @SerializedName("geometry")
    public Geometry geometry;

    /**
     * The human-readable name for the returned result. For establishment results, this is usually the
     * business name.
     */
    @SerializedName("name")
    public String name;

    /** A textual identifier that uniquely identifies a place. */
    @SerializedName("place_id")
    public String placeId;

    /** Information on when the place is open. */
    @SerializedName("opening_hours")
    public OpeningHours openingHours;

    /** Photo objects associated with this place, each containing a reference to an image. */
    @SerializedName("photos")
    public Photo[] photos;

    /** A feature name of a nearby location. */
    @SerializedName("vicinity")
    public String vicinity;

    /** Indicates that the place has permanently shut down. */
    public boolean permanentlyClosed;


    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[PlacesSearchResult: ");
        sb.append("\"").append(name).append("\"");
        sb.append(", geometry=").append(geometry);
        sb.append(", placeId=").append(placeId);
        if (vicinity != null) {
            sb.append(", vicinity=").append(vicinity);
        }
        if (openingHours != null) {
            sb.append(openingHours);
        }
        if (photos != null && photos.length > 0) {
            sb.append(", ").append(photos.length).append(" photos");
        }
        if (permanentlyClosed) {
            sb.append(", permanentlyClosed");

        }

        sb.append("]");
        return sb.toString();
    }

}
