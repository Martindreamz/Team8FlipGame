package iss.workshop.team8flipgame.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

import iss.workshop.team8flipgame.service.BGMusicService;
import iss.workshop.team8flipgame.model.*;
import iss.workshop.team8flipgame.adapter.ImageAdapter;
import iss.workshop.team8flipgame.ImageScraper;
import iss.workshop.team8flipgame.R;

public class ImagePickingActivity extends AppCompatActivity
        implements View.OnClickListener, ImageScraper.ICallback, ServiceConnection {

    ArrayList<Image> images = new ArrayList<>();
    Button fetch;
    EditText urlReader;
    String url;
    ImageScraper imageScraper;
    static int imageNo =20;
    GridView gridView;
    ImageAdapter imageAdapter;
    int childPos=0;
    public static ArrayList<Integer> selectedCell = new ArrayList<>();
    public static ArrayList<Image> selectedImage = new ArrayList<>();
    public static int gameImageNo = 6;

    BGMusicService bgMusicService;
    Boolean IS_MUTED = false ; //Setting of BG Music

    @SuppressLint("HandlerLeak")
    Handler mainHandler = new Handler(){
        public void handleMessage(@NonNull Message msg){
            System.out.println("Msg:"+msg.obj);
            ViewGroup gridElement = (ViewGroup) gridView.getChildAt(childPos);
            ImageView currentImage= (ImageView) gridElement.getChildAt(0);
            currentImage.setScaleType(ImageView.ScaleType.FIT_XY);
            currentImage.setImageBitmap(((Image) msg.obj).getBitmap());
            selectedImage.add(((Image)msg.obj));
            childPos++;
            System.out.println(childPos);
            if(childPos==getImageNo()){
                childPos = 0;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_picking);

        fetch = findViewById(R.id.BTfetch);
        urlReader = findViewById(R.id.ETurl);

        fetch.setOnClickListener(this);

        for(int i = 0;i<imageNo;i++){
            images.add(new Image(null,i));
        }
        gridView = (GridView)findViewById(R.id.gridView);
        ImageAdapter imageAdapter = new ImageAdapter(this, images);
        gridView.setAdapter(imageAdapter);
        gridView.setVerticalScrollBarEnabled(false);

        //Bianca Music Service
        Intent music = new Intent(this, BGMusicService.class);
        bindService(music, this, BIND_AUTO_CREATE);

    }

    public static int getImageNo() {
        return imageNo;
    }

    public static void setImageNo(int imageNo2) {
        imageNo = imageNo2;
    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        System.out.println(id);
        if(id == R.id.BTfetch){
            selectedCell.clear();
            selectedImage.clear();
            System.out.println("start scrapping");
            scrapImage();


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
            System.out.println(childPos);

            mainHandler.sendMessage(msg);}
    }

    @Override
    public void makeToast(String message) {

    }

    //Bianca Music Service
    //@Override
    public void onServiceConnected(ComponentName name, IBinder binder){
        BGMusicService.LocalBinder musicBinder = (BGMusicService.LocalBinder) binder;
        if(binder != null && !IS_MUTED) {
            bgMusicService = musicBinder.getService();
            bgMusicService.playMusic("MENU");
            Log.i("MusicLog", "BGMusicService Connected, state: play MENU.");
        }
    }
    @Override
    public void onServiceDisconnected(ComponentName name){
        Log.i("MusicLog", "BGMusicService DIS-Connected.");

    }
}