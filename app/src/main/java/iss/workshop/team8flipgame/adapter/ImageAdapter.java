package iss.workshop.team8flipgame.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

import iss.workshop.team8flipgame.activity.GameActivity;
import iss.workshop.team8flipgame.R;
import iss.workshop.team8flipgame.activity.ImagePickingActivity;
import iss.workshop.team8flipgame.model.Image;

public class ImageAdapter extends BaseAdapter{
    private  ArrayList<Image> images;
    public ArrayList<Bitmap> barray = new ArrayList<>();
    private ArrayList<ImageView> seleted_view = new ArrayList<>();
    private  Context mContext;

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
            if (view == null) {
                final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                view = layoutInflater.inflate(R.layout.images, null);
            }
            final ImageView imageView1 = view.findViewById(R.id.image);
            imageView1.setImageBitmap(image.getBitmap());
        }

        if(mContext instanceof GameActivity){

            final Image image = images.get(pos);
            image.setPosID(pos);

            switch (images.size()){
                case 12:{
                    if (view == null) {
                        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                        view = layoutInflater.inflate(R.layout.card_mode_easy, null);
                    }
                    break;}
                case 20:{
                    if (view == null) {
                        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                        view = layoutInflater.inflate(R.layout.card_mode_normal, null);
                    }
                    break;}
                case 28:{
                    if (view == null) {
                        final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
                        view = layoutInflater.inflate(R.layout.card_mode_hard, null);
                    }
                    break;}
            }
        }
        return view;
    }

    public void updateItemList(ArrayList<Image> newItemList) {
        this.images = newItemList;
        notifyDataSetChanged();
    }

}
