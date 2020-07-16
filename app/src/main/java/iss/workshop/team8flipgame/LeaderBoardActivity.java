package iss.workshop.team8flipgame;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;

public class LeaderBoardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);

        ScoreAdapter adapter = new ScoreAdapter(this, R.layout.leaderboard_row);
        ListView listView = findViewById(R.id.listView);
        if(listView != null){
            listView.setAdapter(adapter);
        }
    }
}