package iss.workshop.team8flipgame.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Build;
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
import android.widget.Toast;

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

    //attributes
    private AlertDialog alertDialog;
    private ArrayList<Bitmap> bitmapArray = new ArrayList<>();
    private ArrayList<Image> images;
    private ArrayList<EasyFlipView> selectedCards = new ArrayList<>();
    private ArrayList<Integer> selectedMatch;
    private BGMusicService bgMusicService;
    private Boolean clickable = true;
    private boolean isGameFinished = false;
    private Boolean IS_MUTED = false ; //Setting of BG Music
    private Chronometer chronometer;
    final Context context = this;
    private EditText nameId;
    private GridView gridView;
    private final Handler handler = new Handler();
    private int cardMatched;
    private int cardCount;
    private int numOfAttempts = 0;
    private int totalScore=0;
    private long elapsedMillis;
    private SharedPreferences game_pref;
    private SharedPreferences music_pref;
    private static final String CHANNEL_ID = "888888";
    private static final String CHANNEL_NAME = "Message Notification Channel";
    private static final String CHANNEL_DESCRIPTION = "This channel is for displaying messages";
    private TextView matches;
    private TextView txtScore;
    private Thread autoKillGame;
    private View dialogView;

    //onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        //variables
        images = new ArrayList<>();
        selectedMatch = new ArrayList<>();
        Intent intent = getIntent();
        ArrayList<Integer> selectedCell = intent.getIntegerArrayListExtra("selectedCells");
        game_pref = getSharedPreferences("game_service",MODE_PRIVATE);
        cardCount = (int) game_pref.getInt("cardcount",0);
        System.out.println(cardCount);


        //top bar
        chronometer = findViewById(R.id.chronometer);
        matches = findViewById(R.id.matches);
        matches.setText(cardMatched +"/"+ cardCount +" matches");

        //Grid view
        for(int i : selectedCell){
            images .add (ImagePickingActivity.allScrappedImages.get(i));
            images .add (ImagePickingActivity.allScrappedImages.get(i));
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

        music_pref = getSharedPreferences("game_service",MODE_PRIVATE);
    }

    //onClick
    @Override
    public void onClick(View view){
        int id = view.getId();
        if(id == R.id.btnOK){
            nameId = dialogView.findViewById(R.id.name);
            String playerName = nameId.getText().toString();
            finishedGame(playerName,totalScore);
            alertDialog.dismiss();
            //activity-stack(LIFO)  splash->home->picking->game
            Intent intent = new Intent(this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear picking&game
            startActivity(intent);
        }
    }

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

            if(bitmapArray.size()<2){
                Bitmap b = image.getBitmap();
                bitmapArray.add(b);
                selectedMatch.add(i);
                selectedCards.add(currentView);
            }

            if(bitmapArray.size()==2){
                if(bitmapArray.get(0) == bitmapArray.get(1)){
                    bitmapArray.clear();
                    selectedCards.clear();
                    cardMatched++;
                    numOfAttempts++;
                    matches.setText(cardMatched +"/"+ cardCount +" matches");
                    if(cardMatched == cardCount){
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



    //life cycles
    @Override
    public void onBackPressed() {
        Toast.makeText(this,"This turn is over, pick image again.",Toast.LENGTH_LONG).show();
        super.onBackPressed();
        isGameFinished=true;
        if(bgMusicService!=null) {
            bgMusicService.mute();
            //unbindService(this);
        }
        if (autoKillGame != null && !autoKillGame.interrupted())  autoKillGame.interrupt();
        finish();//dunt reset the images still show chosen stage
    }

    @Override
    public void finish() {
        if(!isGameFinished && bgMusicService!=null) {
            unbindService(this);
        }
        super.finish();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isGameFinished)
        {
            chronometer.setBase(SystemClock.elapsedRealtime() - elapsedMillis);
            chronometer.start();
        }
    }


    @Override
    public void onResume(){
        if (!isGameFinished)
        {
            chronometer.setBase(SystemClock.elapsedRealtime() - elapsedMillis);
            chronometer.start();
            Toast.makeText(getApplicationContext(),
                    "Start Timing. GO!",Toast.LENGTH_SHORT).show();
        }
        super.onResume();
        // restore game
    }

    @Override
    public void onPause(){ // This happens when finishing game and quit the game just before onStop()
        super.onPause();
        if (!isGameFinished) {
            // send notification : Want to continue or end the current Game
            Toast.makeText(getApplicationContext(),
                    "You left the game, please come back in 5 seconds to continue!", Toast.LENGTH_LONG).show();
            createNotificationChannel();
            createNotification(1);
            //this thread pending interrupted?

            autoKillGame = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        if (!getLifecycle().getCurrentState().name().equals("RESUMED")){
                            //&& !getLifecycle().getCurrentState().name().equals("STARTED")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("gameLife", "After 5 seconds, this turn is over! State as "+
                                            getLifecycle().getCurrentState().name());
                                    if (bgMusicService!=null) bgMusicService.pause();
                                    Toast.makeText(getApplicationContext(),
                                            "After 5 seconds, this turn is over!", Toast.LENGTH_SHORT).show();
                                    finish();
                                    createNotification(2);
                                    //onDestroy();//cannot destroy since fragments have been destriyed.
                                }
                            });
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            autoKillGame.start();
        }

    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //unbind service before go to on-destroy
    }

    @Override
    public void onStop() { // This happens when finishing game and quit the game (activity invisible)
        super.onStop();
        elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
    }

    //other functions
    private void createNotificationChannel() {
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel( CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            NotificationManager notificationManager =
                    getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
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

    public int calculateScore(long totalTime,int numOfAttempts){
        return (int) ((5 * cardCount) + (500 / numOfAttempts) + (5000 / (totalTime/1000)));
    }

    public void finishedGame(String name ,int totalScore){
        Score scoreObj = new Score(name,totalScore, music_pref.getString("difficulty", "Easy"));
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

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            selectedCards.get(1).flipTheView();
            selectedCards.get(0).flipTheView();
            bitmapArray.clear();
            selectedCards.clear();
            clickable=true;
        }
    };

    private void createNotification(int times) {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this, CHANNEL_ID);
        if (times == 1 ) {
            builder.setSmallIcon(R.drawable.games)
                    .setContentTitle("Oops!")
                    .setContentText("You just left the game, please come back in 5 seconds to continue!" +
                            "Otherwise you will lose this turn and need to restart with a new game.")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setAutoCancel(true);
        }
        if (times == 2 ) {
            builder.setSmallIcon(R.drawable.games)
                    .setContentTitle("Come On!")
                    .setContentText("Current game has been stopped already." +
                            "Wanna try again?")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)
                    .setTimeoutAfter(60000);
        }

        Notification notification = builder.build();
        int notificationId = 99999;
        NotificationManagerCompat mgr = NotificationManagerCompat.from(this);
        mgr.notify(notificationId, notification);
    }
}