package iss.workshop.team8flipgame.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import iss.workshop.team8flipgame.activity.GameActivity;
import iss.workshop.team8flipgame.R;
import iss.workshop.team8flipgame.activity.ImagePickingActivity;
import iss.workshop.team8flipgame.model.Image;

public class ImageAdapter extends BaseAdapter{

    private  Context mContext;
    private  ArrayList<Image> images;
    ImageView currentImage;

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
            final ImageView imageView = (ImageView)view.findViewById(R.id.image);
            imageView.setImageBitmap(image.getBitmap());

            imageView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {

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
            });}

        if(mContext instanceof GameActivity){

            final Image image = images.get(pos);
            image.setPosID(pos);
            final int position = pos;
            if (view == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                view = layoutInflater.inflate(R.layout.images, null);
            }

            final ImageView imageView = (ImageView)view.findViewById(R.id.image);
            imageView.setImageBitmap(image.getBitmap());

            imageView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View view) {

                    System.out.println(image.getPosID());
                }

            });
        }
        return view;
    }

    public void updateItemList(ArrayList<Image> newItemList) {
        this.images = newItemList;
        notifyDataSetChanged();
    }

    public void selectedImageView(ImageView imageView){
        imageView.setImageResource(R.drawable.background_border);
        currentImage = imageView;
    }

    //https://blog.csdn.net/double_sweet1/java/article/details/84787917
    /*
    private void changeImageView(ImageView v,int id) {
        if (v.getDrawable() instanceof BitmapDrawable) {
            Bitmap bitmap1 = v.get;
            Bitmap bitmap2 = ((BitmapDrawable) getResources().getDrawable(
                    R.drawable.go)).getBitmap();
            v.setImageBitmap(image.getBitmap());
            Drawable[] array = new Drawable[2];
            array[0] = new BitmapDrawable(bitmap1);
            array[1] = new BitmapDrawable(bitmap2);
            LayerDrawable la = new LayerDrawable(array);
            la.setLayerInset(0, 0, 0, 0, 0);
            la.setLayerInset(1, 20, 20, 20, 20);
            image.setImageDrawable(la);
        }
        else if (v.getDrawable() instanceof LayerDrawable) {
            //Resources res = this.getResources();
            Drawable drawable = v.getDrawable();
            v.setImageDrawable(drawable);
        }
    }*/



}
