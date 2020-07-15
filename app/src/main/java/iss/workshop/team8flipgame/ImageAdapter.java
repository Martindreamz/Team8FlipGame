package iss.workshop.team8flipgame;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

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
            @Override
            public void onClick(View view) {
                System.out.println(image.getPosID());
            }
        });
        return view;
    }

    public void updateItemList(ArrayList<Image> newItemList) {
        this.images = newItemList;
        notifyDataSetChanged();
    }

}
