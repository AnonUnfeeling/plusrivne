package com.example.jdroidcoder.directory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class RajonQuery {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private Context context;

    public RajonQuery(Context context) {
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
        mapData.put("rajon", context.getResources().getString(R.string.rajonQuery));

        Call<Object> routeResponse = iQuery.query(mapData);

        try {
            final Response<Object> objectResponse = routeResponse.execute();

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
                    context.openFileOutput(context.getResources().getString(R.string.rajon), Context.MODE_PRIVATE));
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
