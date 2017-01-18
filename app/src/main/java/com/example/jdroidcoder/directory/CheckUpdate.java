package com.example.jdroidcoder.directory;

import android.content.Context;
import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.LinkedTreeMap;

import java.io.BufferedReader;
import java.io.FileNotFoundException;

import java.io.IOException;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class CheckUpdate {
    private Context context;
    private final Gson gson = new GsonBuilder()
            .setDateFormat("HH:mm:ss' 'MM/dd/yyyy")
            .setPrettyPrinting()
            .create();

    private final Retrofit restAdapter;
    private final ІServerQuery iQuery;

    public CheckUpdate(Context context) {
        this.context = context;
        restAdapter = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(gson))
                .baseUrl(context.getResources().getString(R.string.base_url))
                .build();

        iQuery = restAdapter.create(ІServerQuery.class);
    }

    public JSONClientsModel accept() {

        Map<String, String> mapData = new LinkedTreeMap<>();
        mapData.put("info", "check_updates");
        String date = readFromFile();
        if (!date.isEmpty()) {
            mapData.put("date", date);
        }

        Call<Object> routeResponse = iQuery.query(mapData);

        try {
            Response<Object> objectResponse = routeResponse.execute();

            UpdateModel updateModel = gson.fromJson(objectResponse.body().toString(), UpdateModel.class);

            saveDate(updateModel.last_date);

            for (int i = 0; i < updateModel.getList().length; i++) {
                if ("categories".equals(updateModel.getList()[i])) {
                    new CategoriesUpdata().execute();
                }
                if ("clients".equals(updateModel.getList()[i])) {
                    new ClientsUpdata().execute();
                }
                if ("ads".equals(updateModel.getList()[i])) {
                    new AdsUpdata().execute();
                }
            }
        } catch (IOException | StringIndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void saveDate(String date) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(
                    context.openFileOutput(context.getResources().getString(R.string.date), Context.MODE_PRIVATE));
            outputStreamWriter.write(date);
            outputStreamWriter.flush();
            outputStreamWriter.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String readFromFile() {
        String ret = "";

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(context.openFileInput(context.getResources().getString(R.string.date)));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str;
            while ((str = bufferedReader.readLine()) != null) {
                ret += str;
            }
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return ret;
    }

    private class ClientsUpdata extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            new ClientsQuery(context).accept();

            return null;
        }
    }

    private class CategoriesUpdata extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            new CategorysQuery(context).accept();

            return null;
        }
    }

    private class AdsUpdata extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            new AdsQuery(context).accept();

            return null;
        }
    }
}
