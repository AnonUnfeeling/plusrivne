package com.example.jdroidcoder.directory;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.Map;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class CheckNotificationQuery {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private Context context;
    private int id = 0;

    public CheckNotificationQuery(Context context, int id) {
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

    public CheckModel accept() {
        Map<String, String> mapData = new LinkedTreeMap<>();
        mapData.put("info", "check_available_messages");
        mapData.put("id", String.valueOf(id));
        mapData.put("town", context.getResources().getString(R.string.clientsQuery));

        Call<Object> routeResponse = iQuery.query(mapData);

        try {
            final Response<Object> objectResponse = routeResponse.execute();

            CheckModel checkModel = gson.fromJson(objectResponse.body().toString(), CheckModel.class);
            saveId(checkModel.id);

            return checkModel;

        } catch (IOException | OutOfMemoryError | com.google.gson.JsonSyntaxException | NullPointerException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void saveId(int id) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("id", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("id", id);
        editor.apply();
    }

    public class CheckModel {
        private int id;
        private int type;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
