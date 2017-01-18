package com.example.jdroidcoder.directory;

import android.content.Context;

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

public class AdsQuery {
    private Context context;

    public AdsQuery(Context context) {
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

    public AdsModel accept() {
        Map<String, String> mapData = new LinkedTreeMap<>();
        mapData.put("info", "ads");
        mapData.put("town",context.getResources().getString(R.string.clientsQuery));

        Call<Object> routeResponse = iQuery.query(mapData);

        try {
            Response<Object> objectResponse = routeResponse.execute();

            System.out.println("ads: "+objectResponse.body().toString());

            saveAll(objectResponse.body().toString());

            return gson.fromJson(objectResponse.body().toString(), AdsModel.class);
        } catch (IOException | StringIndexOutOfBoundsException e) {
            return null;
        }
    }

    private void saveAll(String json) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(context.getResources().getString(R.string.ads), Context.MODE_PRIVATE));
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
