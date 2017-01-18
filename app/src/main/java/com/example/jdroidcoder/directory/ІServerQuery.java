package com.example.jdroidcoder.directory;

import java.util.Map;

import retrofit.Call;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.Multipart;
import retrofit.http.POST;

public interface ІServerQuery {
    @FormUrlEncoded
    @POST("/api/getdata.php")
    Call<Object> query(@FieldMap Map<String,String> link);
}
