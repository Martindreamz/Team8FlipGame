package iss.workshop.team8flipgame.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.List;

import iss.workshop.team8flipgame.activity.GameActivity;
import iss.workshop.team8flipgame.R;
import iss.workshop.team8flipgame.activity.ImagePickingActivity;
import iss.workshop.team8flipgame.model.Image;
import iss.workshop.team8flipgame.model.Score;
import iss.workshop.team8flipgame.service.DBService;

public class ImageAdapter extends BaseAdapter{

    private  Context mContext;
    private  ArrayList<Image> images;
    public ArrayList<Bitmap> barray = new ArrayList<>();
    ArrayList<View> seleted_view = new ArrayList<>();
    boolean disableFlip;

    private static final int NUM_OF_CARDS = 6;
    int numOfAttempts = 0;
    int totalTime = 0;

    public ImageAdapter(Context mContext, ArrayList<Image> images){
        this.mContext = mContext;
        this.images=images;
    }

    @Override
    public int getCount(){
        return images.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int pos, View view, ViewGroup viewGroup) {
        if(mContext instanceof ImagePickingActivity){
            final Image image = images.get(pos);
            image.setPosID(pos);
            final int position = pos;
            if (view == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                view = layoutInflater.inflate(R.layout.images, null);
            }
            final ImageView imageView1 = (ImageView)view.findViewById(R.id.image);
            imageView1.setImageBitmap(image.getBitmap());

            imageView1.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {

                    System.out.println("Image picking: " + image.getPosID());
                    System.out.println(image.getPosID());
                    if (imageView.getBackground() == null) {
                        Drawable highlight = mContext.getResources().getDrawable(R.drawable.background_border);
                        imageView.setBackground(highlight);
                    }
                    else{
                        imageView.setBackground(null);
                    }
                    if(ImagePickingActivity.selectedCell.contains(Integer.valueOf(image.getPosID())))
                    { ImagePickingActivity.selectedCell.remove(Integer.valueOf(image.getPosID()));}
                    else{ImagePickingActivity.selectedCell.add(image.getPosID());}

                    if (ImagePickingActivity.selectedCell.size()==ImagePickingActivity.gameImageNo){
                        Intent intent = new Intent(mContext, GameActivity.class);
                        intent.putExtra("selectedCells",ImagePickingActivity.selectedCell);
                        intent.putExtra("IS_MUTED",ImagePickingActivity.IS_MUTED);
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        if(mContext instanceof GameActivity){

            final Image image = images.get(pos);
            image.setPosID(pos);
            final int position = pos;
            image.setPosID(position);

            if (view == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                view = layoutInflater.inflate(R.layout.images, null);
            }

            final ImageView imageView2 = (ImageView)view.findViewById(R.id.image);
            imageView2.setImageBitmap(image.getBitmap());

            imageView2.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)

                @Override
                public void onClick(View view) {
                    System.out.println("Game Activity " + image.getPosID());

                    if(barray.size()<2){
                        Bitmap b = image.getBitmap();
                        imageView2.setImageBitmap(image.getBitmap());
                        barray.add(b);
                        seleted_view.add(view);}
                    if(barray.size()==2){
                        if(barray.get(0) == barray.get(1)){
                            System.out.println("same");
                            barray.clear();
                            view.setClickable(false);
                            seleted_view.get(0).setClickable(false);
                        }
                        else{
                            System.out.println("not same");
                            barray.clear();
                            seleted_view.clear();
                        }
                    }

                }

            });
        }
        return view;
    }

    public int calculateScore(int totalTime,int numOfAttempts){
        return (5 * NUM_OF_CARDS) + (500 / numOfAttempts) + (5000 / totalTime);
    }

    public void finishedGame(int totalTime,int numOfAttempts){
        int totalScore = calculateScore(60,15);
        Score scoreObj = new Score("Theingi",totalScore);
        DBService db = new DBService(mContext);
        db.addScore(scoreObj);
    }

    public void updateItemList(ArrayList<Image> newItemList) {
        this.images = newItemList;
        notifyDataSetChanged();
    }

}
