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
import android.widget.RadioGroup;
import android.widget.Toast;

import iss.workshop.team8flipgame.R;
import iss.workshop.team8flipgame.service.BGMusicService;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class HomeActivity extends AppCompatActivity
        implements View.OnClickListener , ServiceConnection {
    BGMusicService bgMusicService;
    public Boolean IS_MUTED = false ; //Setting of BG Music
    RadioGroup difficulties;
    int cardCount;
    String difficulty;
    SharedPreferences game_service;
    SharedPreferences sharedPref;
    SharedPreferences.Editor game_service_editor;
    ImageButton toggle;
    Button play;
    Button leader;
    Button credits;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming);
        game_service = getSharedPreferences("game_service",MODE_PRIVATE);
        game_service_editor = game_service.edit();

        //top bar
        toggle = findViewById(R.id.soundToggle);
        if (toggle != null) { toggle.setOnClickListener(this); }

        //play button
        play = findViewById(R.id.play);
        if (play != null) { play.setOnClickListener(this); }
        defaultPreference();

        //difficulty radio
        difficulties = findViewById(R.id.difficultyRD);
        difficulties.check(R.id.easy_modeRD);

        difficulties.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.easy_modeRD:
                        cardCount=6;
                        difficulty="Easy";
                        game_service_editor.putString("difficulty",difficulty);
                        game_service_editor.putInt("cardcount",cardCount);
                        game_service_editor.commit();
                        break;
                    case R.id.normal_modeRD:
                        cardCount=10;
                        difficulty="Normal";
                        game_service_editor.putString("difficulty",difficulty);
                        game_service_editor.putInt("cardcount",cardCount);
                        game_service_editor.commit();
                        break;
                    case R.id.hard_modeRD:
                        cardCount=14;
                        difficulty="Hard";
                        game_service_editor.putString("difficulty",difficulty);
                        game_service_editor.putInt("cardcount",cardCount);
                        game_service_editor.commit();
                        break;
                }

            }
        });

        //leaderboard button
        leader = findViewById(R.id.leaderBoard);
        if (leader != null) { leader.setOnClickListener(this); }

        //credits button
        credits = findViewById(R.id.credits);
        if (credits != null) { credits.setOnClickListener(this); }



        final GifImageView gifImageView = (GifImageView) findViewById(R.id.gifImageView);
        final GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        gifDrawable.setLoopCount(5);
        //Bianca :This special thread is to monitor the gif is finished.
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        Thread.sleep(100);
                        if(!gifDrawable.isRunning()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    gifImageView.setImageDrawable(null);//way1
                                    Log.i("gif","not running.");
                                }
                            });
                            break;
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();


        //onAnimationCompleted()

        //gifDrawable.setVisible(false,false); notwork!

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
    void defaultPreference(){
        cardCount=6;
        difficulty="Easy";
        game_service_editor.putString("difficulty",difficulty);
        game_service_editor.putInt("cardcount",cardCount);
        game_service_editor.commit();
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}