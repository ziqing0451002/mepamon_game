package com.example.bybanggame01;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;

public class ScoreListFragment extends Fragment {

    //畫面元件
    private View mainView;
    private MainActivity mainActivity;
    private ListView listView;
    public String[] from = {"ranking","score","time"};
    private int[] to = {R.id.item_ranking,R.id.item_score,R.id.item_time};
    private SimpleAdapter adapter;
    public LinkedList<HashMap<String,String>> listData;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_score_list, container, false);
        initListView();
        return mainView;
    }

    // 初始化 ListView
    private void initListView(){
        listData = new LinkedList<>();
        for (int i = 0; i < mainActivity.data.size(); i++) {
            MainActivity.ScoreHistory scoreHistory = mainActivity.data.get(i);
            HashMap<String,String> row = new HashMap<>();
            row.put(from[0],"排名：" + (i + 1));
            row.put(from[1],"得分：" + scoreHistory.getScore());
            row.put(from[2],scoreHistory.getTimeStamp());
            listData.add(row);
        }

        listView = mainView.findViewById(R.id.list);
        adapter = new SimpleAdapter(getContext(),listData,R.layout.item_score,from,to);
        listView.setAdapter(adapter);
    }


//    //寫入一筆得分紀錄
//    public void addScore(int score){
//        HashMap<String, String> row = new HashMap<>();
//        row.put(from[0], "排名" + (data.size() + 1));
//        row.put(from[1], "得分：" + score);
//        row.put(from[2], getTime());
//        if (data.size() == 0) {
//            data.add(row);
//        } else {
//            for (int i = 0; i < data.size(); i++) {
//                if (score > Integer.parseInt(Objects.requireNonNull(data.get(i).get(from[1])))) {
//                    data.add(i-1,row);
//                }
//            }
//        }
//
//        adapter.notifyDataSetChanged();
//    }


    
}