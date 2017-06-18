package com.kevguev.mobile.vidly.model.retrofit;

import com.kevguev.mobile.vidly.model.jsonpojo.videoids.Items;
import com.kevguev.mobile.vidly.model.jsonpojo.videos.Videos;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Kevin Guevara on 6/17/2017.
 */

public interface SearchEndpointInterface {

    @GET("search")
    Call<Items> getVideoIds(@Query("key") String key,
                            @Query("part") String part,
                            @Query("location") String location,
                            @Query("locationRadius") String radius,
                            @Query("maxResults") int maxResults,
                            @Query("publishedAfter") String publishedAfter,
                            @Query("type") String type,
                            @Query("fields") String fields);

    @GET("videos")
    Call<Videos> getVideos(@Query("key")String key,
                           @Query("part") String part,
                           @Query("id") String id);

}
