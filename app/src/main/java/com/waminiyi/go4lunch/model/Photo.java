package com.waminiyi.go4lunch.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * Used to identify the photo when you perform a Photo request.
     */
    @SerializedName("photo_reference")
    public String photoReference;

    /**
     * The maximum height of the image.
     */
    public int height;

    /**
     * The maximum width of the image.
     */
    public int width;

    /**
     * Attributions about this listing which must be displayed to the user.
     */
    public String[] htmlAttributions;

    @Override
    public String toString() {
        String str = String.format("[Photo %s (%d x %d)", photoReference, width, height);
        if (htmlAttributions != null && htmlAttributions.length > 0) {
            str = str + " " + htmlAttributions.length + " attributions";
        }
        str = str + "]";
        return str;

    }
}
