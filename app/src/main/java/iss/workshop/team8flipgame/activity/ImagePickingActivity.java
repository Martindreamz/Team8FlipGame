package iss.workshop.team8flipgame.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import iss.workshop.team8flipgame.service.BGMusicService;
import iss.workshop.team8flipgame.model.*;
import iss.workshop.team8flipgame.adapter.ImageAdapter;
import iss.workshop.team8flipgame.ImageScraper;
import iss.workshop.team8flipgame.R;

public class ImagePickingActivity extends AppCompatActivity
        implements View.OnClickListener, ImageScraper.ICallback,
        ServiceConnection,AdapterView.OnItemClickListener {
    ArrayList<Image> images = new ArrayList<>();
    Button mFetchBtn;
    EditText urlReader;
    ImageScraper imageScraper;
    static int imageNo =20;
    GridView gridView;
    ImageAdapter imageAdapter;
    int childPos=0;
    public static ArrayList<Integer> selectedCell = new ArrayList<>();
    public static ArrayList<Image> selectedImage = new ArrayList<>();
    public static int gameImageNo;
    ProgressBar progressBar;
    TextView mDownload_progressText;
    TextView mSelected_imageText;
//    public static MutableLiveData<Integer> listen; //No Longer in use but keep this, too powerful for next time
    BGMusicService bgMusicService;
    SharedPreferences sharedPref;
    boolean IS_MUTED;//Setting of BG Music
    private static int MASK_HINT_COLOR = 0x99ffffff;
    Boolean clickable;
    SharedPreferences global_pref;
    Thread autoStartGame;


    @SuppressLint("HandlerLeak")
    Handler mainHandler = new Handler(){
        public void handleMessage(@NonNull Message msg){
            ViewGroup gridElement = (ViewGroup) gridView.getChildAt(childPos);
            ImageView currentImage= (ImageView) gridElement.getChildAt(0);
            currentImage.setScaleType(ImageView.ScaleType.FIT_XY);
            currentImage.setImageBitmap(((Image) msg.obj).getBitmap());
            selectedImage.add(((Image)msg.obj));
            mDownload_progressText.setText("Downloading "+selectedImage.size()+" of " + imageNo+" images...");
            progressBar.setProgress(progressBar.getProgress() + 5);

            if(progressBar.getProgress()==100) {
                clickable=true;
                progressBar.setVisibility(View.GONE);
                mDownload_progressText.setVisibility(View.GONE);
                mSelected_imageText.setVisibility(View.VISIBLE);
                mSelected_imageText.setText(selectedCell.size()+" out of "+gameImageNo+" images selected");
            }
            childPos++;
            if(childPos==getImageNo()){
                childPos = 0;
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picking);

       //variables
        clickable = false;
        global_pref = getSharedPreferences("game_service",MODE_PRIVATE);
        gameImageNo = (int) global_pref.getInt("cardcount",0);
        System.out.println(gameImageNo);

        //for top bar
        urlReader = findViewById(R.id.ETurl);
        urlReader.addTextChangedListener(validurl);
        mFetchBtn = findViewById(R.id.BTfetch);
        mFetchBtn.setOnClickListener(this);

        //for gridview
        for(int i = 0;i<imageNo;i++){
            images.add(new Image(null,i));
        }
        gridView = findViewById(R.id.gridView);
        imageAdapter = new ImageAdapter(this, images);
        gridView.setAdapter(imageAdapter);
        gridView.setVerticalScrollBarEnabled(false);
        gridView.setOnItemClickListener(this);

        //for bottom bar
        progressBar = findViewById(R.id.download_progress);
        progressBar.setMax(100);
        progressBar.setMin(0);
        mDownload_progressText = findViewById(R.id.download_textview);
        mSelected_imageText = findViewById(R.id.selected_image);

        //Bianca Music Service
        sharedPref = getSharedPreferences("music_service",MODE_PRIVATE);
        IS_MUTED = sharedPref.getBoolean("IS_MUTED",false);
        if (!IS_MUTED) {
            Intent music = new Intent(this, BGMusicService.class);
            bindService(music, this, BIND_AUTO_CREATE);
        }

        reset();

//This is no longer in use but keep this, too useful for next time, Martin
//        listen = new MutableLiveData<>();//
//        listen.setValue(selectedCell.size()); //Initilize with a value//
//        listen.observe(this, new Observer<Integer>() {
//            @Override
//            public void onChanged(Integer integer) {
//                mSelected_imageText.setText(selectedCell.size()+" out of "+gameImageNo+" images selected");
//            }
//        });
    }

    //gettsers and setters
    public static int getImageNo() {
        return imageNo;
    }

    public static void setImageNo(int imageNo2) {
        imageNo = imageNo2;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.BTfetch){
            clickable = false;
            mainHandler.removeCallbacksAndMessages(null);
            progressBar.setVisibility(View.VISIBLE);
            mDownload_progressText.setVisibility(View.VISIBLE);
            mSelected_imageText.setVisibility(View.GONE);
            reset();//Martin clear the data memory
            clearImages();//Bianca Reset all images to null
            mDownload_progressText.setText("Downloading "+selectedImage.size()+" of " + imageNo+" images...");
            System.out.println("start scrapping");
            scrapImage();
        }
    }

    void reset(){
        childPos = 0;
        selectedCell.clear();
        selectedImage.clear();
        progressBar.setProgress(0);
    }

    //Bianca Reset all images to null (only show Background)
    void clearImages() {
        for (int i = 0; i < imageNo; i++) {
            ViewGroup gridElement = (ViewGroup) gridView.getChildAt(i);
            ImageView currentImage = (ImageView) gridElement.getChildAt(0);
            currentImage.setImageBitmap(null); //work!
            if (currentImage.getColorFilter() != null) currentImage.setColorFilter(null); //Try
        }
    }

    void scrapImage(){
        if(imageScraper != null){
            imageScraper.cancel(true);
        }
        imageScraper = new ImageScraper(this);
        imageScraper.execute(urlReader.getText().toString());

    }
    @Override
    public Image getImage(Image image) {
        return null;
    }

    @Override
    public void onBitmapReady(Image image) {
        if(childPos!=getImageNo()){
            Message msg = new Message();
            msg.obj=image;
            mainHandler.sendMessage(msg);}
    }

    //Handling Toast Message together
    // this is used in imageScrapper
    @Override
    public void makeToast(String message) {
        showToast(message,Toast.LENGTH_SHORT);
    }

    // all toast message will be shipped via this attribute so that it only show one piece at one time.
    public Toast toastMsg;
    public void showToast(String text,int toastLength) {
        if(toastMsg == null) {
            toastMsg = Toast.makeText(this, text, toastLength);
        } else {
            toastMsg.setText(text);
            toastMsg.setDuration(toastLength);
        }
        toastMsg.show();
    }
    public void cancelToast() {
        if (toastMsg != null) {
            toastMsg.cancel();
        }
    }


    //Bianca Music Service
    //@Override
    public void onServiceConnected(ComponentName name, IBinder binder){
        BGMusicService.LocalBinder musicBinder = (BGMusicService.LocalBinder) binder;
        if(binder != null) {
            bgMusicService = musicBinder.getService();
            bgMusicService.playMusic("MENU");
            Log.i("MusicLog", "BGMusicService Connected, state: play MENU.");
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
        // pause music
        if(bgMusicService!=null) bgMusicService.pause();
        if (autoStartGame != null && !autoStartGame.interrupted())  autoStartGame.interrupt();

    }

    @Override
    public void onResume(){
        //Log.i("gameLife", "TEST");
        super.onResume();
        Log.i("gameLife", "current picked Number: " + selectedImage.size());
        if (selectedCell.size()==gameImageNo){
            showToast("If you do not change your choice, the game will auto-start after 5 seconds.",Toast.LENGTH_LONG);
            //Toast.makeText(this,,Toast.LENGTH_LONG).show();
            autoStartGame = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        if (selectedCell.size()==gameImageNo) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("gameLife", "AUTO start!");
                                    Intent intent = new Intent(getApplicationContext(),GameActivity.class);
                                    intent.putExtra("selectedCells",selectedCell);
                                    startActivity(intent);
                                }
                            });
                        }
                    }
                    catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            autoStartGame.start();
        // restore music
        if(bgMusicService!=null) bgMusicService.playMusic("MENU");
        else if(!IS_MUTED) {
            Intent music = new Intent(this, BGMusicService.class);
            bindService(music, this, BIND_AUTO_CREATE);
        }

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
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        if(clickable){
            if (autoStartGame != null && !autoStartGame.interrupted())  autoStartGame.interrupt();
            ViewGroup gridElement = (ViewGroup) gridView.getChildAt(i);
            ImageView currentImage= (ImageView) gridElement.getChildAt(0);
            if(currentImage.getColorFilter() == null){
                currentImage.setColorFilter(MASK_HINT_COLOR, PorterDuff.Mode.SRC_OVER);
            }
            else {
                currentImage.clearColorFilter();
            }

            if(selectedCell.contains(Integer.valueOf(i)))
            {
                selectedCell.remove(Integer.valueOf(i));
                mSelected_imageText.setText(selectedCell.size()+" out of "+gameImageNo+" images selected");
            }
            else{
                selectedCell.add(i);
                mSelected_imageText.setText(selectedCell.size()+" out of "+gameImageNo+" images selected");
            }
            if (selectedCell.size()>gameImageNo){
                showToast("U cannot choose more than "
                        +gameImageNo + "pictures,pls cancel one of your choices.",Toast.LENGTH_SHORT);
            }
            else if (selectedCell.size()==gameImageNo){
                imageScraper.cancel(true);
                Intent intent = new Intent(this, GameActivity.class);
                intent.putExtra("selectedCells",selectedCell);
                startActivity(intent);
            }
        }
    }
    @Override
    public void onBackPressed() {
        cancelToast();
        super.onBackPressed();
        // if clicking back, kill the AUTO-start-game thread
        if (autoStartGame != null && !autoStartGame.interrupted())  autoStartGame.interrupt();
        Intent intent = new Intent(this,HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear game
        startActivity(intent);
    }

    private TextWatcher validurl = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String url = urlReader.getText().toString().trim();

            mFetchBtn.setEnabled(!url.isEmpty() && (url.contains("http://")||url.contains("https://")));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };
}