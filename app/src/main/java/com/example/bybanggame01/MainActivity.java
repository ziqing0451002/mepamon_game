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

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

//    private FragmentManager fragmentManager;
//    private ScoreListFragment scoreListFragment;
//    private GameBlockFragment gameBlockFragment;

    //元件
    private ImageView imgHeart01 ;
    private ImageView imgHeart02 ;
    private ImageView imgHeart03 ;
    private TextView textCountDown;
    private TextView textScore;
    private Button btnStart;
    private ImageView[] imgBlocklist = new ImageView[20];
    private int[] imgBlockElement={
            R.id.img_block_1,R.id.img_block_2,R.id.img_block_3,R.id.img_block_4,R.id.img_block_5,
            R.id.img_block_6,R.id.img_block_7,R.id.img_block_8,R.id.img_block_9,R.id.img_block_10,
            R.id.img_block_11,R.id.img_block_12,R.id.img_block_13,R.id.img_block_14,R.id.img_block_15,
            R.id.img_block_16,R.id.img_block_17,R.id.img_block_18,R.id.img_block_19,R.id.img_block_20
    };
    //內部變數
    private Integer timeLimit = 30; //遊戲時間（影響倒數，出塊頻錄，出塊位址）
    private Integer countDown;
    private Integer score;
    private int[] blockImgForRate = {R.drawable.bybang_correct,R.drawable.bybang_correct,R.drawable.bybang_correct,R.drawable.bybang_correct,R.drawable.bybang_wrong};
    private ArrayList<Integer> blockShowInit ;
    private ArrayList<Integer> imgShowInit ;
    private int imageShowPoint = 0;
    private int life;
    private ArrayList<Boolean> blockTapRecord;

    //背景變數
    private Timer timer;
    private TaskCountDown taskCountDown;
    private TaskShowBlock taskShowBlock;
    private Integer gameMode;
    private CountDownHandler countDownHandler;
    private BlockShowHandler blockShowHandler;
    private BlockTapHandler blockTapHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        timer = new Timer();
        countDownHandler = new CountDownHandler();
        blockShowHandler = new BlockShowHandler();
        blockTapHandler = new BlockTapHandler();

//        fragmentManager = getSupportFragmentManager();
//        scoreListFragment = new ScoreListFragment();
//        gameBlockFragment = new GameBlockFragment();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.container, gameBlockFragment);
//        fragmentTransaction.commit();

        initView();
        initGame();

    }

    //初始化畫面
    private void initView(){
        imgHeart01 = findViewById(R.id.img_heart01);
        imgHeart02 = findViewById(R.id.img_heart02);
        imgHeart03 = findViewById(R.id.img_heart03);
        textCountDown = findViewById(R.id.text_countdown);
        textScore = findViewById(R.id.text_score);
        btnStart = findViewById(R.id.btn_start);

        for (int i = 0; i < imgBlockElement.length; i++) {
            imgBlocklist[i] = findViewById(imgBlockElement[i]);
        }
    }

    //初始化遊戲
    private void initGame(){
        //顯示三顆生命
        imgHeart01.setVisibility(View.VISIBLE);
        imgHeart02.setVisibility(View.VISIBLE);
        imgHeart03.setVisibility(View.VISIBLE);
        life = 3;
        //尚未開始遊戲
        gameMode = 0;
        //初始化時間
        countDown = timeLimit;
        textCountDown.setText(String.valueOf(countDown));
        //初始化分數
        score = 0;
        textScore.setText("得分："+ score);
        //初始化開始按鈕
        btnStart.setText("開始");
        btnStart.setBackgroundColor(Color.BLUE);
        btnStart.setTextColor(Color.WHITE);
        btnStart.setEnabled(true);
        //初始化週期任務
        if(taskCountDown != null){
            taskCountDown.cancel();
            taskCountDown = null;
        }
        if(taskShowBlock != null){
            taskShowBlock.cancel();
            taskShowBlock = null;
        }
        //初始化遊戲區
        if (imageShowPoint!=0){
            imgBlocklist[blockShowInit.get(imageShowPoint-1)].setImageResource(0);
        }
        blockShowInit = new ArrayList<>();
        imgShowInit = new ArrayList<>();
        imageShowPoint = 0;
        blockTapRecord =new ArrayList<>();
        //產生圖片位置亂數陣列
        for (int i = 0; i < timeLimit; i++) {
            blockTapRecord.add(false);
            int randomNum = (int) (Math.random()*20);
            if (i != 0 && randomNum != blockShowInit.get(i-1)){
                blockShowInit.add(randomNum);
            }else if (i == 0){
                blockShowInit.add(randomNum);
            }else {
                randomNum = (randomNum+1) % 20;
                blockShowInit.add(randomNum);
            }
        }
        //產生圖片種類亂數陣列
        for (int i = 0; i < timeLimit; i++) {
            int randomNum = (int) (Math.random()*5);
            imgShowInit.add(randomNum);
        }

        for (int i = 0; i < 20; i++) {
            int targetPos = i;
            imgBlocklist[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Message message = new Message();
                    Bundle bundle = new Bundle();
                    bundle.putInt("targetPos",targetPos);
                    message.setData(bundle);
                    blockTapHandler.sendMessage(message);
                }
            });
        }

    }

    //遊戲開始按鈕
    public void gameStart(View view) {
        if (gameMode == 0){
            //開始倒數計時
            taskCountDown = new TaskCountDown();
            timer.schedule(taskCountDown,0*1000,1*1000);
            taskShowBlock = new TaskShowBlock();
            timer.schedule(taskShowBlock,0*1000,1*1000);
            //按鈕變成重玩
            btnStart.setText("重玩");
            btnStart.setTextColor(Color.BLACK);
            btnStart.setBackgroundColor(Color.RED);
            gameMode = 1;
        }else if (gameMode == 1){
            initGame();
        }
    }


    //遊戲計時器
    private class TaskCountDown extends TimerTask{
        @Override
        public void run() {
            if (countDown >= 0){
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("countDown",countDown);
                message.setData(bundle);
                countDownHandler.sendMessage(message);
                countDown --;
            }else {
                if(taskCountDown != null){
                    taskCountDown.cancel();
                    taskCountDown = null;
                }
            }
        }
    }

    //遊戲區塊產生
    private class TaskShowBlock extends TimerTask{
        @Override
        public void run() {
            if (taskCountDown != null){
                Message message = new Message();
                Bundle bundle = new Bundle();
                bundle.putInt("imageShowPoint",imageShowPoint);
                message.setData(bundle);
                blockShowHandler.sendMessage(message);
                if (imageShowPoint < timeLimit){
                    imageShowPoint ++;
                }
            }else {
                if(taskShowBlock != null){
                    taskShowBlock.cancel();
                    taskShowBlock = null;
                }
            }

        }
    }

    private class CountDownHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            //監聽秒數倒數
            int newCountDown = msg.getData().getInt("countDown");
            if (newCountDown > 0) {
                textCountDown.setText(String.valueOf(newCountDown));
            }else if (newCountDown == 0){
                textCountDown.setText(String.valueOf(newCountDown));
                getDialog();
            }

        }
    }

    private class BlockShowHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            //監聽秒數更改圖片產生位置
            int newImageShowPoint = msg.getData().getInt("imageShowPoint");
            if (newImageShowPoint == 0){
                imgBlocklist[blockShowInit.get(newImageShowPoint)].setImageResource(blockImgForRate[imgShowInit.get(newImageShowPoint)]);
            }else if (newImageShowPoint < timeLimit){
                imgBlocklist[blockShowInit.get(newImageShowPoint-1)].setImageResource(0);
                imgBlocklist[blockShowInit.get(newImageShowPoint)].setImageResource(blockImgForRate[imgShowInit.get(newImageShowPoint)]);
                System.out.println("出現位址：" + newImageShowPoint + ", 出現格子："+ blockShowInit.get(newImageShowPoint));
            }

        }
    }

    private class BlockTapHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            //監聽點擊圖片
            int realImageShowPoint = imageShowPoint - 1;
            int targetPos = msg.getData().getInt("targetPos");
            System.out.println("出現位址：" + realImageShowPoint + ", 出現格子："+ blockShowInit.get(realImageShowPoint));
            System.out.println("點擊位址：" + targetPos);
            System.out.println("是否已點擊：" + blockTapRecord.get(realImageShowPoint));
            if (targetPos == blockShowInit.get(realImageShowPoint)){
                if (blockImgForRate[imgShowInit.get(realImageShowPoint)] == R.drawable.bybang_correct && !blockTapRecord.get(realImageShowPoint)){
                    if (countDown != 0){
                        score = score + 10;
                        imgBlocklist[blockShowInit.get(realImageShowPoint)].setImageResource(R.drawable.bybang_correct_catch);
                        blockTapRecord.set(realImageShowPoint,true);
                    }
                }else if (blockImgForRate[imgShowInit.get(realImageShowPoint)] == R.drawable.bybang_wrong && !blockTapRecord.get(realImageShowPoint)) {
                    if (countDown != 0) {
                        imgBlocklist[blockShowInit.get(realImageShowPoint)].setImageResource(R.drawable.bybang_wrong_catch);
                        blockTapRecord.set(realImageShowPoint,true);
                        if (life == 3){
                            imgHeart03.setVisibility(View.INVISIBLE);
                            life -- ;
                        }else if (life == 2){
                            imgHeart02.setVisibility(View.INVISIBLE);
                            life -- ;
                        }else if (life == 1){
                            imgHeart01.setVisibility(View.INVISIBLE);
                            life -- ;
                            if(taskCountDown != null){
                                taskCountDown.cancel();
                                taskCountDown = null;
                            }
                            if(taskShowBlock != null){
                                taskShowBlock.cancel();
                                taskShowBlock = null;
                            }
                            getDialog();
                        }
                    }
                }
                textScore.setText("得分："+ score);
            }
        }
    }

    private void getDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(false);
        //這邊是設定使用者可否點擊空白處返回
        //builder.setIcon();
        //setIcon可以在Title旁邊放一個小插圖
        builder.setTitle("遊戲結束");
        builder.setMessage("得分：" +score);
        //alterdialog最多可以放三個按鈕，且位置是固定的，分別是
        //setPositiveButton()右邊按鈕
        //setNegativeButton()中間按鈕
        //setNeutralButton()左邊按鈕
        builder.setNegativeButton("確認", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                initGame();
            }
        });
        builder.create().show();
    }

}