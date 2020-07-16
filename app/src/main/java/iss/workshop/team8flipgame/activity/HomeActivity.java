package iss.workshop.team8flipgame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import iss.workshop.team8flipgame.R;

public class HomeActivity extends AppCompatActivity
        implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.incoming);

        Button play = findViewById(R.id.play);
        if (play != null) {
            play.setOnClickListener(this);

        }
        Button leader = findViewById(R.id.leaderBoard);
        if (leader != null) {
            leader.setOnClickListener(this);

        }
        Button credits = findViewById(R.id.credits);
        if (credits != null) {
            credits.setOnClickListener(this);

        }
        ImageButton toggle = findViewById(R.id.soundToggle);
        if (toggle != null) {
            toggle.setOnClickListener(this);

        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.play) {
            Intent intent = new Intent(this, ImagePickingActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.leaderBoard) {
            Intent intent = new Intent(this, LeaderBoardActivity.class);
            startActivity(intent);
        }
//        else if (id == R.id.credits) {
//            Intent intent = new Intent(this,CreditsActivity.class);
//            startActivity(intent);
//        }
    }
}