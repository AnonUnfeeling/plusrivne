package com.example.jdroidcoder.directory;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.Map;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class CategorysQuery {
    private Context context;

    public CategorysQuery(Context context) {
        this.context = context;

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
        mapData.put("info", "categories");
        mapData.put("town",context.getResources().getString(R.string.categoriesQuery));

        Call<Object> routeResponse = iQuery.query(mapData);

        try {
            final Response<Object> objectResponse = routeResponse.execute();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    saveAll(objectResponse.body().toString());
                }
            }).start();

        } catch (IOException | StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    private void saveAll(String json) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(context.getResources().getString(R.string.allCategories), Context.MODE_PRIVATE));
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
