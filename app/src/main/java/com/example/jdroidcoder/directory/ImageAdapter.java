package com.example.jdroidcoder.directory;

import android.content.Context;
import android.content.Intent;

import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ImageAdapter extends PagerAdapter {

    private Context mContext;
    private int width;
    private int height;
    private LayoutInflater inflater;
    private ViewPager viewPager;
    private ArrayList<String> mImage = new ArrayList<>();
    private ArrayList<String> bigIco = new ArrayList<>();
    private boolean isShowBigBanner = false;
    private ImageView bigBanner;
    private AdsModel adsModel;
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .create();
    private RelativeLayout.LayoutParams paramsDefault;

    public ImageAdapter(final Context mContext, int width, int height,
                        final String categoris, final ViewPager viewPager) {

        this.mContext = mContext;
        this.width = width;
        this.height = height;

        this.viewPager = viewPager;

        try {
            adsModel = gson.fromJson(readFromFile(), AdsModel.class);

            if (adsModel.getList().size() > 0) {
                shuffleArray(adsModel.getList());
            } else {
                this.viewPager.setVisibility(View.GONE);
            }

            mImage.clear();
            bigIco.clear();

            for (int i = 0; i < adsModel.getList().size(); i++) {
                if (searchByPage(adsModel.getList().get(i).getPages_to_show(), categoris)) {
                    mImage.add(adsModel.getList().get(i).getSMALL_BANNER());
                    bigIco.add(adsModel.getList().get(i).getBANNER());
                }
            }

            if (mImage.isEmpty()) {
                this.viewPager.setVisibility(View.GONE);
            } else {
                this.viewPager.setVisibility(View.VISIBLE);
            }

        } catch (Exception ex) {
            this.viewPager.setVisibility(View.GONE);
        }

        paramsDefault = (RelativeLayout.LayoutParams) viewPager.getLayoutParams();

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        TimeUnit.SECONDS.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    try {
                        if (!isShowBigBanner) {
                            new Update().execute(new Random().nextInt(adsModel.getList().size()));
                        }
                    } catch (Exception e) {

                    }
                }
            }
        }).start();
    }

    private class Update extends AsyncTask<Integer, Void, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(Integer integer) {

            viewPager.setLayoutParams(paramsDefault);
            if (bigBanner != null) {
                bigBanner.setVisibility(View.GONE);
            }
            isShowBigBanner = false;
            viewPager.setCurrentItem(integer);
            notifyDataSetChanged();
            super.onPostExecute(integer);
        }
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void reclam(String categoris) {
        try {
            mImage.clear();
            bigIco.clear();
            for (int i = 0; i < adsModel.getList().size(); i++) {
                if (searchByPage(adsModel.getList().get(i).getPages_to_show(), categoris)) {
                    mImage.add(adsModel.getList().get(i).getSMALL_BANNER());
                    bigIco.add(adsModel.getList().get(i).getBANNER());
                }
            }

            if (mImage.isEmpty()) {
                this.viewPager.setVisibility(View.GONE);
            } else {
                this.viewPager.setVisibility(View.VISIBLE);
            }

            notifyDataSetChanged();
        } catch (Exception e) {

        }
    }

    private boolean searchByPage(String[] arr, String tag) {
        for (String s : arr) {
            if (s.toUpperCase().equals(tag.toUpperCase()) || s.toUpperCase().equals("-3"))
                return true;
        }
        return false;
    }

    private void shuffleArray(LinkedList<AdsModel.Model> small) {
        long seed = System.nanoTime();
        Collections.shuffle(small, new Random(seed));
    }

    private String readFromFile() {
        String ret = "";

        try {
            InputStreamReader inputStreamReader = new InputStreamReader(mContext.openFileInput(mContext.getResources().getString(R.string.ads)));
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

    @Override
    public int getCount() {
        return mImage.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {


        final ImageView imgflag;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        final View view = inflater.inflate(R.layout.reclam_style, container, false);

        imgflag = (ImageView) view.findViewById(R.id.banner);

        try {
            Picasso.with(mContext).load(mImage.get(position))
                    .into(imgflag, new Callback() {
                        @Override
                        public void onSuccess() {

                        }

                        @Override
                        public void onError() {
                            viewPager.setVisibility(View.GONE);
                        }
                    });
        } catch (Exception e) {

        }
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                viewPager.setLayoutParams(paramsDefault);
                if (bigBanner != null) {
                    bigBanner.setVisibility(View.GONE);
                    isShowBigBanner = false;
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        imgflag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    bigBanner = (ImageView) view.findViewById(R.id.bigBanner);
                    if (isShowBigBanner) {
                        viewPager.setLayoutParams(paramsDefault);
                        bigBanner.setVisibility(View.GONE);
                        isShowBigBanner = false;
                    } else {
                        isShowBigBanner = true;

                        RelativeLayout.LayoutParams params =
                                new RelativeLayout.LayoutParams(width, (int) (height * 0.453));
                        viewPager.setLayoutParams(params);

                        bigBanner.setVisibility(View.VISIBLE);

                        Picasso.with(mContext).load(bigIco.get(position))
                                .resize(width, (int) (height * 0.453)).into(bigBanner, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                viewPager.setVisibility(View.GONE);
                            }
                        });

                        bigBanner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    for (int i = 0; i < adsModel.getList().size(); i++) {
                                        if (adsModel.getList().get(i).getBANNER().equals(bigIco.get(position))) {
                                            mContext.startActivity(new Intent(mContext, PersonalPageActivity.class)
                                                    .putExtra("person", adsModel.getList().get(i).getList()));
                                            viewPager.setLayoutParams(paramsDefault);
                                            bigBanner.setVisibility(View.GONE);
                                            isShowBigBanner = false;
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
