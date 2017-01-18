package com.example.jdroidcoder.directory;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        try {
            boolean skin = getIntent().getBooleanExtra("skin", false);
            if (skin) {
                startActivity(new Intent(this, MainActivity.class));
                finish();
            } else {

                viewPager.setAdapter(new ImageAboutAdapter(AboutActivity.this, viewPager));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
