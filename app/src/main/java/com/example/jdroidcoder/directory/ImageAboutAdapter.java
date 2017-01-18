package com.example.jdroidcoder.directory;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class ImageAboutAdapter extends PagerAdapter {
    private Context context;
    private Integer[] image = new Integer[]{R.drawable.rivne_about1, R.drawable.rivne_about2,
            R.drawable.rivne_about3, R.drawable.rivne_about4, R.drawable.rivne_about5};
    private LayoutInflater inflater;
    private ViewPager viewPager;

    public ImageAboutAdapter(Context context, ViewPager viewPager) {
        this.context = context;
        this.viewPager = viewPager;

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return image.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        final View view = inflater.inflate(R.layout.about_style, container,
                false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.page);

        imageView.setImageResource(image[position]);

        Button skin = (Button) view.findViewById(R.id.skin);
        skin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context,AboutActivity.class).putExtra("skin",true));
            }
        });

        ImageButton nextPage = (ImageButton) view.findViewById(R.id.nextPage);
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(position+1);
            }
        });

        if(position == image.length-1){
            skin.setVisibility(View.GONE);
            nextPage.setVisibility(View.GONE);
            Button closeAbout = (Button) view.findViewById(R.id.close_about);
            closeAbout.setVisibility(View.VISIBLE);
            closeAbout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    context.startActivity(new Intent(context,AboutActivity.class).putExtra("skin",true));

                }
            });
        }
        container.addView(view);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
