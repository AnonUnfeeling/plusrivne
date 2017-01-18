package com.example.jdroidcoder.directory;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;
import java.util.Map;

public class FollowedListFragment extends Fragment{
    private ListView followedList;
    private LinkedList<ModelNameList> set = new LinkedList<>();
    private ModelNameList personslPage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.follower_list_fragment, container, false);

        followedList = (ListView) view.findViewById(R.id.follower_list);

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Followers", getActivity().MODE_PRIVATE);
        Map<String, ModelNameList> mapFollowed = (Map<String, ModelNameList>) sharedPreferences.getAll();

        if(mapFollowed.isEmpty()){
            Toast toast = new Toast(getActivity());
            toast.setView(inflater.inflate(R.layout.toast, container, false));
            toast.setDuration(Toast.LENGTH_LONG);
            toast.show();
        }else {

            for (Map.Entry map : mapFollowed.entrySet()) {
                try {
                    personslPage = gson.fromJson(map.getValue().toString(), ModelNameList.class);
                    set.add(personslPage);
                }catch (Exception ex){
                    ex.printStackTrace();
                }
            }

            followedList.setAdapter(new FollowedAdapter(getActivity(), set));
        }
        return view;
    }
}
