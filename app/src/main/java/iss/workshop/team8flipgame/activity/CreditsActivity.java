package iss.workshop.team8flipgame.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import iss.workshop.team8flipgame.R;

public class CreditsActivity extends AppCompatActivity
                            implements View.OnClickListener {
    Button contact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        contact = findViewById(R.id.emailContact);
        if (contact!=null)
            contact.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.emailContact) {
            Uri uri = Uri.parse("mailto:gdipsa50t8@gmail.com");
            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Contact Team");
            intent.putExtra(Intent.EXTRA_TEXT, "");
            if (intent.resolveActivity(getPackageManager()) != null) startActivity(intent);
        }

        }


}