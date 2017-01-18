package com.example.jdroidcoder.directory;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.google.gson.internal.LinkedTreeMap;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NameListViewAdapret extends BaseAdapter implements Filterable, SectionIndexer {

    private Context context;
    private String[] sections;
    private List<ModelNameList> set = new LinkedList<>();
    private List<ModelNameList> originSet = new LinkedList<>();
    private LayoutInflater layoutInflater;
    private Filter planetFilter;
    private LinkedTreeMap<String, Integer> mapIndex;
    private JSONClientsModel jsonModel;
    private TextView name, address;
    private ImageView ico;
    private int sizeTop;
    private String constraint;
    private String pre_search = "";
    private String fullAdress = "";

    public NameListViewAdapret(Context context, JSONClientsModel jsonModel) {
        this.context = context;

        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        try {
            this.jsonModel = jsonModel;
            sizeTop = -1;

            set = new LinkedList<>(jsonModel.getList());

            Collections.sort(set, new Comparator<ModelNameList>() {
                @Override
                public int compare(ModelNameList lhs, ModelNameList rhs) {
                    return lhs.getName().compareTo(rhs.getName().toString());
                }
            });

            originSet = set;

            mapIndex = new LinkedTreeMap<>();

            try {
                for (int x = 0; x < set.size(); x++) {
                    String fruit = set.get(x).getName();
                    String ch = fruit.substring(0, 1);
                    ch = ch.toUpperCase();

                    mapIndex.put(ch, x);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            Set<String> sectionLetters = mapIndex.keySet();

            ArrayList<String> sectionList = new ArrayList<>(sectionLetters);

            Collections.sort(sectionList);

            sections = new String[sectionList.size()];

            sectionList.toArray(sections);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    public void addTops(LinkedList<ModelNameList> tops) {
        set.addAll(0, tops);
        sizeTop = tops.size() - 1;
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.name_list_style, parent, false);

        final ModelNameList client = set.get(position);

        if (sizeTop == -1) {
            setData(convertView, position, client);
        } else {
            if (position == 0) {
                TextView top = (TextView) convertView.findViewById(R.id.top);
                top.setVisibility(View.VISIBLE);
            }
            if (position == sizeTop) {
                TextView another = (TextView) convertView.findViewById(R.id.another);
                another.setVisibility(View.VISIBLE);
            }
            setData(convertView, position, client);
        }

        return convertView;
    }

    private void setData(View convertView, int position, final ModelNameList client) {
        ico = (ImageView) convertView.findViewById(R.id.ico);

        try {
            Picasso.with(context).load(set.get(position).getAvatar()).into(ico, new Callback() {
                @Override
                public void onSuccess() {
                    ico.setBackgroundResource(R.drawable.imageview_personal_page_style_add);
                }

                @Override
                public void onError() {
                    ico.setBackgroundResource(R.drawable.imageview_personal_page_style);
                }
            });
        } catch (Exception e) {
            if (set.get(position).getIco() != 0) {
                ico.setImageResource(client.getIco());
                ico.setBackgroundResource(R.drawable.imageview_personal_page_style);
            }

            e.getMessage();
        }

        name = (TextView) convertView.findViewById(R.id.name);
        address = (TextView) convertView.findViewById(R.id.address);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, PersonalPageActivity.class)
                        .putExtra("person", client));
            }
        });
        String addr = "";
        try {
            addr = (!client.getTown().equals("м.Рівне")) ?
                    client.getTown() + ", " + client.getStreet() +
                            ((client.getBuild().isEmpty()) ? "" : ", "
                                    + client.getBuild()) + ((client.getAdd_address().isEmpty()) ?
                            "" : ", " + client.getAdd_address())
                    : client.getStreet() + ((client.getBuild().isEmpty()) ? "" : ", "
                    + client.getBuild()) + ((client.getAdd_address().isEmpty()) ?
                    "" : ", " + client.getAdd_address());

        } catch (Exception e) {

        }

        if (planetFilter != null) {
            String street = "", town = "", addAddress = "", build = "";

            try {
                street = set.get(position).getStreet().toUpperCase();
            } catch (Exception e) {

            }

            try {
                town = set.get(position).getTown().toUpperCase();
            } catch (Exception e) {

            }
            try {
                addAddress = set.get(position).getAdd_address();
            } catch (Exception e) {

            }
            try {
                build = set.get(position).getBuild().toUpperCase();
            } catch (Exception e) {

            }

            fullAdress = town + " " + street + " "
                    + build + " " + addAddress;
            try {
                if (client.getName().toUpperCase().contains(constraint.toString().toUpperCase())) {
                    this.name.setTextColor(context.getResources().getColor(R.color.colorSearch));
                } else if (client.getStreet().toUpperCase().contains(constraint.toString().toUpperCase())) {
                    address.setTextColor(context.getResources().getColor(R.color.headerBackground));
                } else if (client.getAlias().toUpperCase().contains(constraint.toString().toUpperCase())) {
                    this.name.setTextColor(context.getResources().getColor(R.color.colorSearch));
                } else if (client.getTown().toUpperCase().contains(constraint.toString().toUpperCase())) {
                    address.setTextColor(context.getResources().getColor(R.color.headerBackground));
                } else if (client.getAdd_address().toUpperCase().contains(constraint.toString().toUpperCase())) {
                    address.setTextColor(context.getResources().getColor(R.color.headerBackground));
                } else if (client.getSpecialize().toUpperCase().contains(constraint.toString().toUpperCase())) {
                    this.name.setTextColor(context.getResources().getColor(R.color.colorSearch));
                } else if (fullAdress.toUpperCase().contains(constraint.toString().toUpperCase())) {
                    address.setTextColor(context.getResources().getColor(R.color.headerBackground));
                }
            }catch (Exception e){

            }
        }

        name.setText(client.getName() + ((client.getSpecialize().isEmpty()) ? "" :
                ", " + client.getSpecialize()));
        address.setText(addr);
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

    @Override
    public Object[] getSections() {
        return sections;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        return (sectionIndex == 0) ? mapIndex.get(sections[sectionIndex]) : mapIndex.get(sections[sectionIndex - 1]);
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

    private class PlanetFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (pre_search.toCharArray().length < constraint.toString().toCharArray().length) {
                pre_search = constraint.toString();
            } else {
                set = originSet;
                pre_search = constraint.toString();
            }

            NameListViewAdapret.this.constraint = constraint.toString();
            FilterResults results = new FilterResults();
            if (constraint == null || constraint.length() == 0) {
                results.values = set;
                results.count = set.size();
            } else {
                List<ModelNameList> nPlanetList = new ArrayList<>();
                String name = "", street = "", alias = "", town = "", addAddress = "", build = "", spec ="";
                for (ModelNameList p : set) {
                    try {
                        try {
                            name = p.getName().toUpperCase();
                        } catch (Exception e) {

                        }

                        try {
                            street = p.getStreet().toUpperCase();
                        } catch (Exception e) {

                        }

                        try {
                            alias = p.getAlias().toUpperCase();
                        }catch (Exception e){

                        }

                        try {
                            town = p.getTown().toUpperCase();
                        } catch (Exception e) {

                        }

                        try {
                            addAddress = p.getAdd_address();
                        } catch (Exception e) {

                        }

                        try {
                            build = p.getBuild().toUpperCase();
                        } catch (Exception e) {

                        }

                        try {
                            spec = p.getSpecialize().toUpperCase();
                        } catch (Exception e) {

                        }

                        fullAdress = town + " " + street + " "
                                + build + " " + addAddress;

                        if (searchByTags(p.getTags(), constraint.toString())) {
                            nPlanetList.add(p);
                        } else if (name.contains(constraint.toString().toUpperCase())) {
                            nPlanetList.add(p);
                        }else if (spec.contains(constraint.toString().toUpperCase())) {
                            nPlanetList.add(p);
                        } else if (street.contains(constraint.toString().toUpperCase())) {
                            nPlanetList.add(p);
                        } else if (alias.contains(constraint.toString().toUpperCase())) {
                            nPlanetList.add(p);
                        } else if (town.contains(constraint.toString().toUpperCase())) {
                            nPlanetList.add(p);
                        } else if (addAddress.toUpperCase().contains(constraint.toString().toUpperCase())) {
                            nPlanetList.add(p);
                        } else if (build.contains(constraint.toString().toUpperCase())) {
                            nPlanetList.add(p);
                        } else if (fullAdress.contains(constraint.toString().toUpperCase())) {
                            nPlanetList.add(p);
                        }
                    } catch (Exception e) {
                        System.out.println("error search: " + name);
                    }
                }

                results.values = nPlanetList;
                results.count = nPlanetList.size();

            }
            return results;
        }

        private boolean searchByTags(String[] arr, String tag) {
            for (String s : arr) {
                if (s.toUpperCase().contains(tag.toUpperCase()))
                    return true;
            }
            return false;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count == 0)
                notifyDataSetInvalidated();
            else {
                set = (ArrayList<ModelNameList>) results.values;
                notifyDataSetChanged();
            }
        }
    }
}
