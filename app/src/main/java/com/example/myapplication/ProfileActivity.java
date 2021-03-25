package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This activity provides functionality to view another players profile
 * including match history.
 */
public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Fetch bundle data.
        Intent profileIntent = getIntent();
        Bundle profileExtras = profileIntent.getExtras();
        TennisUser user = profileExtras.getParcelable("user");
        TennisUser tappedPlayer = profileExtras.getParcelable("tappedPlayer");

        // Identify page elements.
        TextView txtName = (TextView) findViewById(R.id.txtUserName);
        TextView txtClub = (TextView) findViewById(R.id.txtUserClub);
        Button btnChallenge = findViewById(R.id.btnChallenge);
        Button btnMatchHistory = findViewById(R.id.btnMatchHistory);

        // Set page elements.
        String playerName = tappedPlayer.getFname() + " " + tappedPlayer.getLname();
        txtName.setText(playerName);
        String club = tappedPlayer.getClubName();
        txtClub.setText(club);

        // Listener for the challenge button.
        btnChallenge.setOnClickListener(v -> {
            // Bundle relevant data and navigate to the challenge activity
            Intent intent = new Intent(this, ChallengeActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable("user", user);
            extras.putParcelable("opponent", tappedPlayer);
            intent.putExtras(extras);
            startActivity(intent);
        });

        // Listener for the match history button
        btnMatchHistory.setOnClickListener(v -> {
            // Bundle relevant data and navigate to the match history activity
            Intent intent = new Intent(this, MatchHistoryActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable("user", tappedPlayer);
            intent.putExtras(extras);
            startActivity(intent);
        });
    }
}