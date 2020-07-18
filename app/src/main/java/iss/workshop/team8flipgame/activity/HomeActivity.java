package iss.workshop.team8flipgame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.wajahatkarim3.easyflipview.EasyFlipView;

import iss.workshop.team8flipgame.R;
import iss.workshop.team8flipgame.service.BGMusicService;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class HomeActivity extends AppCompatActivity
        implements View.OnClickListener , ServiceConnection {

    //attributes
    private BGMusicService bgMusicService;
    private Boolean IS_MUTED = false ; //Setting of BG Music
    private Button mPlayBtn;
    private Button mLeaderBtn;
    private Button mCreditsBtn;
    private ImageView logoOrange;
    private ImageView logoPink;
    private Thread logoService;
    private int cardCount;
    private RadioGroup difficulties;
    private String difficulty;
    private SharedPreferences game_pref;
    private SharedPreferences music_pref;
    private SharedPreferences.Editor game_pref_editor;
    private SharedPreferences.Editor music_pref_editor;
    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming);
        game_pref = getSharedPreferences("game_service",MODE_PRIVATE);
        game_pref_editor = game_pref.edit();
        logoOrange=findViewById(R.id.app_name_orange);
        logoPink=findViewById(R.id.app_name_pink);
        logoPink.setVisibility(View.GONE);

        //top bar
        EasyFlipView toggle = findViewById(R.id.flipToggle);
        if (toggle != null) {
            toggle.setOnClickListener(this);
        }

        music_pref = getSharedPreferences("music_service",MODE_PRIVATE);
        if (!music_pref.contains("IS_MUTED")) {
            music_pref_editor = music_pref.edit();
            music_pref_editor.putBoolean("IS_MUTED", false);
            music_pref_editor.commit();
        }

        if (!music_pref.getBoolean("IS_MUTED",false)){//not muted
            //if show front need to flip
            if (!toggle.isBackSide()) toggle.flipTheView();//"@+id/soundToggle_back"
        }
        else {//mute == music off
            // if back need to flip
            if (toggle.isBackSide()) toggle.flipTheView();//"@+id/soundToggle" front-side
        }

        //cat gif
        logoService = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(true) {
                        Thread.sleep(300);
                        {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(logoPink.getVisibility()==View.VISIBLE){
                                        logoPink.setVisibility(View.GONE);
                                    }else{logoPink.setVisibility(View.VISIBLE);}

                                    if(logoOrange.getVisibility()==View.VISIBLE){
                                        logoOrange.setVisibility(View.GONE);
                                    }else {logoOrange.setVisibility(View.VISIBLE);}
                                }
                            });
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        logoService.start();

        //play button
        mPlayBtn = findViewById(R.id.play);
        if (mPlayBtn != null) { mPlayBtn.setOnClickListener(this); }
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
                        game_pref_editor.putString("difficulty",difficulty);
                        game_pref_editor.putInt("cardcount",cardCount);
                        game_pref_editor.commit();
                        break;
                    case R.id.normal_modeRD:
                        cardCount=10;
                        difficulty="Normal";
                        game_pref_editor.putString("difficulty",difficulty);
                        game_pref_editor.putInt("cardcount",cardCount);
                        game_pref_editor.commit();
                        break;
                    case R.id.hard_modeRD:
                        cardCount=14;
                        difficulty="Hard";
                        game_pref_editor.putString("difficulty",difficulty);
                        game_pref_editor.putInt("cardcount",cardCount);
                        game_pref_editor.commit();
                        break;
                }

            }
        });

        //leaderboard button
        mLeaderBtn = findViewById(R.id.leaderBoard);
        if (mLeaderBtn != null) { mLeaderBtn.setOnClickListener(this); }

        //credits button
        mCreditsBtn = findViewById(R.id.credits);
        if (mCreditsBtn != null) { mCreditsBtn.setOnClickListener(this); }



        final GifImageView gifImageView = (GifImageView) findViewById(R.id.gifImageView);
        final GifDrawable gifDrawable = (GifDrawable) gifImageView.getDrawable();
        /* follow Martin's need keep the gif running
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
        }).start();*/

        //Bianca Music Service
        if (!music_pref.getBoolean("IS_MUTED",false)) {
            Intent music = new Intent(this, BGMusicService.class);
            bindService(music, this, BIND_AUTO_CREATE);
        }
        Log.i("music","IS_MUTED value: " + music_pref.getBoolean("IS_MUTED",false));

    }









    //onClick
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
        else if (id == R.id.flipToggle) {
            //ImageView toggle = findViewById(R.id.soundToggle);

            EasyFlipView currentView= (EasyFlipView) findViewById(R.id.flipToggle);
            currentView.flipTheView();
            music_pref_editor = music_pref.edit();
            if (music_pref.getBoolean("IS_MUTED",false)) {
                Log.i("MusicLog", "BGMusicService -> UNMUTED");
                music_pref_editor.putBoolean("IS_MUTED", false);
                Intent music = new Intent(this, BGMusicService.class);
                bindService(music, this, BIND_AUTO_CREATE);
                Toast.makeText(getApplicationContext(),"Un-muted music successfully!",Toast.LENGTH_SHORT).show();
            }
            else {
                bgMusicService.mute();
                unbindService(this);
                music_pref_editor.putBoolean("IS_MUTED", true);
                Log.i("MusicLog", "BGMusicService -> MUTED");
                Toast.makeText(getApplicationContext(),"Muted music successfully!",Toast.LENGTH_SHORT).show();
            }
            music_pref_editor.commit();
        }

        else if (id == R.id.credits) {
            Intent intent = new Intent(this,CreditsActivity.class);
            startActivity(intent);
        }
    }

    //life cycles
    @Override
    public void onPause(){
        super.onPause();
        // pause music
        if(bgMusicService!=null) bgMusicService.pause();
        logoService.interrupt();
    }
    @Override
    public void onResume(){
        super.onResume();
        // restore
        if(bgMusicService!=null) bgMusicService.resume();
        else if(!music_pref.getBoolean("IS_MUTED",false)) {
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

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    //other functions
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
        game_pref_editor.putString("difficulty",difficulty);
        game_pref_editor.putInt("cardcount",cardCount);
        game_pref_editor.commit();
    }

}