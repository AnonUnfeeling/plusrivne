package com.example.jdroidcoder.directory;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;

import java.io.File;

public class ImageGalleryAdapter extends PagerAdapter {

    private Context mContext;
    private LayoutInflater inflater;
    private String[] mImage;

    public ImageGalleryAdapter(Context mContext, String[] photos) {
        this.mContext = mContext;

        mImage = photos;
    }

    @Override
    public int getCount() {
        return mImage.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.gallary_style, container,
                false);

        ImageView imgflag = (ImageView) view.findViewById(R.id.page);

        Picasso.with(mContext).load(mImage[position]).into(imgflag);

        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
