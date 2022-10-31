package com.example.bybanggame01;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

public class GameService extends Service {

    private final IBinder gameBinder = new LocalBinder();
    private MediaPlayer mediaPlayer;

    public class LocalBinder extends Binder {
        GameService getService(){
            return GameService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return gameBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mediaPlayer = MediaPlayer.create(this,R.raw.game_bgm);
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(0.5f,0.5f);

    }

    public void playMusic(){
        if (mediaPlayer != null && !mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }
    }

    public void  stopMusic(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    public void setVolume(int i){
        System.out.println("volume:"+i);
        float f = (float) i / 100;
        System.out.println("volume:"+f);
        mediaPlayer.setVolume(f,f);

    }



}