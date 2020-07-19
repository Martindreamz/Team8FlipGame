package iss.workshop.team8flipgame.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
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
    //attributes
    private ArrayList<Image> images = new ArrayList<>();
    public static ArrayList<Image> allScrappedImages = new ArrayList<>();
    private ArrayList<Integer> selectedCell = new ArrayList<>();
    private BGMusicService bgMusicService;
    private boolean clickable;
    private boolean IS_MUTED;//Setting of BG Music
    private Button mFetchBtn;
    private EditText urlReader;
    private ImageScraper imageScraper;
    private GridView gridView;
    private ImageAdapter imageAdapter;
    private int gridViewLocation = 0;
    private int cardCount;
    private int MASK_HINT_COLOR = 0x99ffffff;
    private static int noImageToScrape = 20;
    private ProgressBar progressBar;
    private SharedPreferences music_pref;
    private SharedPreferences game_pref;
    private TextView mDownload_progressText;
    private TextView mSelected_imageText;
    private Thread autoStartGame;
    //    public static MutableLiveData<Integer> listen; //No Longer in use but keep this, too powerful for next time

    //onCreate
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picking);

        //variables
        clickable = false;
        game_pref = getSharedPreferences("game_service", MODE_PRIVATE);
        cardCount = (int) game_pref.getInt("cardcount", 0);

        //for top bar
        urlReader = findViewById(R.id.ETurl);
        urlReader.addTextChangedListener(validurl);
        mFetchBtn = findViewById(R.id.BTfetch);
        mFetchBtn.setOnClickListener(this);

        //for gridview
        for (int i = 0; i < noImageToScrape; i++) {
            images.add(new Image(null, i));
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
        music_pref = getSharedPreferences("music_service", MODE_PRIVATE);
        IS_MUTED = music_pref.getBoolean("IS_MUTED", false);
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

    //handlers
    @SuppressLint("HandlerLeak")
    Handler mainHandler = new Handler() {
        public void handleMessage(@NonNull Message msg) {

//            overwriting the imageview with scraped bitmaps
            ViewGroup gridElement = (ViewGroup) gridView.getChildAt(gridViewLocation);
            ImageView currentImage = (ImageView) gridElement.getChildAt(0);
            currentImage.setScaleType(ImageView.ScaleType.FIT_XY);
            currentImage.setImageBitmap(((Image) msg.obj).getBitmap());

            //storing the images to be passed
            allScrappedImages.add(((Image) msg.obj));

            //refreshing the displays
            mDownload_progressText.setText("Downloading " + allScrappedImages.size() + " of " + noImageToScrape + " images...");
            progressBar.setProgress(progressBar.getProgress() + 5);

            //changing progress bar to textview upon download
            if (progressBar.getProgress() == 100) {
                clickable = true;
                progressBar.setVisibility(View.GONE);
                mDownload_progressText.setVisibility(View.GONE);
                mSelected_imageText.setVisibility(View.VISIBLE);
                mSelected_imageText.setText(selectedCell.size() + " out of " + cardCount + " images selected");
            }
            gridViewLocation++;
            if (gridViewLocation == noImageToScrape) {//location starts from 0
                gridViewLocation = 0;

            }
        }
    };

    //onClick
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.BTfetch) {
            clickable = false; //make sure users cant click on the pictures
            mainHandler.removeCallbacksAndMessages(null); //remove all pending msgs in handler
            progressBar.setVisibility(View.VISIBLE);
            mDownload_progressText.setVisibility(View.VISIBLE);
            mDownload_progressText.setText("Downloading " +
                    allScrappedImages.size() + " of " + noImageToScrape + " images...");
            mSelected_imageText.setVisibility(View.GONE);
            reset();//Martin clear the data memory
            clearImages();//Bianca Reset all images to null
            System.out.println("start scrapping");//to ensure scraping starts
            scrapImage();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        cancelToast();


        if (clickable) {
            if (autoStartGame != null && autoStartGame.interrupted()) autoStartGame.interrupt();
            ViewGroup gridElement = (ViewGroup) gridView.getChildAt(i);
            ImageView currentImage = (ImageView) gridElement.getChildAt(0);
            if (currentImage.getColorFilter() == null) {
                currentImage.setColorFilter(MASK_HINT_COLOR, PorterDuff.Mode.SRC_OVER);
            } else {
                currentImage.clearColorFilter();
            }

            if (selectedCell.contains(Integer.valueOf(i))) {
                selectedCell.remove(Integer.valueOf(i));
                mSelected_imageText.setText(selectedCell.size() + " out of " + cardCount + " images selected");
            } else {
                selectedCell.add(i);
                mSelected_imageText.setText(selectedCell.size() + " out of " + cardCount + " images selected");
            }

            if (selectedCell.size() == cardCount) {
                imageScraper.cancel(true);
                Intent intent = new Intent(this, GameActivity.class);
                intent.putExtra("selectedCells", selectedCell);
                startActivity(intent);
            }
        }
        if (selectedCell.size() > cardCount) {
            toastMsg.makeText(this, "U cannot choose more than "
                    + cardCount + " pictures,pls cancel " +(selectedCell.size()-cardCount) + " of your choices.", toastMsg.LENGTH_SHORT).show();
        }
    }


    //life cycles

    //Bianca Lifecycle
    @Override
    public void onPause() {
        super.onPause();
        // pause music
        if (bgMusicService != null) bgMusicService.pause();
        if (autoStartGame != null && !autoStartGame.interrupted()) autoStartGame.interrupt();

    }

    @Override
    public void onResume() {
        //Log.i("gameLife", "TEST");
        super.onResume();
        Log.i("gameLife", "current picked Number: " + allScrappedImages.size());
        if (selectedCell.size() == cardCount) {
            showToast("If you do not change your choice, the game will auto-start after 5 seconds.", Toast.LENGTH_LONG);
            clickable = true;
            autoStartGame = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5000);
                        if (selectedCell.size() == cardCount) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Log.i("gameLife", "AUTO start!");
                                    Intent intent = new Intent(getApplicationContext(), GameActivity.class);
                                    intent.putExtra("selectedCells", selectedCell);
                                    startActivity(intent);
                                }
                            });
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
            autoStartGame.start();
            // restore music
            if (bgMusicService != null) bgMusicService.playMusic("MENU");
            else if (!IS_MUTED) {
                Intent music = new Intent(this, BGMusicService.class);
                bindService(music, this, BIND_AUTO_CREATE);
            }

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (bgMusicService != null)
            unbindService(this);// unbindService
        // end everything
    }


    @Override
    public void onBackPressed() {
        cancelToast();
        super.onBackPressed();
        // if clicking back, kill the AUTO-start-game thread
        if (autoStartGame != null && !autoStartGame.interrupted()) autoStartGame.interrupt();
//        Intent intent = new Intent(this, HomeActivity.class);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//clear game
//        startActivity(intent);
    }

//other functions

    //Bianca Music Service
    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        BGMusicService.LocalBinder musicBinder = (BGMusicService.LocalBinder) binder;
        if (binder != null) {
            bgMusicService = musicBinder.getService();
            bgMusicService.playMusic("MENU");
            Log.i("MusicLog", "BGMusicService Connected, state: play MENU.");
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.i("MusicLog", "BGMusicService DIS-Connected.");

    }


    //gettsers and setters
    public static int noOfImagesRequired() {
        return noImageToScrape;
    }


    void reset() {
        gridViewLocation = 0;
        selectedCell.clear();
        allScrappedImages.clear();
        progressBar.setProgress(0);
    }

    //Bianca Reset all images to null (only show Background)
    void clearImages() {
        for (int i = 0; i < noImageToScrape; i++) {
            ViewGroup gridElement = (ViewGroup) gridView.getChildAt(i);
            ImageView currentImage = (ImageView) gridElement.getChildAt(0);
            currentImage.setImageBitmap(null); //work!
            if (currentImage.getColorFilter() != null) currentImage.setColorFilter(null); //Try
        }
    }

    void scrapImage() {
        if (imageScraper != null) {
            imageScraper.cancel(true);
        }
        imageScraper = new ImageScraper(this);
        imageScraper.execute(urlReader.getText().toString());

    }

    @Override
    public Image getImage(Image image) {
        return null;
    }


    private TextWatcher validurl = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String url = urlReader.getText().toString().trim();

            mFetchBtn.setEnabled(!url.isEmpty() && (url.contains("http://") || url.contains("https://")));
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    @Override
    public void onBitmapReady(Image image) {
        if (gridViewLocation != noOfImagesRequired()) {
            Message msg = new Message();
            msg.obj = image;
            mainHandler.sendMessage(msg);
        }
    }

    //Handling Toast Message together
    // this is used in imageScrapper
    @Override
    public void makeToast(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
            }
        });    }

    // all toast message will be shipped via this attribute so that it only show one piece at one time.
    public Toast toastMsg;

    public void showToast(String text, int toastLength) {
        if (toastMsg == null) {
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
}