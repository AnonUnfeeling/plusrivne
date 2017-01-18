package com.example.jdroidcoder.directory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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

public class CategotyListFragment extends Fragment {

    private ListView categoryList;
    private Set<ModelCategoryList> list;
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    public static CategoryListViewAdapter categoryListViewAdapter;
    private JSONClientsModel jsonModel;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.categoty_list_fragment, container, false);

        jsonModel = (JSONClientsModel) getArguments().getSerializable("clients");

        categoryList = (ListView) view.findViewById(R.id.categoty_list);

        try {
            JSONCategoryModel jsonCategoryModel = gson.fromJson(
                    readFromFile(getResources().getString(R.string.allCategories)), JSONCategoryModel.class);

            list = new HashSet<>(jsonCategoryModel.getList());

            categoryListViewAdapter = new CategoryListViewAdapter(getActivity(), list, jsonModel);

            categoryList.setAdapter(categoryListViewAdapter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }

    private String readFromFile(String file) {

        String ret = "";

        try {
            InputStream inputStream = getActivity().openFileInput(file);

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
