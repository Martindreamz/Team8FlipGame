package iss.workshop.team8flipgame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
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

import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.ArrayList;
import java.util.Collections;

import iss.workshop.team8flipgame.model.Score;
import iss.workshop.team8flipgame.service.BGMusicService;
import iss.workshop.team8flipgame.R;
import iss.workshop.team8flipgame.adapter.ImageAdapter;
import iss.workshop.team8flipgame.model.Image;
import iss.workshop.team8flipgame.service.DBService;

public class GameActivity extends AppCompatActivity
        implements ServiceConnection, View.OnClickListener , AdapterView.OnItemClickListener {
    ArrayList<Bitmap> barray = new ArrayList<>();
    ArrayList<EasyFlipView> seleted_view = new ArrayList<>();
    ArrayList<Image> images;
    ArrayList<Integer> selectedMatch;
    final Context context = this;
    BGMusicService bgMusicService;
    Boolean IS_MUTED = false ; //Setting of BG Music
    AlertDialog alertDialog;
    View dialogView;
    EditText nameId;
    TextView txtScore;
    GridView gridView;
    TextView matches;
    int matched;
    long elapsedMillis;
    Boolean clickable = true;
    private int NUM_OF_CARDS;
    private int numOfAttempts = 0;
    private Chronometer chronometer;
    private boolean isGameFinished = false;
    private long totalTime = 0;
    private int totalScore=0;
    SharedPreferences global_pref;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //variables
        images = new ArrayList<>();
        selectedMatch = new ArrayList<>();
        Intent intent = getIntent();
        ArrayList<Integer> selectedCell = intent.getIntegerArrayListExtra("selectedCells");
        global_pref = getSharedPreferences("game_service",MODE_PRIVATE);
        NUM_OF_CARDS = (int) global_pref.getInt("cardcount",0);
        System.out.println(NUM_OF_CARDS);


        //top bar
        chronometer = findViewById(R.id.chronometer);
        matches = findViewById(R.id.matches);
        matches.setText(matched+"/"+NUM_OF_CARDS+" matches");

        //Grid view
        for(int i : selectedCell){
            images .add (ImagePickingActivity.selectedImage.get(i));
            images .add (ImagePickingActivity.selectedImage.get(i));
        }



        Collections.shuffle(images);
        gridView =findViewById(R.id.gridViewGame);
        if(images.size()!=12){
        gridView.setNumColumns(4);
        }
        System.out.println("pos 0");

        ImageAdapter imageAdapter = new ImageAdapter(this, images);
        System.out.println("pos 1");

        gridView.setAdapter(imageAdapter);
        System.out.println("pos 2");

        gridView.setVerticalScrollBarEnabled(false);
        gridView.setOnItemClickListener(this);

        //Bianca Music Service
        SharedPreferences sharedPref = getSharedPreferences("music_service", MODE_PRIVATE);
        IS_MUTED = sharedPref.getBoolean("IS_MUTED",false);
        if (!IS_MUTED) {
            Intent music = new Intent(this, BGMusicService.class);
            bindService(music, this, BIND_AUTO_CREATE);
        }

        sharedPreferences = getSharedPreferences("game_service",MODE_PRIVATE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,ImagePickingActivity.class);
        startActivity(intent);
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
        if(id == R.id.btnOK){
            nameId = dialogView.findViewById(R.id.name);
            String playerName = nameId.getText().toString();
            finishedGame(playerName,totalScore);
            alertDialog.dismiss();
            Intent intentForLeaderBoard = new Intent(this,LeaderBoardActivity.class);
            startActivity(intentForLeaderBoard);
        }
    }

    public int calculateScore(long totalTime,int numOfAttempts){
        return (int) ((5 * NUM_OF_CARDS) + (500 / numOfAttempts) + (5000 / (totalTime/1000)));
    }

    public void finishedGame(String name ,int totalScore){
        Score scoreObj = new Score(name,totalScore, sharedPreferences.getString("difficulty", "Easy"));
        DBService db = new DBService(this);
        db.addScore(scoreObj);
    }

    public void dialogBox(long totalTime, int numOfAttempts){
        AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        dialogView = inflater.inflate(R.layout.dialogbox, null);
        builder.setView(dialogView);
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
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
            seleted_view.get(1).flipTheView();
            seleted_view.get(0).flipTheView();
            barray.clear();
            seleted_view.clear();
            clickable=true;
        }
    };

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(!selectedMatch.contains(Integer.valueOf(i))&&clickable==true){
            System.out.println("Game Activity " + i);

            Image image = images.get(i);
            ViewGroup gridElement = (ViewGroup) gridView.getChildAt(i);
            EasyFlipView currentView= (EasyFlipView) gridElement.getChildAt(0);
            ImageView currentImage = (ImageView) currentView.getChildAt(0);
            currentImage.setImageBitmap(image.getBitmap());

            currentView.flipTheView();

            if(barray.size()<2){
                Bitmap b = image.getBitmap();
                barray.add(b);
                selectedMatch.add(i);
                seleted_view.add(currentView);
            }

            if(barray.size()==2){
                if(barray.get(0) == barray.get(1)){
                    barray.clear();
                    seleted_view.clear();
                    matched++;
                    numOfAttempts++;
                    matches.setText(matched+"/"+NUM_OF_CARDS+" matches");
                    if(matched==NUM_OF_CARDS){
                        isGameFinished=true;
                        elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                        chronometer.stop();
                        //need to fix
                    }
                }
                else{
                    numOfAttempts++;
                    clickable=false;
                    selectedMatch.remove(selectedMatch.size()-1);
                    selectedMatch.remove(selectedMatch.size()-1);
                    handler.postDelayed(runnable,1000);
                }
            }

            if(isGameFinished == true){
                dialogBox(elapsedMillis,numOfAttempts);
            }
        }
    }


}