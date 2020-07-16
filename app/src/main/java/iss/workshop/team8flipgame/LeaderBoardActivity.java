package iss.workshop.team8flipgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

import iss.workshop.team8flipgame.model.Score;
import iss.workshop.team8flipgame.repo.DBService;

public class LeaderBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        //retrieve list of scores from db
        DBService db = new DBService(this);
        List<Score> scores = db.getAllScore();
        //instantiate adapter
        ScoreAdapter adapter = new ScoreAdapter(this, R.layout.leaderboard_row, scores);
        ListView listView = findViewById(R.id.listView);
        if(listView != null){
            listView.setAdapter(adapter);
        }
    }
}