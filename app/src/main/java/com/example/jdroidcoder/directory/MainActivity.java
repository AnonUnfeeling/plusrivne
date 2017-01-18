package com.example.jdroidcoder.directory;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import android.widget.Switch;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.ndk.CrashlyticsNdk;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        TabHost.OnTabChangeListener, SearchView.OnQueryTextListener {

    private RelativeLayout.LayoutParams paramsDefault;
    private ListView nameList;
    private ProgressDialog progressDialog;
    private RelativeLayout reclam;
    private SearchView mSearchView;
    private FloatingActionButton fab;
    private NameListViewAdapret nameListViewAdapret;
    private TabHost tabs;
    private ImageAdapter imageAdapter;
    private ViewPager viewPager;
    private MenuItem rajon;
    private JSONClientsModel jsonModelImportent = new JSONClientsModel(), jsonModelDerz = new JSONClientsModel();
    private JSONClientsModel jsonModel = null;
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics(), new CrashlyticsNdk());
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.mipmap.ic_launcher);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ContactActivity.class).putExtra("title", "Додати контакт"));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    new CheckUpdate(MainActivity.this).accept();
                } catch (Exception e) {

                }
            }
        }).start();

        if (!loadEntered()) {
            saveEntered();
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        }

        initTabs();
        initNameList();

        reclam = (RelativeLayout) findViewById(R.id.reclam);
        viewPager = (ViewPager) findViewById(R.id.pager);

        paramsDefault = (RelativeLayout.LayoutParams) viewPager.getLayoutParams();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        width = displayMetrics.widthPixels;
        height = displayMetrics.heightPixels;

        imageAdapter = new ImageAdapter(MainActivity.this, width, height, "0", viewPager);

        startService(new Intent(MainActivity.this, NotificationService.class));
    }

    private void saveEntered() {
        SharedPreferences sharedPreferences = getSharedPreferences("entered", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("entered", 1);
        editor.apply();
    }

    private boolean loadEntered() {
        SharedPreferences sharedPreferences = getSharedPreferences("entered", MODE_PRIVATE);
        int entered = sharedPreferences.getInt("entered", 0);
        return (entered == 1) ? true : false;
    }

    private void saveShowRajon(int i) {
        SharedPreferences sharedPreferences = getSharedPreferences("rajon", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("rajon", i);
        editor.apply();
    }

    private boolean loadShowRajon() {
        SharedPreferences sharedPreferences = getSharedPreferences("rajon", MODE_PRIVATE);
        int entered = sharedPreferences.getInt("rajon", 0);
        return (entered == 1) ? true : false;
    }

    private void initTabs() {
        tabs = (TabHost) findViewById(android.R.id.tabhost);

        tabs.setup();

        TabHost.TabSpec spec = tabs.newTabSpec("tag1");

        spec.setContent(R.id.tab1);
        spec.setIndicator(getResources().getString(R.string.name));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag2");
        spec.setContent(R.id.tab2);
        spec.setIndicator(getResources().getString(R.string.category));
        tabs.addTab(spec);

        spec = tabs.newTabSpec("tag3");
        spec.setContent(R.id.tab3);
        spec.setIndicator(getResources().getString(R.string.follower));
        tabs.addTab(spec);

        setTabColor();

        tabs.setOnTabChangedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        rajon = menu.findItem(R.id.rajon);
        setupSearchView();
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.rajon) {
            final AlertDialog alert = new AlertDialog.Builder(this).create();

            alert.setTitle("Показати район");

            Switch aSwitch = new Switch(this);

            if (loadShowRajon())
                aSwitch.setChecked(true);

            LinearLayout linearLayout = new LinearLayout(MainActivity.this);
            linearLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            linearLayout.setGravity(Gravity.CENTER_HORIZONTAL);
            linearLayout.addView(aSwitch);

            aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        progressDialog = ProgressDialog.show(MainActivity.this, "Завантаження...", "Завантаження контактів району");
                        new SetRajon().execute();
                        saveShowRajon(1);
                    } else {
                        saveShowRajon(0);
                        new Load().execute();
                    }
                    alert.dismiss();
                }
            });

            alert.setView(linearLayout);

            alert.show();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    rajon.setVisible(false);
                    fab.setVisibility(View.GONE);
                    reclam.setVisibility(View.GONE);
                } else {
                    rajon.setVisible(true);
                    fab.setVisibility(View.VISIBLE);
                    reclam.setVisibility(View.VISIBLE);
                }
            }
        });
        mSearchView.setQueryHint("Назва/Адреса");
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.follower) {
            startActivity(new Intent(MainActivity.this, ImportantActivity.class)
                    .putExtra("clients", jsonModelDerz)
                    .putExtra("important", false));
        } else if (id == R.id.add_contact) {
            startActivity(new Intent(MainActivity.this, ContactActivity.class).putExtra("title", "додати контакт"));
        } else if (id == R.id.important) {
            startActivity(new Intent(MainActivity.this, ImportantActivity.class)
                    .putExtra("clients", jsonModelImportent)
                    .putExtra("important", true));
        } else if (id == R.id.about_as) {
            startActivity(new Intent(MainActivity.this, AboutActivity.class));
        } else if (id == R.id.shareApp) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            String shareBody = "Привіт, я користуюсь довідником " +
                    getResources().getString(R.string.app_name) + " " +
                    getResources().getString(R.string.linkPlayMarket) + "\n" +
                    "Рекомендую.";
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Вікно | WikKnow");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            startActivity(Intent.createChooser(sharingIntent, "Share via"));
        } else if (id == R.id.callback) {
            createCallbackDialog();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void createCallbackDialog() {
        LayoutInflater li = LayoutInflater.from(MainActivity.this);
        View promptsView = li.inflate(R.layout.callback_dialog_style, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                MainActivity.this);

        alertDialogBuilder.setView(promptsView);

        final EditText message = (EditText) promptsView
                .findViewById(R.id.message);
        final EditText email = (EditText) promptsView
                .findViewById(R.id.email);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if(email.getText().toString().isEmpty()||message.getText().toString().isEmpty()) {
                                   Toast.makeText(MainActivity.this,"Будь-ласка, заповніть усі поля",Toast.LENGTH_LONG).show();
                                }else {
                                    final String msg = message.getText().toString() + "\n Mій email: " + email.getText().toString();
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new CreateQuery(MainActivity.this, msg, 2).accept();
                                        }
                                    }).start();
                                    Toast.makeText(MainActivity.this,"Дякуємо!\n" +
                                            "Ваше повідомлення надіслане",Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    @Override
    public void onTabChanged(String tabId) {

        switch (tabId) {
            case "tag1":
                this.setTitle(getResources().getString(R.string.app_name));

                setTabColor();

                viewPager.setLayoutParams(paramsDefault);
                viewPager.setAdapter(imageAdapter);
                imageAdapter.reclam("0");
                mSearchView.setVisibility(View.VISIBLE);
                mSearchView.setQueryHint("Назва/Адреса");
                break;
            case "tag2":
                this.setTitle(getResources().getString(R.string.app_name));

                setTabColor();

                mSearchView.setVisibility(View.VISIBLE);
                mSearchView.setQueryHint("Категорія/послуга");

                viewPager.setLayoutParams(paramsDefault);
                imageAdapter.reclam("-1");

                CategotyListFragment categotyListFragment = new CategotyListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("clients", jsonModel);
                categotyListFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.tab2, categotyListFragment).commit();
                break;
            case "tag3":
                this.setTitle(getResources().getString(R.string.app_name));

                mSearchView.setVisibility(View.GONE);

                viewPager.setLayoutParams(paramsDefault);
                imageAdapter.reclam("-2");

                setTabColor();

                getSupportFragmentManager().beginTransaction().replace(R.id.tab3, new FollowedListFragment()).commit();
                break;
        }
    }

    private void setTabColor() {
        for (int i = 0; i < tabs.getTabWidget().getChildCount(); i++) {
            TextView tv = (TextView) tabs.getTabWidget().getChildAt(i).findViewById(android.R.id.title);
            tv.setTextColor(Color.parseColor("#EBECEC"));
        }
        TextView tv = (TextView) tabs.getCurrentTabView().findViewById(android.R.id.title);
        tv.setTextColor(Color.parseColor("#ffffff"));
    }

    private void initNameList() {
        nameList = (ListView) findViewById(R.id.name_list);

        progressDialog = ProgressDialog.show(MainActivity.this, "Завантаження...", "Секундочку:)");

        new Load().execute();

        nameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(),
                        "" + parent.getItemAtPosition(position),
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (tabs.getCurrentTab() == 0) {
            if (newText.isEmpty()) {
                nameListViewAdapret.resetData();
                nameListViewAdapret.notifyDataSetChanged();
            } else {
                fab.setVisibility(View.GONE);
                try {
                    nameListViewAdapret.getFilter().filter(newText);
                } catch (Exception e) {

                }
            }
        } else if (tabs.getCurrentTab() == 1) {
            if (newText.isEmpty()) {
                CategotyListFragment.categoryListViewAdapter.resetData();
            } else {

                fab.setVisibility(View.GONE);
                CategotyListFragment.categoryListViewAdapter.getFilter().filter(newText);
            }
        }

        return true;
    }

    private class Load extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {

            return readFromFile(getResources().getString(R.string.allClients));
        }

        @Override
        protected void onPostExecute(final String jsonClientsModel) {
            super.onPostExecute(jsonClientsModel);

            if (jsonClientsModel.isEmpty()) {
                SetData setData = new SetData();
                setData.execute();
            } else {
                try {
                    jsonModel = gson.fromJson(jsonClientsModel, JSONClientsModel.class);

                    try {
                        LoadImportant loadImportant = new LoadImportant();
                        loadImportant.execute();
                        String str = loadImportant.get();
                    } catch (Exception e) {

                    }

                    nameListViewAdapret = new NameListViewAdapret(MainActivity.this, jsonModel);

                    nameList.setAdapter(nameListViewAdapret);

                    if (loadShowRajon())
                        new SetRajon().execute();

                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    final int width = displayMetrics.widthPixels;
                    final int height = displayMetrics.heightPixels;

                    viewPager.setAdapter(new ImageAdapter(MainActivity.this, width, height, "-1", viewPager));

                } catch (Exception e) {
                    AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Упс...")
                            .setMessage("Для відновлення роботи додатку, перейдіть: налаштування телефону > додатки >" +
                                    " " + getResources().getString(R.string.app_name) + "> очистити дані. Повертайтеся у додаток. Дякуємо")
                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create();
                    dialog.show();
                }

                progressDialog.dismiss();
            }
        }
    }

    private class SetData extends AsyncTask<Void, Void, JSONClientsModel> {
        @Override
        protected JSONClientsModel doInBackground(Void... params) {

            new CategorysQuery(MainActivity.this).accept();
            new AdsQuery(MainActivity.this).accept();
            try {
                new RajonQuery(MainActivity.this).accept();
            } catch (Exception e) {

            }

            return new ClientsQuery(MainActivity.this).accept();
        }

        @Override
        protected void onPostExecute(JSONClientsModel jsonClientsModel) {
            super.onPostExecute(jsonClientsModel);

            try {
                jsonModel = jsonClientsModel;

                try {
                    LoadImportant loadImportant = new LoadImportant();
                    loadImportant.execute();
                    String str = loadImportant.get();
                } catch (Exception e) {

                }

                nameListViewAdapret = new NameListViewAdapret(MainActivity.this, jsonModel);

                nameList.setAdapter(nameListViewAdapret);

                viewPager.setAdapter(new ImageAdapter(MainActivity.this, width, height, "-1", viewPager));

                progressDialog.dismiss();
            } catch (Exception e) {
                AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Упс...")
                        .setMessage("Для відновлення роботи додатку, перейдіть: налаштування телефону > додатки >" +
                                " " + getResources().getString(R.string.app_name) + "" +
                                " > очистити дані. Повертайтеся у додаток. Дякуємо")
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create();
                dialog.show();
            }
        }
    }

    private class LoadImportant extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            try {
                JSONCategoryModel jsonCategoryModel = gson.fromJson(readFromFile(getResources().getString(R.string.allCategories))
                        , JSONCategoryModel.class);
                HashSet<ModelNameList> importent = new HashSet<>();
                HashSet<ModelNameList> derj = new HashSet<>();
                try {
                    for (ModelCategoryList category : jsonCategoryModel.getList()) {
                        if (category.getSpecial() == 2) {
                            for (ModelNameList model : jsonModel.getList()) {
                                try {
                                    if (category.getCATEGORY().equals(model.getCategory()[0])) {
                                        importent.add(model);
                                    }
                                } catch (Exception e) {

                                }
                            }
                        } else if (category.getSpecial() == 1) {
                            for (ModelNameList model : jsonModel.getList()) {
                                try {
                                    if (category.getCATEGORY().equals(model.getCategory()[0])) {
                                        derj.add(model);
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }
                    }
                    jsonModelDerz.setList(derj);
                    jsonModelImportent.setList(importent);

                } catch (Exception e) {
                    return null;
                }

            } catch (Exception e) {
                return null;
            }

            return "";
        }
    }

    private class SetRajon extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            return readFromFile(getResources().getString(R.string.rajon));
        }

        @Override
        protected void onPostExecute(String jsonClientsModel) {
            super.onPostExecute(jsonClientsModel);

            try {
                JSONClientsModel rajon = gson.fromJson(jsonClientsModel, JSONClientsModel.class);
                nameListViewAdapret = new NameListViewAdapret(MainActivity.this, jsonModel);

                nameList.setAdapter(nameListViewAdapret);


                jsonModel.getList().addAll(rajon.getList());
            } catch (Exception e) {

            }

            nameListViewAdapret = new NameListViewAdapret(MainActivity.this, jsonModel);

            try {
                nameList.setAdapter(nameListViewAdapret);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            try {
                LoadImportant loadImportant = new LoadImportant();
                loadImportant.execute();
            } catch (Exception e) {
            }

            progressDialog.dismiss();
        }

    }

    private String readFromFile(String fileName) {
        String ret = "";

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(openFileInput(fileName));
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

}
