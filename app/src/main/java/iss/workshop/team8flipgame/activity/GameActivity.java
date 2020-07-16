package iss.workshop.team8flipgame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.GridView;

import java.util.ArrayList;

import iss.workshop.team8flipgame.model.Score;
import iss.workshop.team8flipgame.service.BGMusicService;
import iss.workshop.team8flipgame.R;
import iss.workshop.team8flipgame.adapter.ImageAdapter;
import iss.workshop.team8flipgame.model.Image;
import iss.workshop.team8flipgame.service.DBService;

public class GameActivity extends AppCompatActivity implements ServiceConnection, View.OnClickListener {
    ArrayList<Image> images;
    BGMusicService bgMusicService;
    static ArrayList<Bitmap> matchedBitmap = new ArrayList<>();
    Boolean IS_MUTED = false ; //Setting of BG Music
    final Context context = this;
    AlertDialog alertDialog;
    View dialogView;
    Button buttonOK;
    EditText nameId;

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

    @Override
    public void onClick(View view){
        int id = view.getId();
            dialogBox();
        if(id == R.id.btnOK){
            nameId = dialogView.findViewById(R.id.name);
            String name = nameId.getText().toString();
            System.out.println(name);
            alertDialog.dismiss();
            System.out.println("it is dismissed");
        }
    }

    public void dialogBox(){
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.dialogbox, null);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();

        final Button buttonOK = dialogView.findViewById(R.id.btnOK);
        buttonOK.setOnClickListener(this);
        buttonOK.setEnabled(false);

        nameId = dialogView.findViewById(R.id.name);
        nameId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(nameId.getText().length() == 0){
                    buttonOK.setEnabled(false);
                }
                else{
                    buttonOK.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

}