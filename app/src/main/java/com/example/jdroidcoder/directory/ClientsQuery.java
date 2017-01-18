package com.example.jdroidcoder.directory;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.TimeUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class ClientsQuery {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private Context context;

    public ClientsQuery(Context context) {
        this.context = context;

        restAdapter = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(context.getResources().getString(R.string.base_url))
                .build();

        iQuery = restAdapter.create(ІServerQuery.class);
    }

    private final Retrofit restAdapter;
    private final ІServerQuery iQuery;

    public JSONClientsModel accept() {
        Map<String, String> mapData = new LinkedTreeMap<>();
        mapData.put("info", "clients");
        mapData.put("town", context.getResources().getString(R.string.clientsQuery));

        Call<Object> routeResponse = iQuery.query(mapData);

        final Response<Object> objectResponse;
        try {
            objectResponse = routeResponse.execute();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        saveAll(objectResponse.body().toString());
                    } catch (OutOfMemoryError | RuntimeException e) {
                        e.getMessage();
                    }
                }
            }).start();

            return gson.fromJson(objectResponse.body().toString(), JSONClientsModel.class);
        } catch (IOException | OutOfMemoryError | com.google.gson.JsonSyntaxException e) {
            return null;
        }
    }

    private void saveAll(String json) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(context.getResources().getString(R.string.allClients), Context.MODE_PRIVATE));
            outputStreamWriter.write(json);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
