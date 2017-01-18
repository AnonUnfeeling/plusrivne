package com.example.jdroidcoder.directory;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class SelectCategoryListActivity extends AppCompatActivity {
    private NameListViewAdapret nameListViewAdapret;
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private JSONClientsModel jsonModel;
    private HashSet<ModelNameList> set = new HashSet<>();
    private LinkedList<ModelNameList> tops = new LinkedList<>();
    private ListView listView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_category);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int width = displayMetrics.widthPixels;
        final int height = displayMetrics.heightPixels;

        initNameList();

        jsonModel = gson.fromJson(getIntent().getStringExtra("clients"), JSONClientsModel.class);

        shearchTops();

        System.out.println(getIntent().getStringExtra("category_id"));

        new Thread(new Runnable() {
            @Override
            public void run() {
                ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
                viewPager.setAdapter(new ImageAdapter(SelectCategoryListActivity.this, width, height,
                        getIntent().getStringExtra("category_id"), viewPager));
            }
        }).start();

    }

    private void shearchTops() {
        LinkedList<ModelNameList> modelsClients = new LinkedList<>(jsonModel.getList());

        String idCategory = getIntent().getStringExtra("category_id");

        for (int i = 0; i < modelsClients.size(); i++) {
            try {
                if (isTop(modelsClients.get(i).getTop(), idCategory)) {
                    tops.add(modelsClients.get(i));
                } else {
                    set.add(modelsClients.get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        createTop();
    }

    private boolean isTop(String[] categoryTop, String idCategory) {
        for (int i = 0; i < categoryTop.length; i++) {
            if (categoryTop[i].equals(idCategory)) {
                return true;
            }
        }
        return false;
    }

    private void createTop() {
        if (set.size() > 0) {
            jsonModel.setList(set);
        }

        nameListViewAdapret = new NameListViewAdapret(this, jsonModel);

        listView.setAdapter(nameListViewAdapret);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "" + parent.getItemAtPosition(position),
                        Toast.LENGTH_LONG).show();
            }
        });

        nameListViewAdapret.addTops(tops);
    }

    private void initNameList() {
        this.setTitle(getIntent().getStringExtra("title"));
        listView = (ListView) findViewById(R.id.name_list);
    }
}
