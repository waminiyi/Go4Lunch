package com.waminiyi.go4lunch.api;

import com.waminiyi.go4lunch.model.NearbyPlaceSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface NearbyPlaceApi {
    String BASE_URL = "https://maps.googleapis.com";


    /**
     * return 20 nearby restaurants with restaurant type specified by the keyword like french, korean, etc
     *
     * @param keyword:restoration type
     * @param location:current    location
     * @param radius:search       range radius
     * @param placeType:          place type
     * @param apiKey:google       places api key
     * @return Call<NearbyPlaceSearchResponse>
     */
    @GET("/maps/api/place/nearbysearch/json")
    Call<NearbyPlaceSearchResponse> getNearbyPlaces(@Query("keyword") String keyword, @Query("location") String location,
                                                    @Query("radius") Integer radius, @Query("type") String placeType, @Query("key") String apiKey);

    /**
     * return 20 nearby restaurants in the location range
     *
     * @param location:current location
     * @param radius:search    range radius
     * @param placeType:       place type
     * @param apiKey:google    places api key
     * @return Call<NearbyPlaceSearchResponse>
     */
    @GET("/maps/api/place/nearbysearch/json")
    Call<NearbyPlaceSearchResponse> getNearbyPlaces(@Query("location") String location,
                                                    @Query("radius") Integer radius, @Query("type") String placeType, @Query("key") String apiKey);

    /**
     * return the next set of result using next_page_token
     *
     * @param nextPageToken:next page token
     * @param apiKey:google      places api key
     * @return Call<NearbyPlaceSearchResponse>
     */
    @GET("/maps/api/place/nearbysearch/json")
    Call<NearbyPlaceSearchResponse> getNearbyPlaces(@Query("pagetoken") String nextPageToken, @Query("key") String apiKey);


}
