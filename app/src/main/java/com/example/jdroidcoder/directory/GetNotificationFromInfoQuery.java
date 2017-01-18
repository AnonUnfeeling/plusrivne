package com.example.jdroidcoder.directory;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.Map;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by jdroidcoder on 06.10.16.
 */
public class GetNotificationFromInfoQuery {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private Context context;
    private int id;

    public GetNotificationFromInfoQuery(Context context, int id) {
        this.context = context;
        this.id = id;

        restAdapter = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(context.getResources().getString(R.string.base_url))
                .build();

        iQuery = restAdapter.create(ІServerQuery.class);
    }

    private final Retrofit restAdapter;

    private final ІServerQuery iQuery;

    public JSONNotificationModelFromInfo accept() {
        Map<String, String> mapData = new LinkedTreeMap<>();
        mapData.put("info", "get_messages");
        mapData.put("id", String.valueOf(id));
        System.out.println("info "+id);

        Call<Object> routeResponse = iQuery.query(mapData);

        try {
            final Response<Object> objectResponse = routeResponse.execute();

            System.out.println(objectResponse.body().toString());

            return gson.fromJson(objectResponse.body().toString(), JSONNotificationModelFromInfo.class);
        } catch (IOException | OutOfMemoryError | com.google.gson.JsonSyntaxException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }
}