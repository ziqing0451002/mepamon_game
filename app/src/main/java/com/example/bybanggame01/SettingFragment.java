package com.example.bybanggame01;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;


public class SettingFragment extends Fragment {

    //畫面元件
    private View mainView;
    private MainActivity mainActivity;
    private Switch bgmSwitch;
    private SeekBar bgmVolumeSeekbar;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mainActivity = (MainActivity) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainView = inflater.inflate(R.layout.fragment_setting, container, false);
        initView();
        return mainView;
    }

    private void initView(){
        //背景音樂開關設定
        bgmSwitch = mainView.findViewById(R.id.bgm_switch);
        bgmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isCheck) {
                if (isCheck){
                    mainActivity.gameService.playMusic();
                }else {
                    mainActivity.gameService.stopMusic();
                }
                mainActivity.editor.putBoolean("bgm",isCheck);
                mainActivity.editor.commit();
            }
        });
        Boolean isBgmOn = mainActivity.sharedPreferences.getBoolean("bgm",true);
        System.out.println("isBgmOn:"+isBgmOn);
        bgmSwitch.setChecked(isBgmOn);

        //背景音樂音量大小設定
        bgmVolumeSeekbar = mainView.findViewById(R.id.bgm_volume);
        bgmVolumeSeekbar.setMax(100);
        int bgmVolume = mainActivity.sharedPreferences.getInt("bgm_volume", 50);
        bgmVolumeSeekbar.setProgress(bgmVolume);
        bgmVolumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                if (b) {
                    mainActivity.gameService.setVolume(progress);
                    mainActivity.editor.putInt("bgm_volume",progress);
                    mainActivity.editor.commit();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    //清除得分紀錄
    public void clearScoreList(View view) {
        int scoreHistorySize = mainActivity.sharedPreferences.getInt("dataSize",0);
        for (int i = 0; i < scoreHistorySize; i++) {
            mainActivity.editor.remove("dataSize");
            mainActivity.editor.remove("data_"+i+"_score");
            mainActivity.editor.remove("date_"+i+"_timestamp");
        }
    }
}