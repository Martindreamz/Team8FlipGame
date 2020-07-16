package iss.workshop.team8flipgame.adapter;

import android.content.Context;
import android.content.Intent;
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

public class ImageAdapter extends BaseAdapter{

    private  Context mContext;
    private  ArrayList<Image> images;
    public ArrayList<Bitmap> barray = new ArrayList<>();
    ArrayList<View> seleted_view = new ArrayList<>();
    boolean disableFlip;

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
                    if(ImagePickingActivity.selectedCell.contains(Integer.valueOf(image.getPosID())))
                    { ImagePickingActivity.selectedCell.remove(Integer.valueOf(image.getPosID()));}
                    else{ImagePickingActivity.selectedCell.add(image.getPosID());}

                    if (ImagePickingActivity.selectedCell.size()==ImagePickingActivity.gameImageNo){
                        Intent intent = new Intent(mContext, GameActivity.class);
                        intent.putExtra("selectedCells",ImagePickingActivity.selectedCell);
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        if(mContext instanceof GameActivity){

            final Image image = images.get(pos);
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

    public void updateItemList(ArrayList<Image> newItemList) {
        this.images = newItemList;
        notifyDataSetChanged();
    }

}
