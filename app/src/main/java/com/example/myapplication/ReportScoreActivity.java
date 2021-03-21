package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ReportScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_score);

        Intent challengeIntent = getIntent();
        Bundle challengeExtras = challengeIntent.getExtras();
        TennisUser user = challengeExtras.getParcelable("user");
        TennisChallenge challenge = challengeExtras.getParcelable("challenge");

        TextView titleUser = findViewById(R.id.txt_user_report_score);
        TextView titleOpponent = findViewById(R.id.txt_opponent_report_score);
        Spinner spinnerWinner = findViewById(R.id.spinner_winner);
        TextView lastNameUser = findViewById(R.id.txt_user_last_name);
        TextView lastNameOpponent = findViewById(R.id.txt_opponent_last_name);

        String userName = user.getFname() + " " + user.getLname();
        String opponentName = challenge.getOpponent().getFname() + " " + challenge.getOpponent().getLname();
        titleUser.setText(userName);
        titleOpponent.setText(opponentName);
        ArrayList<String> players = new ArrayList<>();
        players.add(userName);
        players.add(opponentName);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, players);
        spinnerWinner.setAdapter(arrayAdapter);
        lastNameUser.setText(user.getLname());
        lastNameOpponent.setText(challenge.getOpponent().getLname());
    }
}