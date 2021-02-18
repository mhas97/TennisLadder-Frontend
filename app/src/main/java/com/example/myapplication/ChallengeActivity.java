package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

public class ChallengeActivity extends AppCompatActivity {

    private TextView txtUser, txtOpponent;
    private Spinner spinnerClubChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        Intent challengeIntent = getIntent();
        Bundle challengeExtras = challengeIntent.getExtras();
        TennisUser user = challengeExtras.getParcelable("user");
        TennisUser opponent = challengeExtras.getParcelable("opponent");

        txtUser = findViewById(R.id.txtChallengeUser);
        txtOpponent = findViewById(R.id.txtChallengeOpponent);
        spinnerClubChallenge = findViewById(R.id.spinnerClubChallenge);

        txtUser.setText(user.getLname());
        txtOpponent.setText(opponent.getLname());

        ArrayList<String> clubs = new ArrayList<>();
        String userClub = user.getClubName();
        String opponentClub = opponent.getClubName();
        clubs.add(userClub);
        if (!userClub.equals(opponentClub)) {
            clubs.add(opponentClub);
        }
        ArrayAdapter<String> clubAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, clubs);
        spinnerClubChallenge.setAdapter(clubAdapter);
    }
}