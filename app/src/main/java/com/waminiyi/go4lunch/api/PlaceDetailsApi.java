package com.waminiyi.go4lunch.api;

import com.waminiyi.go4lunch.model.NearbyPlaceSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PlaceDetailsApi {

    String BASE_URL = "https://maps.googleapis.com";

    @GET("/maps/api/place/details/json")
    Call<NearbyPlaceSearchResponse> getPlaceDetails(@Query("fields") String keyword,
                                              @Query("place_id") String placeId,
                                                    @Query("radius") Integer radius, @Query("type") String placeType, @Query("key") String apiKey);
}
