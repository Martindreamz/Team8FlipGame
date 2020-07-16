package iss.workshop.team8flipgame.adapter;

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

import iss.workshop.team8flipgame.R;
import iss.workshop.team8flipgame.model.Score;

public class ScoreAdapter extends ArrayAdapter {

    Context context;

    //Test
    List<Score> scores = new ArrayList<>();


    public ScoreAdapter(@NonNull Context context, int resource, List<Score> scores) {
        super(context, resource);
        this.context = context;
        this.scores = scores;

        //text
        Score score = new Score("Daryl", 123);
        scores.add(score);
        Score score1 = new Score("Daryl1", 122434);
        scores.add(score1);

        for(int i=0; i<scores.size(); i++){
            add(null);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.leaderboard_row, null);

        TextView name = convertView.findViewById(R.id.nameTxt);
        name.setText(scores.get(position).getName());

        TextView score = convertView.findViewById(R.id.scoreTxt);
        score.setText(scores.get(position).getScore());

        return convertView;
    }


}
