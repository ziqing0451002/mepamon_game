package com.example.bybanggame01;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Objects;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    //畫面元件
    private FragmentManager fragmentManager;
    private ScoreListFragment scoreListFragment;
    private GameBlockFragment gameBlockFragment;
    private SettingFragment settingFragment;
    public Button btnsetting;
    private Button btnStart;
    public Button btnScoreList;

    //內部變數
    private Boolean isInGame;
    private Boolean isBound;
    private Boolean isBgmPlaying;

    //共用變數
    public LinkedList<ScoreHistory> data;
    public SharedPreferences sharedPreferences;
    public SharedPreferences.Editor editor;
    public GameService gameService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        scoreListFragment = new ScoreListFragment();
        gameBlockFragment = new GameBlockFragment();
        settingFragment = new SettingFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container, gameBlockFragment);
        fragmentTransaction.commit();

        initView();

        sharedPreferences = getSharedPreferences("game",MODE_PRIVATE);
        editor = sharedPreferences.edit();

    }

    //畫面初始化
    private void initView(){
        btnStart = findViewById(R.id.btn_start);
        btnsetting = findViewById(R.id.btn_setting);
        btnScoreList = findViewById(R.id.btn_score_list);
        isInGame = true;

    }

    //得分紀錄初始化
    private void initScoreHistory(){
        data = new LinkedList<>();
        int scoreHistorySize = sharedPreferences.getInt("dataSize",0);
        for (int i = 0; i < scoreHistorySize; i++) {
            int score = sharedPreferences.getInt("data_"+i+"_score",-1);
            String timestamp = sharedPreferences.getString("date_"+i+"_timestamp",null);
            ScoreHistory scoreHistory = new ScoreHistory();
            scoreHistory.setScore(score);
            scoreHistory.setTimeStamp(timestamp);
            data.add(scoreHistory);
        }
    }

    //遊戲初始化
    public void initGame(){
        //初始化開始按鈕
        btnStart.setText("開始");
        btnStart.setBackgroundColor(Color.BLUE);
        btnStart.setTextColor(Color.WHITE);
        btnStart.setEnabled(true);
        initScoreHistory();
    }

    //觸發遊戲開始按鈕
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

    //觸發設定按鈕
    public void setting(View view) {
        exitedGame();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, settingFragment);
        fragmentTransaction.commit();
    }

    //觸發得分紀錄按鈕
    public void scoreList(View view) {
        exitedGame();

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container, scoreListFragment);
        fragmentTransaction.commit();
    }
    
    //離開遊戲畫面（跳轉設定or得分紀錄）
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

        editor.putInt("dataSize",data.size());
        for (int i = 0; i < data.size(); i++) {
            editor.putInt("data_"+i+"_score", data.get(i).getScore());
            editor.putString("date_"+i+"_timestamp",data.get(i).getTimeStamp());
        }
        editor.commit();
    }

    //獲得現在時間
    private String getTime(){
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
        return formatter.format(currentTime);
    }

    //得分紀錄物件
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

    //綁定遊戲背景音樂Service
    private ServiceConnection gameConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            GameService.LocalBinder binder = (GameService.LocalBinder) iBinder;
            gameService = binder.getService();
            isBound = true;
            Boolean isBgmOn = sharedPreferences.getBoolean("bgm",true);
            if (isBgmOn) {
                gameService.playMusic();
            }else {
                gameService.stopMusic();
            }
            isBgmPlaying = true;
            int bgmVolume = sharedPreferences.getInt("bgm_volume", 50);
            gameService.setVolume(bgmVolume);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            isBound = false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this,GameService.class);
        bindService(intent,gameConnection, Context.BIND_AUTO_CREATE);


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isBound){
            unbindService(gameConnection);
            isBound = false;
        }
        if (isBgmPlaying){
            gameService.stopMusic();
        }
    }

    //清除得分紀錄
    public void clearScoreList(View view) {
        int scoreHistorySize = sharedPreferences.getInt("dataSize",0);
        for (int i = 0; i < scoreHistorySize; i++) {
            editor.remove("dataSize");
            editor.remove("data_"+i+"_score");
            editor.remove("date_"+i+"_timestamp");
        }
        editor.commit();
    }

}