package iss.workshop.team8flipgame.adapter;

import android.content.Context;
import android.content.Intent;
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
                    if(ImagePickingActivity.selectedCell.contains(Integer.valueOf(image.getPosID())))
                    { ImagePickingActivity.selectedCell.remove(Integer.valueOf(image.getPosID()));}
                    else{ImagePickingActivity.selectedCell.add(image.getPosID());}

                    if (ImagePickingActivity.selectedCell.size()==ImagePickingActivity.gameImageNo){
                        Intent intent = new Intent(mContext, GameActivity.class);
                        intent.putExtra("selectedCells",ImagePickingActivity.selectedCell);
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

}
