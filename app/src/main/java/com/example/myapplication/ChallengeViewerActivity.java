package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class ChallengeViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent challengeIntent = getIntent();
        Bundle challengeExtras = challengeIntent.getExtras();
        TennisChallenge challenge = challengeExtras.getParcelable("challenge");

        if (challenge.getAccepted() == 1) {
            setContentView(R.layout.activity_challenge_accepted);
            Button reportScore = findViewById(R.id.btn_report_score);
            Button cancel = findViewById(R.id.btn_cancel_accepted);

            reportScore.setOnClickListener(v -> {
                Intent intent = new Intent(this, ReportScoreActivity.class);
                intent.putExtras(challengeExtras);
                startActivity(intent);
            });

            cancel.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("Cancel challenge");
                builder.setMessage("Are you sure you want to cancel this challenge?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    cancelChallenge(challenge.getChallengeID());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtras(challengeExtras);
                    startActivity(intent);
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            });
        }
        else if (challenge.getDidInitiate() == 0) {
            setContentView(R.layout.activity_challenge_incoming);
            Button accept = findViewById(R.id.btn_accept);
            Button decline = findViewById(R.id.btn_decline);
            accept.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("Accept challenge");
                builder.setMessage("Are you sure you want to accept this challenge?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    acceptChallenge(challenge.getChallengeID());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtras(challengeExtras);
                    startActivity(intent);
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            });

            decline.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("Decline challenge");
                builder.setMessage("Are you sure you want to decline this challenge?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    cancelChallenge(challenge.getChallengeID());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtras(challengeExtras);
                    startActivity(intent);
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            });

        }
        else {
            setContentView(R.layout.activity_challenge_outgoing);
            Button cancel = findViewById(R.id.btn_cancel_outgoing);
            cancel.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);
                builder.setTitle("Cancel challenge");
                builder.setMessage("Are you sure you want to cancel this challenge?");
                builder.setPositiveButton("Yes", (dialog, which) -> {
                    cancelChallenge(challenge.getChallengeID());
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.putExtras(challengeExtras);
                    startActivity(intent);
                });
                builder.setNegativeButton("No", (dialog, which) -> {
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            });
        }

        TextView opponent = findViewById(R.id.txt_opponent);
        TextView date = findViewById(R.id.txt_date);
        TextView time = findViewById(R.id.txt_time);
        TextView location = findViewById(R.id.txt_location);

        String opponentName = challenge.getOpponent().getFname() + " " + challenge.getOpponent().getLname();
        opponent.setText(opponentName);
        date.setText(challenge.getDate());
        time.setText(challenge.getTime());
        location.setText(challenge.getLocation());
    }

    protected void cancelChallenge(int challengeID) {
        CancelChallenge cancelChallenge = new CancelChallenge(challengeID);
        cancelChallenge.execute();
    }

    protected void acceptChallenge(int challengeID) {
        AcceptChallenge acceptChallenge = new AcceptChallenge(challengeID);
        acceptChallenge.execute();
    }

    private static class CancelChallenge extends AsyncTask<Void, Void, String> {

        private final int challengeID;

        public CancelChallenge(int challengeID) {
            this.challengeID = challengeID;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler req = new RequestHandler();
            return req.sendGetRequest(API_URL.URL_CANCEL_CHALLENGE + challengeID);
        }
    }

    private static class AcceptChallenge extends AsyncTask<Void, Void, String> {

        private final int challengeID;

        public AcceptChallenge(int challengeID) {
            this.challengeID = challengeID;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler req = new RequestHandler();
            return req.sendGetRequest(API_URL.URL_ACCEPT_CHALLENGE + challengeID);
        }
    }
}