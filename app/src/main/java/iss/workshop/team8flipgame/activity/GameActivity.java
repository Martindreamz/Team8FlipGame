package iss.workshop.team8flipgame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.GridView;

import java.util.ArrayList;

import iss.workshop.team8flipgame.service.BGMusicService;
import iss.workshop.team8flipgame.R;
import iss.workshop.team8flipgame.adapter.ImageAdapter;
import iss.workshop.team8flipgame.model.Image;

public class GameActivity extends AppCompatActivity implements ServiceConnection {
    ArrayList<Image> images;
    BGMusicService bgMusicService;
    static ArrayList<Bitmap> matchedBitmap = new ArrayList<>();
    Boolean IS_MUTED = false ; //Setting of BG Music

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

        GridView gridView = (GridView) findViewById(R.id.gridViewGame);
        ImageAdapter imageAdapter = new ImageAdapter(this, images);
        gridView.setAdapter(imageAdapter);
        gridView.setVerticalScrollBarEnabled(false);

        //Bianca Music Service
        Log.i("MusicLog", "Already in game activity");
        Intent music = new Intent(this, BGMusicService.class);
        bindService(music, this, BIND_AUTO_CREATE);

    }

    //Bianca Music Service
    //@Override
    public void onServiceConnected(ComponentName name, IBinder binder){
        BGMusicService.LocalBinder musicBinder = (BGMusicService.LocalBinder) binder;
        if(binder != null && !IS_MUTED) {
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