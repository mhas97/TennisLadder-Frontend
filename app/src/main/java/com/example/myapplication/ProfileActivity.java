package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private ImageView imgProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent profileIntent = getIntent();
        Bundle profileExtras = profileIntent.getExtras();
        TennisUser user = profileExtras.getParcelable("user");
        TennisUser tappedPlayer = profileExtras.getParcelable("tappedPlayer");

        TextView txtName = (TextView) findViewById(R.id.txtUserName);
        TextView txtClub = (TextView) findViewById(R.id.txtUserClub);
        Button btnChallenge = findViewById(R.id.btnChallenge);
        Button btnMatchHistory = findViewById(R.id.btnMatchHistory);
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

        btnMatchHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, MatchHistoryActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable("user", tappedPlayer);
            intent.putExtras(extras);
            startActivity(intent);
        });
    }
}