package com.example.bybanggame01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //畫面元件
    private FragmentManager fragmentManager;
    private ScoreListFragment scoreListFragment;
    private GameBlockFragment gameBlockFragment;
    public Button btnsetting;
    private Button btnStart;
    public Button btnScoreList;

    //內部變數
    private Boolean isInGame;

    //共用變數
    public LinkedList<ScoreHistory> data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        scoreListFragment = new ScoreListFragment();
        gameBlockFragment = new GameBlockFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, gameBlockFragment);
        fragmentTransaction.commit();

        initView();


    }

    private void initView(){
        btnStart = findViewById(R.id.btn_start);
        btnsetting = findViewById(R.id.btn_setting);
        btnScoreList = findViewById(R.id.btn_score_list);
        isInGame = true;
        data = new LinkedList<>();
    }

    public void initGame(){
        //初始化開始按鈕
        btnStart.setText("開始");
        btnStart.setBackgroundColor(Color.BLUE);
        btnStart.setTextColor(Color.WHITE);
        btnStart.setEnabled(true);
    }


    public void gameStart(View view) {
        if (isInGame){
            if (gameBlockFragment.gameMode == 0){
                //按鈕變成結束遊戲
                btnStart.setText("結束遊戲");
                btnStart.setTextColor(Color.BLACK);
                btnStart.setBackgroundColor(Color.RED);
                gameBlockFragment.gameStart();
                btnsetting.setEnabled(false);
                btnScoreList.setEnabled(false);
            }else if (gameBlockFragment.gameMode == 1){
                initGame();
                gameBlockFragment.initGame();
                btnsetting.setEnabled(true);
                btnScoreList.setEnabled(true);
            }
        }else {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container, gameBlockFragment);
            fragmentTransaction.commit();
            isInGame = true;
        }

    }

    public void scoreList(View view) {
        exitedGame();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, scoreListFragment);
        fragmentTransaction.commit();
    }

//    public void showScoreList(Integer score) {
//        exitedGame();
//
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.replace(R.id.container, scoreListFragment);
//        fragmentTransaction.commit();
//    }
    

    private void exitedGame(){
        isInGame = false;
        btnStart.setText("返回遊戲");
    }

    public ScoreListFragment getScoreListFragment(){
        return scoreListFragment;
    }

    //寫入一筆得分紀錄
    public void addScore(int score){
        ScoreHistory scoreHistory = new ScoreHistory();
        scoreHistory.setScore(score);
        scoreHistory.setTimeStamp(getTime());
        data.add(scoreHistory);
        for (int i = data.size()-1; i > 0; i--) {
            if (data.get(i).getScore() > data.get(i-1).getScore()){
                ScoreHistory temp = data.get(i-1);
                data.set((i-1),data.get(i));
                data.set(i,temp);
            }
        }
    }

    private String getTime(){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    public class ScoreHistory{
        private Integer score;
        private String timeStamp;

        ScoreHistory() {
        }

        public String getTimeStamp() {
            return timeStamp;
        }

        public void setTimeStamp(String timeStamp) {
            this.timeStamp = timeStamp;
        }

        public Integer getScore() {
            return score;
        }

        public void setScore(Integer score) {
            this.score = score;
        }
    }

}