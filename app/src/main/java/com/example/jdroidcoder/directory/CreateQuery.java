package com.example.jdroidcoder.directory;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.Map;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

public class CreateQuery {
    private Context context;
    private String message;
    private int key = 0;

    public CreateQuery(Context context, String message, int key) {
        this.context = context;
        this.message = message;
        this.key = key;

        restAdapter = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(context.getResources().getString(R.string.base_url))
                .build();

        iQuery = restAdapter.create(ІServerQuery.class);
    }

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final Retrofit restAdapter;

    private final ІServerQuery iQuery;

    public void accept() {
        Map<String, String> mapData = new LinkedTreeMap<>();
        if(key==0) {
            mapData.put("info", "create");
        }else if(key==1){
            mapData.put("info", "edit");
        }else if(key == 2){
            mapData.put("info", "callback");
        }else if(key == 3){
            mapData.put("info", "delete");
        }

        System.out.println(message);
        mapData.put("town", context.getResources().getString(R.string.clientsQuery));
        mapData.put("message", message);

        Call<Object> routeResponse = iQuery.query(mapData);

        try {
            routeResponse.execute();
            System.out.println("create sent");
        } catch (IOException | StringIndexOutOfBoundsException e) {

        }
    }
}
