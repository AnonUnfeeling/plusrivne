package com.example.jdroidcoder.directory;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class CategoryListViewAdapter extends BaseAdapter implements Filterable {
    private Context context;
    private List<ModelCategoryList> set;
    private List<ModelCategoryList> originSet;
    private LayoutInflater layoutInflater;
    private Filter planetFilter;
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private ArrayList<ModelNameList> modelNameLists;
    private JSONClientsModel jsonModelClients = new JSONClientsModel();
    private TextView category;
    private JSONClientsModel jsonModel;
    private String constraint;

    public CategoryListViewAdapter(Context context, Set<ModelCategoryList> set, JSONClientsModel jsonClientsModel) {
        this.context = context;
        this.jsonModel = jsonClientsModel;
        this.set = new LinkedList<>(set);

        Collections.sort(this.set, new Comparator<ModelCategoryList>() {
            @Override
            public int compare(ModelCategoryList lhs, ModelCategoryList rhs) {
                Collator сollator = Collator.getInstance(new Locale("ua", "UA"));
                сollator.setStrength(Collator.PRIMARY);
                return сollator.compare(lhs.getCATEGORY(), rhs.getCATEGORY());
            }
        });

        originSet = new LinkedList<>(set);

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return set.size();
    }

    @Override
    public Object getItem(int position) {
        return set.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.category_list_style, parent, false);

        category = (TextView) convertView.findViewById(R.id.category);

        if (planetFilter != null) {
            if (set.get(position).getCATEGORY().toUpperCase().contains(constraint.toUpperCase())) {
                category.setTextColor(context.getResources().getColor(R.color.colorSearch));
            } else if (searchByTags(set.get(position).getCat_tags(), constraint.toUpperCase())) {
                category.setTextColor(context.getResources().getColor(R.color.colorSearch));
            }
        }

        category.setText(set.get(position).getCATEGORY());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String category = set.get(position).getCATEGORY();

                modelNameLists = new ArrayList<>();

                try {
                    LinkedList<ModelNameList> set = new LinkedList<>(jsonModel.getList());

                    for (int j = 0; j < set.size(); j++) {
                        ModelNameList modelNameList = set.get(j);
                        try {
                            if (checkCategory(modelNameList, category)) {
                                if ("Газова служба".contains(modelNameList.getName())) {
                                    modelNameList.setIco(R.mipmap.gas);
                                } else if ("Пожежна допомога".contains(modelNameList.getName())) {
                                    modelNameList.setIco(R.mipmap.fire);
                                } else if ("Поліція".contains(modelNameList.getName())) {
                                    modelNameList.setIco(R.mipmap.police);
                                } else if ("Швидка допомога".contains(modelNameList.getName())) {
                                    modelNameList.setIco(R.mipmap.medical);
                                }

                                modelNameLists.add(modelNameList);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    jsonModelClients.setList(new HashSet<>(modelNameLists));
                    context.startActivity(new Intent(context, SelectCategoryListActivity.class).putExtra("clients", gson.toJson(jsonModelClients))
                            .putExtra("title", category)
                            .putExtra("category_id", String.valueOf(CategoryListViewAdapter.this.set.get(position).getId())));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return convertView;
    }

    private boolean checkCategory(ModelNameList modelNameList, String category) {
        for (int i = 0; i < modelNameList.getCategory().length; i++) {
            if (category.equals(modelNameList.getCategory()[i])) {
                return true;
            }
        }
        return false;
    }

    public void resetData() {
        planetFilter = null;
        set = originSet;
    }

    @Override
    public Filter getFilter() {
        if (planetFilter == null)
            planetFilter = new PlanetFilter();

        return planetFilter;
    }

    private class PlanetFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            CategoryListViewAdapter.this.constraint = constraint.toString();
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = set;
                results.count = set.size();
            } else {
                List<ModelCategoryList> nPlanetList = new ArrayList<>();

                for (ModelCategoryList p : set) {
                    if (p.getCATEGORY().toUpperCase().contains(constraint.toString().toUpperCase())) {
                        nPlanetList.add(p);
                    } else if (searchByTags(p.getCat_tags(), constraint.toString())) {
                        nPlanetList.add(p);
                    }
                }

                results.values = nPlanetList;
                results.count = nPlanetList.size();

            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                set = (List<ModelCategoryList>) results.values;
                notifyDataSetChanged();
            }
        }
    }

    private boolean searchByTags(String[] arr, String tag) {
        for (String s : arr) {
            if (s.toUpperCase().contains(tag.toUpperCase()))
                return true;
        }
        return false;
    }
}