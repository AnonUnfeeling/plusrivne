package com.example.jdroidcoder.directory;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

public class FollowedAdapter extends BaseAdapter {
    private Context context;
    private List<ModelNameList> set;
    private LayoutInflater layoutInflater;
    private ImageView ico;

    public FollowedAdapter(Context context, List<ModelNameList> set) {
        this.context = context;

        this.set = set;

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
        convertView = layoutInflater.inflate(R.layout.name_list_style,parent,false);

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
            e.getMessage();
        }


        ((TextView)convertView.findViewById(R.id.name)).setText(set.get(position).getName());
        ((TextView)convertView.findViewById(R.id.address)).setText(set.get(position).getStreet()+", "+set.get(position).getBuild());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, PersonalPageActivity.class)
                        .putExtra("person", set.get(position)));
            }
        });

        return convertView;
    }
}
