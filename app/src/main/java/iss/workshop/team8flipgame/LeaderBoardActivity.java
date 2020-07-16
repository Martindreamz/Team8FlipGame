package iss.workshop.team8flipgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.util.List;

import iss.workshop.team8flipgame.model.Score;
import iss.workshop.team8flipgame.repo.DBService;

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
        Intent music = new Intent(this, BGMusicService.class);
        bindService(music, this, BIND_AUTO_CREATE);
    }

    //Bianca Music Service
    //@Override
    public void onServiceConnected(ComponentName name, IBinder binder){
        BGMusicService.LocalBinder musicBinder = (BGMusicService.LocalBinder) binder;
        if(binder != null && !IS_MUTED) {
            bgMusicService = musicBinder.getService();
            bgMusicService.playMusic("LEADER_BOARD");
            Log.i("MusicLog", "BGMusicService Connected, state: play MENU.");
        }
    }
    @Override
    public void onServiceDisconnected(ComponentName name){
        Log.i("MusicLog", "BGMusicService DIS-Connected.");

    }
}