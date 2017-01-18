package com.example.jdroidcoder.directory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.util.Map;
import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MapLatLngSendQuery {
    private String address = "";

    public MapLatLngSendQuery() {
    }

    public MapLatLngSendQuery(String address) {
        this.address = address;
    }

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private final Retrofit restAdapter = new Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(gson))
            .baseUrl("https://maps.googleapis.com")
            .build();

    private final MapQuery iQuery = restAdapter.create(MapQuery.class);

    public String[] acceptQuest() {
        return accept();
    }

    private String[] accept() {

        Map<String, String> mapData = new LinkedTreeMap<>();
        mapData.put("address", address);
        mapData.put("key", "AIzaSyDjueRnyEDff1nvcznbO9eQEfI2ZQl1veM");

        Call<Object> routeResponse = iQuery.send(mapData);

        try {
            Response<Object> objectResponse = routeResponse.execute();

            String response = objectResponse.body().toString();

            int x = response.indexOf("location");
            int y = response.indexOf("location_type");
            String trimed = response.substring(x, y);
            int lat_last_x = trimed.indexOf("lat=");
            int lat_last_y = trimed.indexOf(", lng");
            String trimed_last = trimed.substring(lat_last_x, lat_last_y);
            String lat = trimed_last.replace("lat=", "");

            int lng_last_x = trimed.indexOf("lng=");
            int lng_last_y = trimed.indexOf("},");

            String lng = trimed.substring(lng_last_x, lng_last_y).replace("lng=", "");
            return new String[]{lat, lng};
        } catch (IOException | StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return new String[]{"", ""};
    }
}

