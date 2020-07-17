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
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

import iss.workshop.team8flipgame.model.Score;
import iss.workshop.team8flipgame.service.BGMusicService;
import iss.workshop.team8flipgame.R;
import iss.workshop.team8flipgame.adapter.ImageAdapter;
import iss.workshop.team8flipgame.model.Image;
import iss.workshop.team8flipgame.service.DBService;

public class GameActivity extends AppCompatActivity implements ServiceConnection, View.OnClickListener , AdapterView.OnItemClickListener {
    ArrayList<Image> images;
    BGMusicService bgMusicService;
    static ArrayList<Bitmap> matchedBitmap = new ArrayList<>();
    Boolean IS_MUTED = false ; //Setting of BG Music
    final Context context = this;
    AlertDialog alertDialog;
    View dialogView;
    Button buttonOK;
    EditText nameId;
    TextView txtScore;
    public ArrayList<Bitmap> barray = new ArrayList<>();
    ArrayList<ImageView> seleted_view = new ArrayList<>();
    GridView gridView;
    ArrayList<Integer> selectedMatch;
    TextView matches;
    int matched;
    long elapsedMillis;

    //For Score calculation
    private static final int NUM_OF_CARDS = 6;
    private int numOfAttempts = 0;

    private Chronometer chronometer;
    private boolean isGameFinished = false;
    private long totalTime = 0;
    private int totalScore=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

//        variables
        images = new ArrayList<>();
        selectedMatch = new ArrayList<>();
        Intent intent = getIntent();
        ArrayList<Integer> selectedCell = intent.getIntegerArrayListExtra("selectedCells");

//        top bar
        chronometer = findViewById(R.id.chronometer);
        matches = findViewById(R.id.matches);

//        Grid view
        for(int i : selectedCell){
            images .add (ImagePickingActivity.selectedImage.get(i));
            images .add (ImagePickingActivity.selectedImage.get(i));
        }
        Collections.shuffle(images);
        gridView =findViewById(R.id.gridViewGame);
        ImageAdapter imageAdapter = new ImageAdapter(this, images);
        gridView.setAdapter(imageAdapter);
        gridView.setVerticalScrollBarEnabled(false);
        gridView.setOnItemClickListener(this);

        //Bianca Music Service
        IS_MUTED = intent.getBooleanExtra("IS_MUTED",false);
        if (!IS_MUTED) {
            Intent music = new Intent(this, BGMusicService.class);
            bindService(music, this, BIND_AUTO_CREATE);
        }

    }

    public int calculateScore(long totalTime,int numOfAttempts){
        return (int) ((5 * NUM_OF_CARDS) + (500 / numOfAttempts) + (5000 / (totalTime/1000)));
    }

    public void finishedGame(String name ,int totalScore){
        Score scoreObj = new Score(name,totalScore);
        DBService db = new DBService(this);
        db.addScore(scoreObj);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        if (!isGameFinished)
        {
            chronometer.setBase(SystemClock.elapsedRealtime() - elapsedMillis);
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
        if (!isGameFinished)
        {
            chronometer.setBase(SystemClock.elapsedRealtime() - elapsedMillis);
            chronometer.start();
        }
        super.onResume();
        // restore game
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // end everything
    }

    @Override
    public void onStop() {
        super.onStop();
        elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
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
        //dialogBox();
        if(id == R.id.btnOK){
            nameId = dialogView.findViewById(R.id.name);
            String playerName = nameId.getText().toString();

            finishedGame(playerName,totalScore);

            System.out.println(playerName);
            alertDialog.dismiss();
            System.out.println("it is dismissed");

            Intent intentForLeaderBoard = new Intent(this,LeaderBoardActivity.class);
            startActivity(intentForLeaderBoard);
        }
    }

    public void dialogBox(long totalTime, int numOfAttempts){
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.dialogbox, null);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.show();

        txtScore = dialogView.findViewById(R.id.txtScore);
        totalScore = calculateScore(totalTime,numOfAttempts);
        txtScore.setText(totalScore+ " points");

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

    final Handler handler = new Handler();
    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            seleted_view.get(1).setImageBitmap(null);
            seleted_view.get(0).setImageBitmap(null);
            barray.clear();
            seleted_view.clear();
        }
    };
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(!selectedMatch.contains(Integer.valueOf(i))){
            System.out.println("Game Activity " + i);

            Image image = images.get(i);
            ViewGroup gridElement = (ViewGroup) gridView.getChildAt(i);
            ImageView currentImage= (ImageView) gridElement.getChildAt(0);
            currentImage.setImageBitmap(image.getBitmap());

            if(barray.size()<2){
                Bitmap b = image.getBitmap();
                barray.add(b);
                selectedMatch.add(i);
                seleted_view.add(currentImage);
            }

            if(barray.size()==2){
                System.out.println("pos2");
                if(barray.get(0) == barray.get(1)){
                    System.out.println("pos2.1");
                    System.out.println("same");
                    barray.clear();
                    seleted_view.clear();
                    matched++;
                    numOfAttempts++;
                    matches.setText(matched+"/"+NUM_OF_CARDS+" matches");
                    if(matched==NUM_OF_CARDS){
                        isGameFinished=true;
//                        try {
//                            Thread.sleep(5000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
                        elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                        chronometer.stop();
                        System.out.println("total time " + elapsedMillis);

//                        dialogBox();
//                        finishedGame(nameId.getText().toString(),calculateScore((int)elapsedMillis,numOfAttempts));

                    }
                }
                else{
                    System.out.println("pos2.2");
                    System.out.println("not same");
                    numOfAttempts++;
                    selectedMatch.remove(selectedMatch.size()-1);
                    selectedMatch.remove(selectedMatch.size()-1);
                    handler.postDelayed(runnable,300);
                }
            }

            if(isGameFinished == true){
                int id = view.getId();
                dialogBox(elapsedMillis,numOfAttempts);
                if(id == R.id.btnOK){
                    nameId = dialogView.findViewById(R.id.name);
                    String playerName = nameId.getText().toString();

                    finishedGame(playerName,totalScore);

                    System.out.println(playerName);
                    alertDialog.dismiss();
                    System.out.println("it is dismissed");

                    Intent intentForLeaderBoard = new Intent(this,LeaderBoardActivity.class);
                    startActivity(intentForLeaderBoard);
                }
            }
        }
    }
}