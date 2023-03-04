package com.waminiyi.go4lunch.model;

import androidx.annotation.NonNull;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class NearbyPlaceSearchResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    /** The list of Search Results. */
    @SerializedName("results")
    public NearbyPlaceSearchResult[] results;

    /** Attributions about this listing which must be displayed to the user. */
    @SerializedName("html_attributions")
    public String[] htmlAttributions;

    /**
     * A token that can be used to request up to 20 additional results. This field will be null if
     * there are no further results. The maximum number of results that can be returned is 60.
     *
     * <p>Note: There is a short delay between when this response is issued, and when nextPageToken
     * will become valid to execute.
     */
    @SerializedName("next_page_token")
    public String nextPageToken;

    @NonNull
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[PlacesSearchResponse: ");
        sb.append(results.length).append(" results");
        if (nextPageToken != null) {
            sb.append(", nextPageToken=").append(nextPageToken);
        }
        if (htmlAttributions != null && htmlAttributions.length > 0) {
            sb.append(", ").append(htmlAttributions.length).append(" htmlAttributions");
        }
        sb.append("]");
        return sb.toString();
    }
}
