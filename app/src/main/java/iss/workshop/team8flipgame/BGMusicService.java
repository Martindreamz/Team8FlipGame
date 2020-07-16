package iss.workshop.team8flipgame;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Switch;

public class BGMusicService extends Service {
    MediaPlayer BGMusicPlayer;
    //IN ACTIVITY:
    //if user choose to mute this attr. (IS_MUTED)become true.
    //Then service becomes unbind also.
    //Boolean IS_MUTED = false ;

    //use binder to connect service and activity
    private final IBinder binder = new LocalBinder();
    public class LocalBinder extends Binder {
        BGMusicService getService(){
            return BGMusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent){
        Log.i("MusicLog", "BackgroundMusicService -> onBind, Thread: " + Thread.currentThread().getName());
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent){
        Log.i("MusicLog", "BackgroundMusicService -> onUnBind");
        return super.onUnbind(intent);
    }
    @Override
    public void onDestroy() {
        Log.i("MusicLog", "BackgroundMusicService -> onDestroy");
        super.onDestroy();
    }


    public void playMusic(String scene){
        if(BGMusicPlayer != null) {
            BGMusicPlayer.reset();
            //BGMusicPlayer.release();
            //BGMusicPlayer = null;
        }
        switch (scene) {
            case "GAME": {
                BGMusicPlayer = MediaPlayer.create(this, R.raw.rainbow);
                Log.i("MusicLog", "Play music GAME");
                break;
            }
            case "MENU": {
                BGMusicPlayer = MediaPlayer.create(this, R.raw.menu);
                Log.i("MusicLog", "Play music MENU");
                break;
            }
            case "LEADER_BOARD": {
                BGMusicPlayer = MediaPlayer.create(this, R.raw.leader_board);
                break;
            }
        }
        BGMusicPlayer.setLooping(true);
        BGMusicPlayer.start();
    }

    public void stop() {
        if (BGMusicPlayer != null && BGMusicPlayer.isPlaying()) {//只有播放器已初始化并且正在播放才可暂停
            BGMusicPlayer.stop();
        } else { return;
        }
    }

    public void mute() {
        if (BGMusicPlayer != null && BGMusicPlayer.isPlaying()) {
            BGMusicPlayer.stop();
            BGMusicPlayer.release();
            BGMusicPlayer = null;
        }
    }
/*
    public void playMenuMusic(){
        if(BGMusicPlayer != null) BGMusicPlayer.reset();//release the resources
        BGMusicPlayer = MediaPlayer.create(this, R.raw.menu);
        BGMusicPlayer.setLooping(true);
        BGMusicPlayer.start();
    }
    public void playGameMusic(){
        if(BGMusicPlayer != null) BGMusicPlayer.reset();
        BGMusicPlayer = MediaPlayer.create(this, R.raw.rainbow);
        BGMusicPlayer.setLooping(true);
        BGMusicPlayer.start();
    }
    public void playLBMusic(){
        if(BGMusicPlayer != null) BGMusicPlayer.reset();
        BGMusicPlayer = MediaPlayer.create(this,R.raw.leader_board);
        BGMusicPlayer.setLooping(true);
        BGMusicPlayer.start();
    }*/


}

