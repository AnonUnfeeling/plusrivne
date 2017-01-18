package com.example.jdroidcoder.directory;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.TreeSet;

public class ImportantActivity extends AppCompatActivity {

    private ListView categoryList;
    private HashSet<ModelCategoryList> list = new HashSet<>();
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    public static CategoryListViewAdapter categoryListViewAdapter;
    private JSONClientsModel jsonModel;
    private int width, height;
    private ViewPager viewPager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.important_contact);

        try {
            jsonModel = (JSONClientsModel) getIntent().getSerializableExtra("clients");
            boolean isImportant = getIntent().getBooleanExtra("important", false);

            categoryList = (ListView) findViewById(R.id.categoty_list);

            viewPager = (ViewPager) findViewById(R.id.pager);

            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;

            JSONCategoryModel jsonCategoryModel = gson.fromJson(readFromFile(getResources().getString(R.string.allCategories)), JSONCategoryModel.class);

            for (ModelCategoryList category : jsonCategoryModel.getList()) {
                if (isImportant) {
                    if (category.getSpecial() == 2) {
                        list.add(category);
                    }

//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            viewPager.setAdapter(new ImageAdapter(ImportantActivity.this, width, height, "-4", viewPager));
//                        }
//                    }).start();
                } else {
                    if (category.getSpecial() == 1) {
                        list.add(category);
                    }
//
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                            viewPager.setAdapter(new ImageAdapter(ImportantActivity.this, width, height, "-5", viewPager));
//                        }
//                    }).start();
                }
            }

            categoryListViewAdapter = new CategoryListViewAdapter(this, list, jsonModel);

            categoryList.setAdapter(categoryListViewAdapter);
        } catch (Exception e) {
        }
    }

    private String readFromFile(String file) {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(file);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
}
