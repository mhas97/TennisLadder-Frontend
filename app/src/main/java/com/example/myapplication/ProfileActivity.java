package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;
    private TextView txtName, txtClub;
    private Button btnChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent profileIntent = getIntent();
        Bundle profileExtras = profileIntent.getExtras();
        TennisUser user = profileExtras.getParcelable("user");
        TennisUser tappedPlayer = profileExtras.getParcelable("tappedPlayer");

        txtName = (TextView) findViewById(R.id.txtPlayerName);
        txtClub = (TextView) findViewById(R.id.txtPlayerClub);
        btnChallenge = findViewById(R.id.btnChallenge);
        String name = tappedPlayer.getFname() + " " + tappedPlayer.getLname();
        txtName.setText(name);
        String club = tappedPlayer.getClubName();
        txtClub.setText(club);
        btnChallenge.setOnClickListener(v -> {
            Intent intent = new Intent(this, ChallengeActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable("user", user);
            extras.putParcelable("opponent", tappedPlayer);
            intent.putExtras(extras);
            startActivity(intent);
        });
    }
}