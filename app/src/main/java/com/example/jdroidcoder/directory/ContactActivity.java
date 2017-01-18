package com.example.jdroidcoder.directory;

import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;

public class ContactActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private EditText name, address, categoris, site, youName;
    private EditText telephone, emails, tags;
    private TextView category, how, graphics;
    private EditText insUrl, fbUrl, vkUrl, youtubeUrl, twitterUrl, odUrl, city, concret_address, build;
    private TextView beginMonday, beginTuesday, beginWednesday, beginThursday, beginFriday, beginSaturday, beginSunday;
    private TextView endMonday, endTuesday, endWednesday, endThursday, endFriday, endSaturday, endSunday;
    private ImageView ico, image, largeBackground, smallBackground;
    private Integer idPhone = 0, idEmail = 0, idTag = 0, tagCount = 0;
    private CheckBox monday_not_wirk, tuesday_not_wirk, wednesday_not_wirk, thursday_not_wirk,
            friday_not_wirk, saturday_not_wirk, sunday_not_wirk;
    private ModelNameList personslPage;
    private Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private String[] rubriks;
    private String title;
    private Uri URIIcoPath = null, URIImagePath = null;
    private int columnIndex, countGraphics = 0;
    private String icoPath = "", imagePath = "";
    private int key = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact);

        title = getIntent().getStringExtra("title");

        if (title.equals("Редагувати")) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.edit_contact);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setTitle("");
            key = 1;
        } else {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setLogo(R.drawable.add_contact);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
            getSupportActionBar().setTitle("");
        }

        final Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                createCategories();
            }
        });
        thread.start();

        init();

        category.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.equals(category)) {
                    try {
                        thread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    createAlert();
                }
            }
        });

        how.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAlertHowAreYou();
            }
        });

        graphics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (countGraphics == 0) {
                    createGraphicsDialog();
                    countGraphics++;
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
                    builder.setTitle("Увага!!!");
                    builder.setMessage("Графік роботи обнулиться")
                            .setCancelable(false)
                            .setPositiveButton("Продовжити", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    createGraphicsDialog();
                                }
                            })
                            .setNegativeButton("Ні",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });

        try {
            personslPage = (ModelNameList) getIntent().getSerializableExtra("person");

            categoris.setText(personslPage.getSpecialize());
            name.setText(personslPage.getName());
            address.setText(personslPage.getStreet());

            telephone.setText(personslPage.getPhone()[0]);

            category.setText(personslPage.getCategory()[0]);
            emails.setText(personslPage.getEmail()[0]);

            city.setText(personslPage.getTown());
            build.setText(personslPage.getBuild());

            concret_address.setText(personslPage.getAdd_address());
            site.setText(personslPage.getWebsite()[0]);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.contact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.send) {
            send();
            finish();
        } else if (id == R.id.cancel) {
            finish();
        } else if (id == R.id.remove) {
            createRemoveDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void createRemoveDialog() {
        LayoutInflater li = LayoutInflater.from(ContactActivity.this);
        View promptsView = li.inflate(R.layout.remove_dialog_style, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        final EditText name = (EditText) promptsView
                .findViewById(R.id.name);

        final EditText position = (EditText) promptsView
                .findViewById(R.id.position);

        final EditText emailPhone = (EditText) promptsView
                .findViewById(R.id.phone_email);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (!name.getText().toString().isEmpty() || !position.getText().toString().isEmpty()
                                        || !emailPhone.getText().toString().isEmpty()) {
                                    final String message = "name: " +
                                            personslPage.getName() + "; i: " + position.getText().toString()
                                            + "; myName: " + name.getText().toString()
                                            + "; callback: " + emailPhone.getText().toString() + ";";

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            new CreateQuery(ContactActivity.this, message, 3).accept();
                                        }
                                    }).start();
                                    Toast.makeText(ContactActivity.this,"Дякуємо!\n" +
                                            "Ваше повідомлення надіслане",Toast.LENGTH_LONG).show();

                                    dialog.cancel();
                                }else {
                                    Toast.makeText(ContactActivity.this,"Будь-ласка, заповніть усі поля",Toast.LENGTH_LONG).show();

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
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (!title.equals("Редагувати")) {
            MenuItem remove = menu.findItem(R.id.remove);
            remove.setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    private void send() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"info@wikkno.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, (key == 0) ? "Прошу додати контакт" : "Прошу редагувати контакт");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, createMessageText());

        emailIntent.putExtra(Intent.EXTRA_STREAM,URIIcoPath);
        emailIntent.putExtra(Intent.EXTRA_STREAM, URIImagePath);
        startActivity(Intent.createChooser(emailIntent, "Отправка письма..."));
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                new CreateQuery(ContactActivity.this, createMessageText(), key).accept();
//            }
//        }).start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    icoPath = cursor.getString(columnIndex);
                    URIIcoPath = Uri.parse("file://" + icoPath);
                    cursor.close();
                    smallBackground = (ImageView) findViewById(R.id.personal_ico);
                    Picasso.with(ContactActivity.this).load(URIIcoPath).into(smallBackground);
                }
                break;
            }
            case 2: {
                if (resultCode == RESULT_OK) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    imagePath = cursor.getString(columnIndex);
                    URIImagePath = Uri.parse("file://" + imagePath);
                    cursor.close();
                    largeBackground = (ImageView) findViewById(R.id.map);
                    Picasso.with(ContactActivity.this).load(URIImagePath).into(largeBackground);
                }
                break;
            }
        }
    }

    private String createMessageText() {
        String message = "", addr, sit, ph, em, gr, inst, fb, vk, you, tw, od, tg, concAddress, bld, ct, cat;

        try {
            sit = (site.getText().toString().isEmpty() || site.getText().toString().equals(personslPage.getWebsite()) ?
                    "" : site.getText().toString());

            ph = (telephone.getText().toString().isEmpty() || telephone.getText().toString().equals(personslPage.getPhone()[0]) ?
                    "" : telephone.getText().toString());

            if (idPhone != 0) {
                for (int i = 0; i < idPhone; i++) {
                    EditText phone = (EditText) findViewById(i);
                    ph += " / " + ((phone.getText().toString().isEmpty()) ?
                            "" : phone.getText().toString());
                }
            }

            em = (emails.getText().toString().isEmpty() || emails.getText().toString().equals(personslPage.getEmail()[0]) ?
                    "" : emails.getText().toString());

            if (idEmail != 0) {
                for (int i = 100; i < idEmail; i++) {
                    EditText email = (EditText) findViewById(i);
                    em += " / " + ((email.getText().toString().isEmpty()) ?
                            "" : email.getText().toString());
                }
            }

            gr = (graphics.getText().toString().isEmpty()) ?
                    "" : graphics.getText().toString();
            inst = (insUrl.getText().toString().isEmpty()) ?
                    "" : insUrl.getText().toString();
            fb = (fbUrl.getText().toString().isEmpty()) ?
                    "" : fbUrl.getText().toString();
            vk = (vkUrl.getText().toString().isEmpty()) ?
                    "" : vkUrl.getText().toString();
            you = (youtubeUrl.getText().toString().isEmpty()) ?
                    "" : youtubeUrl.getText().toString();
            tw = (twitterUrl.getText().toString().isEmpty()) ?
                    "" : twitterUrl.getText().toString();
            od = (odUrl.getText().toString().isEmpty()) ?
                    "" : odUrl.getText().toString();
            tg = (tags.getText().toString().isEmpty()) ?
                    "" : tags.getText().toString();

            if (tagCount != 0) {
                for (int i = 0; i < tagCount; i++) {
                    EditText email = (EditText) findViewById(i + 200);
                    tg += " / " + ((email.getText().toString().isEmpty()) ?
                            "" : email.getText().toString());
                }
            }

            concAddress = (concret_address.getText().toString().isEmpty()) ?
                    "" : concret_address.getText().toString();

            bld = (build.getText().toString().isEmpty()) ?
                    "" : build.getText().toString();

            ct = (city.getText().toString().isEmpty()) ?
                    "" : city.getText().toString();

            addr = (address.getText().toString().isEmpty() || address.getText().toString().equals(personslPage.getStreet())) ?
                    "" : address.getText().toString();

            cat = (category.getText().toString().isEmpty() || category.getText().toString().equals(personslPage.getCategory()[0]) ?
                    "" : category.getText().toString());

        } catch (Exception e) {
            addr = (address.getText().toString().isEmpty()) ?
                    "" : address.getText().toString();
            sit = (site.getText().toString().isEmpty()) ?
                    "" : site.getText().toString();

            ph = (telephone.getText().toString().isEmpty()) ?
                    "" : telephone.getText().toString();

            if (idPhone != 0) {
                for (int i = 0; i < idPhone; i++) {
                    EditText phone = (EditText) findViewById(i);
                    ph += " / " + ((phone.getText().toString().isEmpty()) ?
                            "" : phone.getText().toString());
                }
            }
            em = (emails.getText().toString().isEmpty()) ?
                    "" : emails.getText().toString();

            if (idEmail != 0) {
                for (int i = 100; i < idEmail; i++) {
                    EditText email = (EditText) findViewById(i);
                    em += " / " + ((email.getText().toString().isEmpty()) ?
                            "" : email.getText().toString());
                }
            }

            gr = (graphics.getText().toString().isEmpty()) ?
                    "" : graphics.getText().toString();
            inst = (insUrl.getText().toString().isEmpty()) ?
                    "" : insUrl.getText().toString();
            fb = (fbUrl.getText().toString().isEmpty()) ?
                    "" : fbUrl.getText().toString();
            vk = (vkUrl.getText().toString().isEmpty()) ?
                    "" : vkUrl.getText().toString();
            you = (youtubeUrl.getText().toString().isEmpty()) ?
                    "" : youtubeUrl.getText().toString();
            tw = (twitterUrl.getText().toString().isEmpty()) ?
                    "" : twitterUrl.getText().toString();
            od = (odUrl.getText().toString().isEmpty()) ?
                    "" : odUrl.getText().toString();
            tg = (tags.getText().toString().isEmpty()) ?
                    "" : tags.getText().toString();

            if (tagCount != 0) {
                for (int i = 0; i < tagCount; i++) {
                    EditText email = (EditText) findViewById(i + 200);
                    tg += " / " + ((email.getText().toString().isEmpty()) ?
                            "" : email.getText().toString());
                }
            }

            concAddress = (concret_address.getText().toString().isEmpty()) ?
                    "" : concret_address.getText().toString();

            ct = (city.getText().toString().isEmpty()) ?
                    "" : city.getText().toString();

            bld = (build.getText().toString().isEmpty()) ?
                    "" : build.getText().toString();

            cat = (category.getText().toString().isEmpty()) ?
                    "" : category.getText().toString();
        }

        message = name.getText().toString() + "\n" + categoris.getText().toString() + "\nАдреса: "
                + ct + ", " + addr + ", " + bld + ", " + concAddress + "\nСайт: " + sit + "\nТелефон: " + ph + "\nEmail: " + em +
                "\nГрафік роботи: " + gr + "\nInstagram: " + inst + "\nFaceBook: " + fb + "\nVK: " + vk +
                "\nYoutube: " + you + "\nTwitter: " + tw + "\nOD: " + od +
                "\nТеги: " + tg + "\nКатегорія: " + cat + "\nЯ: " + how.getText().toString()
                + "\nІм'я: " + youName.getText().toString();

        return message;
    }

    private void init() {
        name = (EditText) findViewById(R.id.personal_name);
        address = (EditText) findViewById(R.id.personal_address);
        telephone = (EditText) findViewById(R.id.contacts);
        emails = (EditText) findViewById(R.id.emails);
        categoris = (EditText) findViewById(R.id.category);
        site = (EditText) findViewById(R.id.personal_site);
        graphics = (TextView) findViewById(R.id.graphics);
        graphics.setRawInputType(0x00000000);
        tags = (EditText) findViewById(R.id.tags);
        insUrl = (EditText) findViewById(R.id.inst_url);
        fbUrl = (EditText) findViewById(R.id.fb_url);
        vkUrl = (EditText) findViewById(R.id.vk_url);
        youtubeUrl = (EditText) findViewById(R.id.youtube_url);
        twitterUrl = (EditText) findViewById(R.id.twitter_url);
        odUrl = (EditText) findViewById(R.id.od_url);
        category = (TextView) findViewById(R.id.personal_category);
        category.setRawInputType(0x00000000);
        youName = (EditText) findViewById(R.id.name);

        how = (TextView) findViewById(R.id.howAreYou);
        how.setRawInputType(0x00000000);

        ico = (ImageView) findViewById(R.id.photoaparat_personal_ico);
        image = (ImageView) findViewById(R.id.photoaparat_personal_image);

        ico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        }

                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                321);
                    }
                }
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra("return-data", true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 1);
            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED) {
                        if (shouldShowRequestPermissionRationale(
                                Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        }

                        requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                321);
                    }
                }
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra("return-data", true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), 2);
            }
        });

        site.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                site.setText("www.");
            }
        });

        city = (EditText) findViewById(R.id.city);
        concret_address = (EditText) findViewById(R.id.concret_address);
        build = (EditText) findViewById(R.id.build);
    }

    private void createGraphicsDialog() {
        LayoutInflater li = LayoutInflater.from(ContactActivity.this);
        View promptsView = li.inflate(R.layout.graphics_dialog_style, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setView(promptsView);

        initGraphics(promptsView);

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                ContactActivity.this.graphics.setText("Понеділок: " + beginMonday.getText().toString() + "/" + endMonday.getText().toString()
                                        + "\n" + "Вівторок: " + beginTuesday.getText().toString() + "/" + endTuesday.getText().toString()
                                        + "\n" + "Середа: " + beginWednesday.getText().toString() + "/" + endWednesday.getText().toString()
                                        + "\n" + "Четвер: " + beginThursday.getText().toString() + "/" + endThursday.getText().toString()
                                        + "\n" + "П'ятниця: " + beginFriday.getText().toString() + "/" + endFriday.getText().toString()
                                        + "\n" + "Субота: " + beginSaturday.getText().toString() + "/" + endSaturday.getText().toString()
                                        + "\n" + "Неділя: " + beginSunday.getText().toString() + "/" + endSunday.getText().toString());

                                dialog.cancel();
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

    private void initGraphics(View promptsView) {
        beginMonday = (TextView) promptsView
                .findViewById(R.id.beginMonday);

        endMonday = (TextView) promptsView
                .findViewById(R.id.endMonday);

        beginTuesday = (TextView) promptsView
                .findViewById(R.id.beginTuesday);

        endTuesday = (TextView) promptsView
                .findViewById(R.id.endTuesday);

        beginWednesday = (TextView) promptsView
                .findViewById(R.id.beginWednesday);

        endWednesday = (TextView) promptsView
                .findViewById(R.id.endWednesday);

        beginThursday = (TextView) promptsView
                .findViewById(R.id.beginThursday);

        endThursday = (TextView) promptsView
                .findViewById(R.id.endThursday);

        beginFriday = (TextView) promptsView
                .findViewById(R.id.beginFriday);

        endFriday = (TextView) promptsView
                .findViewById(R.id.endFriday);

        beginSaturday = (TextView) promptsView
                .findViewById(R.id.beginSaturday);

        endSaturday = (TextView) promptsView
                .findViewById(R.id.endSaturday);

        beginSunday = (TextView) promptsView
                .findViewById(R.id.beginSunday);

        endSunday = (TextView) promptsView
                .findViewById(R.id.endSunday);

        beginMonday.setOnClickListener(this);
        endMonday.setOnClickListener(this);

        beginThursday.setOnClickListener(this);
        endThursday.setOnClickListener(this);

        beginTuesday.setOnClickListener(this);
        endTuesday.setOnClickListener(this);

        beginWednesday.setOnClickListener(this);
        endWednesday.setOnClickListener(this);

        beginFriday.setOnClickListener(this);
        endFriday.setOnClickListener(this);

        beginSaturday.setOnClickListener(this);
        endSaturday.setOnClickListener(this);

        beginSunday.setOnClickListener(this);
        endSunday.setOnClickListener(this);


        monday_not_wirk = (CheckBox) promptsView.findViewById(R.id.monday_not_wirk);
        monday_not_wirk.setOnCheckedChangeListener(this);
        tuesday_not_wirk = (CheckBox) promptsView.findViewById(R.id.tuesday_not_wirk);
        tuesday_not_wirk.setOnCheckedChangeListener(this);
        wednesday_not_wirk = (CheckBox) promptsView.findViewById(R.id.wednesday_not_wirk);
        wednesday_not_wirk.setOnCheckedChangeListener(this);
        thursday_not_wirk = (CheckBox) promptsView.findViewById(R.id.thursday_not_wirk);
        thursday_not_wirk.setOnCheckedChangeListener(this);
        friday_not_wirk = (CheckBox) promptsView.findViewById(R.id.friday_not_wirk);
        friday_not_wirk.setOnCheckedChangeListener(this);
        saturday_not_wirk = (CheckBox) promptsView.findViewById(R.id.saturday_not_wirk);
        saturday_not_wirk.setOnCheckedChangeListener(this);
        sunday_not_wirk = (CheckBox) promptsView.findViewById(R.id.sunday_not_wirk);
        sunday_not_wirk.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(final View v) {
        TimePickerDialog.OnTimeSetListener myCallBack = new TimePickerDialog.OnTimeSetListener() {
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                final String time = String.format("%02d-%02d", hourOfDay, minute);
                int id = v.getId();
                if (id != R.id.beginSunday && id != R.id.endSunday &&
                        id != R.id.beginSaturday && id != R.id.endSaturday) {
                    createAlertForTime(time, v.getId());
                }

                switch (id) {
                    case R.id.beginMonday:
                        beginMonday.setText(time);
                        break;
                    case R.id.endMonday:
                        endMonday.setText(time);
                        break;
                    case R.id.beginThursday:
                        beginThursday.setText(time);
                        break;
                    case R.id.endThursday:
                        endThursday.setText(time);
                        break;
                    case R.id.beginTuesday:
                        beginTuesday.setText(time);
                        break;
                    case R.id.endTuesday:
                        endTuesday.setText(time);
                        break;
                    case R.id.beginWednesday:
                        beginWednesday.setText(time);
                        break;
                    case R.id.endWednesday:
                        endWednesday.setText(time);
                        break;
                    case R.id.beginFriday:
                        beginFriday.setText(time);
                        break;
                    case R.id.endFriday:
                        endFriday.setText(time);
                        break;
                    case R.id.beginSaturday:
                        beginSaturday.setText(time);
                        break;
                    case R.id.endSaturday:
                        endSaturday.setText(time);
                        break;
                    case R.id.beginSunday:
                        beginSunday.setText(time);
                        break;
                    case R.id.endSunday:
                        endSunday.setText(time);
                        break;
                }

            }
        };
        TimePickerDialog tpd = new TimePickerDialog(ContactActivity.this, myCallBack, 0, 0, true);
        tpd.show();
    }

    private void createAlertForTime(final String time, final Integer id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ContactActivity.this);
        builder.setMessage("Встановити цей час для робочого тижня?")
                .setCancelable(false)
                .setPositiveButton("Так", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (id == R.id.beginTuesday || id == R.id.beginWednesday
                                || id == R.id.beginThursday || id == R.id.beginFriday || id == R.id.beginMonday) {
                            beginMonday.setText(time);
                            beginThursday.setText(time);
                            beginTuesday.setText(time);
                            beginWednesday.setText(time);
                            beginFriday.setText(time);
                        } else {
                            endMonday.setText(time);
                            endThursday.setText(time);
                            endTuesday.setText(time);
                            endWednesday.setText(time);
                            endFriday.setText(time);
                        }
                    }
                })
                .setNegativeButton("Ні",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void createAlertHowAreYou() {
        final String[] how1 = new String[]{"Власник", "Керівник", "Співробітник", "Клієнт", "Знайомий", "інше"};

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setItems(how1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                ContactActivity.this.how.setText(how1[item]);
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void createAlert() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setItems(rubriks, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                category.setText(rubriks[item]);
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    private void createCategories() {
        try {
            JSONCategoryModel jsonCategoryModel = gson.fromJson(
                    readFromFile(getResources().getString(R.string.allCategories)), JSONCategoryModel.class);

            rubriks = new String[jsonCategoryModel.getList().size()];

            for (int i = 0; i < rubriks.length; i++) {
                rubriks[i] = jsonCategoryModel.getList().get(i).getCATEGORY();
            }

            Arrays.sort(rubriks);
        } catch (Exception e) {
            e.getMessage();
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

    public void addPhoneInput(View view) {

        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.addPhoneLayout);

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        EditText editText = new EditText(this);
        editText.setId(idPhone);
        idPhone++;

        editText.setLayoutParams(lParams);
        editText.setHint("* 03622222222");

        linearLayout.addView(editText);
    }

    public void addEmailInput(View view) {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.addEmailLayout);

        LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);


        EditText editText = new EditText(this);
        editText.setId(idEmail += 100);
        idEmail++;

        editText.setLayoutParams(lParams);
        editText.setHint("* info@name...");

        linearLayout.addView(editText);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.monday_not_wirk:
                beginMonday.setText("Вихідний");
                endMonday.setText("Вихідний");
                break;
            case R.id.tuesday_not_wirk:
                beginTuesday.setText("Вихідний");
                endTuesday.setText("Вихідний");
                break;
            case R.id.wednesday_not_wirk:
                beginWednesday.setText("Вихідний");
                endWednesday.setText("Вихідний");
                break;
            case R.id.thursday_not_wirk:
                beginThursday.setText("Вихідний");
                endThursday.setText("Вихідний");
                break;
            case R.id.friday_not_wirk:
                beginFriday.setText("Вихідний");
                endFriday.setText("Вихідний");
                break;
            case R.id.saturday_not_wirk:
                beginSaturday.setText("Вихідний");
                endSaturday.setText("Вихідний");
                break;
            case R.id.sunday_not_wirk:
                beginSunday.setText("Вихідний");
                endSunday.setText("Вихідний");
                break;
        }
    }

    public void addTagInput(View view) {
        if (tagCount < 9) {
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.addTagLayout);

            LinearLayout.LayoutParams lParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);

            lParams.rightMargin = 70;

            EditText editText = new EditText(this);
            editText.setId(idTag + 200);
            idTag++;
            tagCount++;

            editText.setLayoutParams(lParams);
            editText.setHint("Перелік продуктів/послуг.");

            editText.setFocusableInTouchMode(true);
            editText.requestFocus();

            linearLayout.addView(editText);
        }
    }
}
