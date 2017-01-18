package com.example.jdroidcoder.directory;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import com.google.android.gms.maps.model.MarkerOptions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

public class PersonalPageActivity extends AppCompatActivity {

    private SupportMapFragment mapFragment;
    private GoogleMap map;
    private ViewPager viewPager;
    private ImageView follow, background;
    private ModelNameList personslPage;
    private Thread thread;
    private ArrayList<ModelNameList> modelNameLists = new ArrayList<>();
    private JSONClientsModel jsonModelClients = new JSONClientsModel();
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private int categoryID = 0, numberPage = 0;
    private ImageView ico, image, showMap, facebook, vk, twitter, google, od, instagram, youtube, fornet;
    private TextView name, address, personalCategory, category, telephone, emails, personalSite,
            graphic_day, graphic_time, about_as, tag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.personal_page_pay);

        personslPage = (ModelNameList) getIntent().getSerializableExtra("person");
        initWidgets();
        setData();

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        map = mapFragment.getMap();
        if (map == null) {
            finish();
            return;
        }

        try {
            if (personslPage.getStreet().isEmpty() || personslPage.getStreet().equals(" ")) {
                mapFragment.getView().setVisibility(View.GONE);
            } else {
                String map_Address = (personslPage.getMap_address().isEmpty()) ? personslPage.getTown() +
                        " " + personslPage.getStreet() + " " + personslPage.getBuild() : personslPage.getMap_address() + " " +
                        personslPage.getTown() + " " + personslPage.getStreet() + " " + personslPage.getBuild();

                if (!map_Address.isEmpty()) {
                    new CheckLocation().execute(map_Address);
                } else {
                    mapFragment.getView().setVisibility(View.GONE);

                }
            }
        } catch (Exception e) {
            mapFragment.getView().setVisibility(View.GONE);
        }
        searchAnotherClientToCategory();
    }

    private void searchAnotherClientToCategory() {
        thread = new Thread(new Runnable() {
            @Override
            public void run() {

                JSONCategoryModel jsonCategoryModel = gson.fromJson(readFromFile(getResources().getString(R.string.allCategories)), JSONCategoryModel.class);

                LinkedList<ModelCategoryList> list = jsonCategoryModel.getList();

                for (int i = 0; i < list.size(); i++) {
                    if (list.get(i).getCATEGORY().equals(personslPage.getCategory()[0])) {
                        categoryID = list.get(i).getId();
                    }
                }

                JSONClientsModel jsonModel = gson.fromJson(readFromFile(getResources().getString(R.string.allClients)), JSONClientsModel.class);
                LinkedList<ModelNameList> rajonList = null;
                if (loadShowRajon()) {

                    try {
                        JSONClientsModel rajon = gson.fromJson(readFromFile(getResources().getString(R.string.rajon)), JSONClientsModel.class);
                        rajonList = new LinkedList<>(rajon.getList());
                    } catch (Exception e) {

                    }
                }

                LinkedList<ModelNameList> set = new LinkedList<>(jsonModel.getList());
                if (rajonList != null) {
                    set.addAll(rajonList);
                }

                for (int i = 0; i < set.size(); i++) {
                    try {
                        if (personslPage.getCategory()[0].equals(set.get(i).getCategory()[0])) {
                            modelNameLists.add(set.get(i));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        thread.start();
    }

    private boolean loadShowRajon() {
        SharedPreferences sharedPreferences = getSharedPreferences("rajon", MODE_PRIVATE);
        int entered = sharedPreferences.getInt("rajon", 0);
        return (entered == 1) ? true : false;
    }

    private String readFromFile(String fileName) {

        String ret = "";

        try {
            InputStream inputStream = openFileInput(fileName);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String str;
                while ((str = bufferedReader.readLine()) != null) {
                    ret += str;
                }

                inputStream.close();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private void initWidgets() {
        Handler setPhoto = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (personslPage.getPhotos().length > 0) {
                    viewPager = (ViewPager) findViewById(R.id.pager);
                    viewPager.setAdapter(new ImageGalleryAdapter(PersonalPageActivity.this, personslPage.getPhotos()));
                } else {
                    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.gallery);
                    relativeLayout.setVisibility(View.GONE);
                }
            }
        };
        setPhoto.sendEmptyMessage(0);

        background = (ImageView) findViewById(R.id.image);

        category = (TextView) findViewById(R.id.category);

        name = (TextView) findViewById(R.id.personal_name);
        address = (TextView) findViewById(R.id.personal_address);
        personalCategory = (TextView) findViewById(R.id.personal_category);

        telephone = (TextView) findViewById(R.id.contacts);
        emails = (TextView) findViewById(R.id.emails);

        follow = (ImageView) findViewById(R.id.follow);

        if (isFollow()) {
            follow.setBackgroundResource(R.drawable.follow);
        }

        ico = (ImageView) findViewById(R.id.personal_ico);

        personalSite = (TextView) findViewById(R.id.personal_site);
        graphic_day = (TextView) findViewById(R.id.day);
        graphic_time = (TextView) findViewById(R.id.time);
        about_as = (TextView) findViewById(R.id.about_as);
        tag = (TextView) findViewById(R.id.tag);
        image = (ImageView) findViewById(R.id.image);
        showMap = (ImageView) findViewById(R.id.showMap);
        try {
            if (personslPage.getCover().isEmpty()) {
                showMap.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            showMap.setVisibility(View.GONE);
        }

        facebook = (ImageView) findViewById(R.id.fb);
        instagram = (ImageView) findViewById(R.id.inst);
        vk = (ImageView) findViewById(R.id.vk);
        youtube = (ImageView) findViewById(R.id.youtube);
        twitter = (ImageView) findViewById(R.id.twitter);
        od = (ImageView) findViewById(R.id.od);
        google = (ImageView) findViewById(R.id.google);
        fornet = (ImageView) findViewById(R.id.fornet);
    }

    private void setData() {
        try {
            PersonalPageActivity.this.name.setText(personslPage.getName());
        } catch (Exception e) {

        }

        try {
            PersonalPageActivity.this.category.setText(personslPage.getSpecialize());
        } catch (Exception e) {

        }

        try {
            String addr = personslPage.getTown() + ((personslPage.getStreet().isEmpty()) ? "" : ", " + personslPage.getStreet())
                    + ((personslPage.getBuild().isEmpty()) ? "" : " " + personslPage.getBuild()) +
                    ((personslPage.getAdd_address().isEmpty()) ? "" : ", " + personslPage.getAdd_address());
            address.setText(addr);
        } catch (Exception e) {

        }

        try {
            personalCategory.setText(personslPage.getCategory()[0]);
        } catch (Exception e) {
            personalCategory.setText("");
        }

        try {
            String site = "";
            String[] www = personslPage.getWebsite();
            for (int i = 0; i < www.length; i++) {
                site += www[i] + "\n";
            }
            if (site.isEmpty()) {
                personalSite.setVisibility(View.GONE);
            } else {
                personalSite.setText(site);
            }
        } catch (Exception ex) {
            personalSite.setVisibility(View.GONE);
        }

        try {
            String description = personslPage.getShort_description();

            if (description.isEmpty()) {
                RelativeLayout rl = (RelativeLayout) findViewById(R.id.informathion);

                rl.setVisibility(View.GONE);
            } else {
                about_as.setText(description);
            }

        } catch (Exception ex) {
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.informathion);

            rl.setVisibility(View.GONE);
        }

        try {
            if (personslPage.getPhone().length > 1) {
                telephone.setText(personslPage.getPhone()[0] + "...");
            } else if (personslPage.getPhone().length == 1) {
                telephone.setText(personslPage.getPhone()[0]);
            } else {
                RelativeLayout rl = (RelativeLayout) findViewById(R.id.contactslist);

                rl.setVisibility(View.GONE);
            }

            telephone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (personslPage.getPhone().length > 1) {
                        AlertDialog.Builder builder;

                        builder = new AlertDialog.Builder(PersonalPageActivity.this);
                        builder.setItems(personslPage.getPhone(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                try {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + personslPage.getPhone()[item]));
                                    startActivity(callIntent);
                                } catch (SecurityException ex) {
                                    Intent callIntentM = new Intent(Intent.ACTION_DIAL);
                                    callIntentM.setData(Uri.parse("tel:" + personslPage.getPhone()[item]));
                                    startActivity(callIntentM);
                                }
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        try {
                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            callIntent.setData(Uri.parse("tel:" + personslPage.getPhone()[0]));
                            startActivity(callIntent);
                        } catch (SecurityException ex) {
                            Intent callIntentM = new Intent(Intent.ACTION_DIAL);
                            callIntentM.setData(Uri.parse("tel:" + personslPage.getPhone()[0]));
                            startActivity(callIntentM);
                        }
                    }
                }
            });
        } catch (Exception ex) {
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.contactslist);

            rl.setVisibility(View.GONE);
        }


        try {
            if (personslPage.getEmail().length > 1) {
                emails.setText(personslPage.getEmail()[0] + "...");
            } else if (personslPage.getEmail().length == 1) {
                emails.setText(personslPage.getEmail()[0]);
            } else {
                RelativeLayout rl = (RelativeLayout) findViewById(R.id.emailslist);

                rl.setVisibility(View.GONE);
            }

            emails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (personslPage.getEmail().length > 1) {

                        AlertDialog.Builder builder;

                        builder = new AlertDialog.Builder(PersonalPageActivity.this);
                        builder.setItems(personslPage.getEmail(), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int item) {
                                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, personslPage.getEmail());
                                emailIntent.setType("text/plain");
                                startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                            }
                        });

                        AlertDialog alert = builder.create();
                        alert.show();
                    } else {
                        Intent emailIntent = new Intent(Intent.ACTION_SEND);
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, personslPage.getEmail());
                        emailIntent.setType("text/plain");
                        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                    }
                }
            });
        } catch (Exception ex) {
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.emailslist);

            rl.setVisibility(View.GONE);
        }

        try {
            final Map<String, String> graf = personslPage.getShedule();
            int i = 0;
            for (Map.Entry entry : graf.entrySet()) {
                if (i < 1) {
                    graphic_day.append(entry.getKey() + "\n");
                    graphic_time.append(entry.getValue() + "\n");
                    i++;
                } else if (i < 2) {
                    graphic_day.append(entry.getKey().toString());
                    graphic_time.append(entry.getValue().toString());
                    i++;
                }
            }

            RelativeLayout rl = (RelativeLayout) findViewById(R.id.graphic_work);
            if (graf.size() > 1) {
                if (rl != null) {
                    rl.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            LayoutInflater li = LayoutInflater.from(PersonalPageActivity.this);
                            View promptsView = li.inflate(R.layout.grafic_style, null);

                            TextView day = (TextView) promptsView.findViewById(R.id.day);
                            TextView time = (TextView) promptsView.findViewById(R.id.time);

                            int i = 0;
                            for (Map.Entry entry : graf.entrySet()) {
                                if (i < graf.size() - 1) {
                                    day.append(entry.getKey() + "\n");
                                    time.append(entry.getValue() + "\n");
                                    i++;
                                } else {
                                    day.append(entry.getKey().toString());
                                    time.append(entry.getValue().toString());
                                    i++;
                                }
                            }

                            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PersonalPageActivity.this);
                            alertDialogBuilder.setView(promptsView);

                            alertDialogBuilder
                                    .setCancelable(false)
                                    .setNegativeButton("OK",
                                            new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int id) {
                                                    dialog.cancel();
                                                }
                                            });

                            AlertDialog alertDialog = alertDialogBuilder.create();

                            alertDialog.show();
                        }
                    });
                }
            } else {
                rl.setVisibility(View.GONE);
            }
        } catch (Exception ex) {
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.graphic_work);

            rl.setVisibility(View.GONE);
        }

        try {
            final String[] tags = personslPage.getTags();
            String line_tags = tags[0] + "...";

            tag.setText(line_tags);

            RelativeLayout rl = (RelativeLayout) findViewById(R.id.tags);
            if (rl != null) {
                rl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater li = LayoutInflater.from(PersonalPageActivity.this);
                        View promptsView = li.inflate(R.layout.tags_style, null);

                        TextView tag = (TextView) promptsView.findViewById(R.id.tag);

                        String line_tags = "";
                        for (int i = 0; i < tags.length; i++) {
                            if (i < tags.length - 1) {
                                line_tags += tags[i] + "\n-\n";
                            } else {
                                line_tags += tags[i];
                            }
                        }

                        tag.setText(line_tags);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(PersonalPageActivity.this);
                        alertDialogBuilder.setView(promptsView);

                        alertDialogBuilder
                                .setCancelable(false)
                                .setNegativeButton("OK",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });

                        AlertDialog alertDialog = alertDialogBuilder.create();

                        alertDialog.show();
                    }
                });
            }
        } catch (Exception ex) {
            RelativeLayout rl = (RelativeLayout) findViewById(R.id.tags);

            rl.setVisibility(View.GONE);
        }

        try {
            Picasso.with(PersonalPageActivity.this).load(personslPage.getAvatar()).resize(150, 150)
                    .into(ico, new com.squareup.picasso.Callback() {
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
            if (personslPage.getIco() != 0) {
                ico.setImageResource(personslPage.getIco());
                ico.setBackgroundResource(R.drawable.imageview_personal_page_style);
            }
        }

        Map<String, String> socials = personslPage.getSocials();

        try {
            for (final Map.Entry entry : socials.entrySet()) {
                if ("Facebook".equals(entry.getKey()) && !entry.getValue().toString().isEmpty()) {
                    facebook.setBackgroundResource(R.drawable.fb);
                    facebook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getValue().toString()));
                            startActivity(browserIntent);
                        }
                    });
                }

                if ("Twitter".equals(entry.getKey()) && !entry.getValue().toString().isEmpty()) {
                    twitter.setBackgroundResource(R.drawable.twitter);
                    twitter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getValue().toString()));
                            startActivity(browserIntent);
                        }
                    });
                }

                if ("Vkontakte".equals(entry.getKey()) && !entry.getValue().toString().isEmpty()) {
                    vk.setBackgroundResource(R.drawable.vk);
                    vk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getValue().toString()));
                            startActivity(browserIntent);
                        }
                    });
                }

                if ("Odnoklassniki".equals(entry.getKey()) && !entry.getValue().toString().isEmpty()) {
                    od.setBackgroundResource(R.drawable.o);
                    od.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getValue().toString()));
                            startActivity(browserIntent);
                        }
                    });
                }
                if ("Youtube".equals(entry.getKey()) && !entry.getValue().toString().isEmpty()) {
                    youtube.setBackgroundResource(R.drawable.youtube);
                    youtube.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getValue().toString()));
                            startActivity(browserIntent);
                        }
                    });
                }
                if ("Instagram".equals(entry.getKey()) && !entry.getValue().toString().isEmpty()) {
                    instagram.setBackgroundResource(R.drawable.inst);
                    instagram.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getValue().toString()));
                            startActivity(browserIntent);
                        }
                    });
                }
                if ("Google+".equals(entry.getKey()) && !entry.getValue().toString().isEmpty()) {
                    google.setBackgroundResource(R.drawable.gl);
                    google.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getValue().toString()));
                            startActivity(browserIntent);
                        }
                    });
                }
                if ("Foursquare".equals(entry.getKey()) && !entry.getValue().toString().isEmpty()) {
                    fornet.setBackgroundResource(R.drawable.fornet_bv);
                    fornet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(entry.getValue().toString()));
                            startActivity(browserIntent);
                        }
                    });
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        DisplayMetrics displayMetrics = new DisplayMetrics();

        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final int width = displayMetrics.widthPixels;
        final int height = displayMetrics.heightPixels;

        try {
            Picasso.with(PersonalPageActivity.this).load(personslPage.getCover()).resize(width, height / 4).into(image);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void showMapButton(View view) {
        if (mapFragment.getView().getVisibility() == View.GONE) {
            background.setVisibility(View.GONE);
            mapFragment.getView().setVisibility(View.VISIBLE);
        } else {
            background.setVisibility(View.VISIBLE);
            mapFragment.getView().setVisibility(View.GONE);
        }
    }

    public void editButton(View view) {
        startActivity(new Intent(PersonalPageActivity.this, ContactActivity.class)
                .putExtra("person", personslPage).putExtra("title", "Редагувати"));
    }

    public void followButton(View view) {
        if (isFollow()) {
            removeFollow();
            follow.setBackgroundResource(R.drawable.no_follow);
        } else {
            Gson gson = new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

            SharedPreferences sharedPreferences = getSharedPreferences("Followers", MODE_APPEND);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(String.valueOf(personslPage.getId()), gson.toJson(personslPage));
            editor.apply();
            follow.setBackgroundResource(R.drawable.follow);
        }
    }

    private void removeFollow() {
        SharedPreferences sharedPreferences = getSharedPreferences("Followers", MODE_APPEND);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(String.valueOf(personslPage.getId()));
        editor.apply();
    }

    private boolean isFollow() {
        SharedPreferences sharedPreferences = getSharedPreferences("Followers", MODE_APPEND);
        Map<String, String> map = (Map<String, String>) sharedPreferences.getAll();

        for (Map.Entry entry : map.entrySet()) {
            if (entry.getKey().equals(String.valueOf(personslPage.getId()))) {
                return true;
            }
        }

        return false;
    }

    public void showMap(View view) {

        String map_Address = (personslPage.getMap_address().isEmpty()) ? personslPage.getTown() +
                " " + personslPage.getStreet() + " " + personslPage.getBuild() : personslPage.getMap_address() + " " +
                personslPage.getTown() + " " + personslPage.getStreet() + " " + personslPage.getBuild();
        Uri gmmIntentUri = Uri.parse("geo:37.7749,-122.4194?q=" + ((map_Address.isEmpty()) ?
                name.getText().toString() : map_Address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        startActivity(mapIntent);
    }

    private void moveToCurrentLocation(LatLng currentLocation) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15));
        map.animateCamera(CameraUpdateFactory.zoomIn());
        map.animateCamera(CameraUpdateFactory.zoomTo(17), 2000, null);
    }

    public void shareButton(View view) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");

        String map_Address = (personslPage.getMap_address().isEmpty()) ? personslPage.getTown() +
                " " + personslPage.getStreet() + " " + personslPage.getBuild() : personslPage.getMap_address();
        String shareBody = personslPage.getName() + "\n" + map_Address + "\n" + personslPage.getAllTel() + "\n" + "моб.довідник [+]Рівне";

        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, personslPage.getName());
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public void showAnotheToCategoty(View view) {
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        jsonModelClients.setList(new HashSet<>(modelNameLists));

        startActivity(new Intent(this, SelectCategoryListActivity.class).putExtra("clients", gson.toJson(jsonModelClients))
                .putExtra("title", personslPage.getCategory()[0])
                .putExtra("category_id", String.valueOf(categoryID)));
    }

    public void clickBack(View view) {
        try{
            numberPage = viewPager.getCurrentItem();
            viewPager.setCurrentItem(numberPage-1);
        }catch (Exception e){

        }
    }

    public void clickNext(View view) {
        try{
            numberPage = viewPager.getCurrentItem();
            viewPager.setCurrentItem(numberPage+1);
        }catch (Exception e){

        }
    }

    private class CheckLocation extends AsyncTask<String, Void, String[]> {

        @Override
        protected String[] doInBackground(String... params) {

            return new MapLatLngSendQuery(params[0]).acceptQuest();
        }

        @Override
        protected void onPostExecute(String[] strings) {
            super.onPostExecute(strings);

            try {
                LatLng latLng = new LatLng(Double.parseDouble(strings[0]), Double.parseDouble(strings[1]));
                map.addMarker(new MarkerOptions()
                        .position(latLng));
                try {
                    moveToCurrentLocation(latLng);
                } catch (Exception e) {

                }
            } catch (NumberFormatException ex) {
                try {
                    mapFragment.getView().setVisibility(View.GONE);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }
    }
}
