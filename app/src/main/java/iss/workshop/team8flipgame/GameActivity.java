package iss.workshop.team8flipgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.GridView;

import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class GameActivity extends AppCompatActivity {
    ArrayList<Image> images;
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

    }
}