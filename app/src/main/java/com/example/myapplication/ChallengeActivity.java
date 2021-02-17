package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ChallengeActivity extends AppCompatActivity {

    TextView txtUser, txtOpponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        Intent ladderIntent = getIntent();
        Bundle ladderExtras = ladderIntent.getExtras();
        TennisUser user = ladderExtras.getParcelable("user");
        String opponent = ladderExtras.getString("opponent");

        txtUser = findViewById(R.id.txtUser);
        txtOpponent = findViewById(R.id.txtOpponent);

        txtUser.setText(user.getLname());
        txtOpponent.setText(opponent);
    }
}