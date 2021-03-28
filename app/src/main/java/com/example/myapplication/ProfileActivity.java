package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This activity provides functionality to view another players profile including their match history
 * and trophy cabinet.
 */
public class ProfileActivity extends AppCompatActivity {

    private ImageView imgTrophy1, imgTrophy2, imgTrophy3, imgTrophy4, imgTrophy5, imgTrophy6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /* Fetch bundle data. */
        Intent profileIntent = getIntent();
        Bundle profileExtras = profileIntent.getExtras();
        TennisUser user = profileExtras.getParcelable("user");
        TennisUser tappedPlayer = profileExtras.getParcelable("tappedPlayer");

        /* Identify page elements. */
        TextView txtName = findViewById(R.id.txtUserName);
        TextView txtClub = findViewById(R.id.txtUserClub);
        Button btnChallenge = findViewById(R.id.btnChallenge);
        Button btnMatchHistory = findViewById(R.id.btnMatchHistory);

        /* Holders for potential achievement unlocks. */
        imgTrophy1 = findViewById(R.id.imgTrophy1);
        imgTrophy2 = findViewById(R.id.imgTrophy2);
        imgTrophy3 = findViewById(R.id.imgTrophy3);
        imgTrophy4 = findViewById(R.id.imgTrophy4);
        imgTrophy5 = findViewById(R.id.imgTrophy5);
        imgTrophy6 = findViewById(R.id.imgTrophy6);
        setUpTrophyCabinet(tappedPlayer);

        /* Set page elements. */
        String playerName = tappedPlayer.getFname() + " " + tappedPlayer.getLname();
        txtName.setText(playerName);
        String club = tappedPlayer.getClubName();
        txtClub.setText(club);

        btnChallenge.setOnClickListener(v -> {
            /* Bundle the relevant data and navigate to the challenge activity. */
            Intent intent = new Intent(this, ChallengeActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable("user", user);
            extras.putParcelable("opponent", tappedPlayer);
            intent.putExtras(extras);
            startActivity(intent);
        });

        btnMatchHistory.setOnClickListener(v -> {
            /* Bundle relevant data and navigate to the match history activity. */
            Intent intent = new Intent(this, MatchHistoryActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable("user", tappedPlayer);
            intent.putExtras(extras);
            startActivity(intent);
        });
    }

    /**
     * Create trophy cabinet helper to format the trophy cabinet.
     */
    private void setUpTrophyCabinet(TennisUser user) {
        /* Achievement holders. */
        ImageView[] trophies = new ImageView[] {
                imgTrophy1, imgTrophy2, imgTrophy3, imgTrophy4, imgTrophy5, imgTrophy6
        };
        TrophyCabinetHelper helper = new TrophyCabinetHelper(trophies, user, this);
        helper.ArrangeTrophyCabinet();
    }
}