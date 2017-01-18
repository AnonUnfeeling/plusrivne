package com.example.jdroidcoder.directory;

import java.util.Map;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.QueryMap;

public interface MapQuery {
    @GET("/maps/api/geocode/json")
    Call<Object> send (@QueryMap Map<String,String> map);
}
