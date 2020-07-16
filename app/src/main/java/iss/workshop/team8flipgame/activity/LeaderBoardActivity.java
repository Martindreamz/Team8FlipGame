package iss.workshop.team8flipgame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.ListView;

import java.util.List;

import iss.workshop.team8flipgame.service.BGMusicService;
import iss.workshop.team8flipgame.R;
import iss.workshop.team8flipgame.adapter.ScoreAdapter;
import iss.workshop.team8flipgame.model.Score;
import iss.workshop.team8flipgame.service.DBService;

public class LeaderBoardActivity extends AppCompatActivity
            implements ServiceConnection {

    BGMusicService bgMusicService;
    Boolean IS_MUTED = false ; //Setting of BG Music

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        //retrieve list of scores from db
        DBService db = new DBService(this);
        List<Score> scores = db.getAllScore();
        //instantiate adapter
        ScoreAdapter adapter = new ScoreAdapter(this, R.layout.leaderboard_row, scores);
        ListView listView = findViewById(R.id.listView);
        if(listView != null){
            listView.setAdapter(adapter);
        }

        //Bianca Music Service
        Intent intent = getIntent();
        IS_MUTED = intent.getBooleanExtra("IS_MUTED",false);
        if (!IS_MUTED) {
            Intent music = new Intent(this, BGMusicService.class);
            bindService(music, this, BIND_AUTO_CREATE);
        }
    }

    //Bianca Music Service
    //@Override
    public void onServiceConnected(ComponentName name, IBinder binder){
        BGMusicService.LocalBinder musicBinder = (BGMusicService.LocalBinder) binder;
        if(binder != null) {
            bgMusicService = musicBinder.getService();
            bgMusicService.playMusic("LEADER_BOARD");
            Log.i("MusicLog", "BGMusicService Connected, state: play LeaderBoard.");
        }
    }
    @Override
    public void onServiceDisconnected(ComponentName name){
        Log.i("MusicLog", "BGMusicService DIS-Connected.");

    }
    //Bianca Lifecycle
    @Override
    public void onPause(){
        super.onPause();
        if(bgMusicService!=null) bgMusicService.pause();// pause music
    }
    @Override
    public void onResume(){
        super.onResume();
        // restore
        if(bgMusicService!=null) bgMusicService.resume();
        else if(!IS_MUTED) {
            Intent music = new Intent(this, BGMusicService.class);
            bindService(music, this, BIND_AUTO_CREATE);
        }
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        unbindService(this);// unbindService
        // end everything
    }
}