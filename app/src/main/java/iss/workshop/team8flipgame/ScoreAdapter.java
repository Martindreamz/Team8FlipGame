package iss.workshop.team8flipgame;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScoreAdapter extends ArrayAdapter {

    Context context;

    //Test
    List<String> names = new ArrayList<>();
    List<Integer> scores = new ArrayList<>();


    public ScoreAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;

        names.add("Daryl");
        scores.add(123);

        names.add("Daryl1");
        scores.add(345);

        for(int i=0; i<names.size(); i++){
            add(null);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.leaderboard_row, null);

        TextView name = convertView.findViewById(R.id.nameTxt);
        name.setText(names.get(position));

        TextView score = convertView.findViewById(R.id.scoreTxt);
        score.setText(scores.get(position));

        return convertView;
    }


}
