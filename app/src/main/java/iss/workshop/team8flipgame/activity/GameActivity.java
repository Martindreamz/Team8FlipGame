package iss.workshop.team8flipgame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Chronometer;
import android.widget.GridView;

import java.util.ArrayList;

import iss.workshop.team8flipgame.model.Score;
import iss.workshop.team8flipgame.service.BGMusicService;
import iss.workshop.team8flipgame.R;
import iss.workshop.team8flipgame.adapter.ImageAdapter;
import iss.workshop.team8flipgame.model.Image;
import iss.workshop.team8flipgame.service.DBService;

public class GameActivity extends AppCompatActivity implements ServiceConnection {
    ArrayList<Image> images;
    BGMusicService bgMusicService;
    static ArrayList<Bitmap> matchedBitmap = new ArrayList<>();
    Boolean IS_MUTED = false ; //Setting of BG Music

    //For Score calculation
    private static final int NUM_OF_CARDS = 6;
    private int numOfAttempts = 0;

    private Chronometer chronometer;
    private boolean isGameFinished = false;
    private long totalTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        images = new ArrayList<>();

        Intent intent = getIntent();

//        ArrayList<Bitmap> selectedBitmap = intent.getParcelableArrayListExtra("bitmapBytes");
        ArrayList<Integer> selectedCell = intent.getIntegerArrayListExtra("selectedCells");


        for(int i : selectedCell){
            images .add (ImagePickingActivity.selectedImage.get(i));
            images .add (ImagePickingActivity.selectedImage.get(i));
//            images.add(selectedBitmap.get(i));
        }
//        Collections.shuffle(images);
        chronometer = findViewById(R.id.chronometer);

        GridView gridView = (GridView) findViewById(R.id.gridViewGame);
        ImageAdapter imageAdapter = new ImageAdapter(this, images);
        gridView.setAdapter(imageAdapter);
        gridView.setVerticalScrollBarEnabled(false);

        //Bianca Music Service
        IS_MUTED = intent.getBooleanExtra("IS_MUTED",false);
        if (!IS_MUTED) {
            Intent music = new Intent(this, BGMusicService.class);
            bindService(music, this, BIND_AUTO_CREATE);
        }

    }

    public int calculateScore(int totalTime,int numOfAttempts){
        return (5 * NUM_OF_CARDS) + (500 / numOfAttempts) + (5000 / totalTime);
    }

    public void finishedGame(int totalTime,int numOfAttempts){
        int totalScore = calculateScore(60,15);
        Score scoreObj = new Score("Theingi",totalScore);
        DBService db = new DBService(this);
        db.addScore(scoreObj);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (!isGameFinished)
        {
            chronometer.setBase(SystemClock.elapsedRealtime() - totalTime);
            chronometer.start();
        }
    }
    //Bianca Lifecycle
    @Override
    public void onPause(){
        super.onPause();
        // send notification : Want to continue or end the current Game
    }
    @Override
    public void onResume(){
        super.onResume();
        // restore game
    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        // end everything
    }

    //Bianca Music Service
    //@Override
    public void onServiceConnected(ComponentName name, IBinder binder){
        BGMusicService.LocalBinder musicBinder = (BGMusicService.LocalBinder) binder;
        if(binder != null) {
            bgMusicService = musicBinder.getService();
            bgMusicService.playMusic("GAME");
            Log.i("MusicLog", "BGMusicService Connected, state: play GAME.");
        }
    }
    @Override
    public void onServiceDisconnected(ComponentName name){
        Log.i("MusicLog", "BGMusicService DIS-Connected.");
    }

}