package iss.workshop.team8flipgame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import iss.workshop.team8flipgame.R;
import iss.workshop.team8flipgame.service.BGMusicService;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class HomeActivity extends AppCompatActivity
        implements View.OnClickListener , ServiceConnection {
    BGMusicService bgMusicService;
    //public Boolean IS_MUTED;//= false ; //Setting of BG Music
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming);
        GifImageView gifImageView = (GifImageView) findViewById(R.id.gifImageView);
        GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        Button play = findViewById(R.id.play);
        if (play != null) {
            play.setOnClickListener(this);
        }
        Button leader = findViewById(R.id.leaderBoard);
        if (leader != null) {
            leader.setOnClickListener(this);
        }
        Button credits = findViewById(R.id.credits);
        if (credits != null) {
            credits.setOnClickListener(this);
        }
        ImageButton toggle = findViewById(R.id.soundToggle);
        if (toggle != null) {
            toggle.setOnClickListener(this);
        }

        sharedPref = getSharedPreferences("music_service",MODE_PRIVATE);
        if (!sharedPref.contains("IS_MUTED")) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean("IS_MUTED", false);
            editor.commit();
        }

        //Bianca Music Service
        if (!sharedPref.getBoolean("IS_MUTED",false)) {
            Intent music = new Intent(this, BGMusicService.class);
            bindService(music, this, BIND_AUTO_CREATE);
        }

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.play) {
            Intent intent = new Intent(this, ImagePickingActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.leaderBoard) {
            Intent intent = new Intent(this, LeaderBoardActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.soundToggle) {
            SharedPreferences.Editor editor = sharedPref.edit();
            if (sharedPref.getBoolean("IS_MUTED",false)) {
                Log.i("MusicLog", "BGMusicService -> UNMUTED");
                editor.putBoolean("IS_MUTED", false);
                Intent music = new Intent(this, BGMusicService.class);
                bindService(music, this, BIND_AUTO_CREATE);
                Toast.makeText(getApplicationContext(),"Un-muted music successfully!",Toast.LENGTH_SHORT).show();
            }
            else {
                bgMusicService.mute();
                unbindService(this);
                editor.putBoolean("IS_MUTED", true);
                Log.i("MusicLog", "BGMusicService -> MUTED");
                Toast.makeText(getApplicationContext(),"Muted music successfully!",Toast.LENGTH_SHORT).show();
            }
            editor.commit();
        }

        else if (id == R.id.credits) {
            Intent intent = new Intent(this,CreditsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        // pause music
        if(bgMusicService!=null) bgMusicService.pause();
    }
    @Override
    public void onResume(){
        super.onResume();
        // restore
        if(bgMusicService!=null) bgMusicService.resume();
        else if(!sharedPref.getBoolean("IS_MUTED",false)) {
            Intent music = new Intent(this, BGMusicService.class);
            bindService(music, this, BIND_AUTO_CREATE);
        }

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        if(bgMusicService!=null)
            unbindService(this);// unbindService
        // end everything
    }

    //Bianca Music Service
    //@Override
    public void onServiceConnected(ComponentName name, IBinder binder){
        BGMusicService.LocalBinder musicBinder = (BGMusicService.LocalBinder) binder;
        if(binder != null) {
            bgMusicService = musicBinder.getService();
            bgMusicService.playMusic("MENU");
            Log.i("MusicLog", "BGMusicService Connected, location: HOME.");
        }
    }
    @Override
    public void onServiceDisconnected(ComponentName name){
        Log.i("MusicLog", "BGMusicService DIS-Connected.");

    }


}