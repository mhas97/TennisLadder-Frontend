package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;

public class ReportScoreActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_score);

        // Obtain bundle extras.
        Intent challengeIntent = getIntent();
        Bundle challengeExtras = challengeIntent.getExtras();
        TennisUser user = challengeExtras.getParcelable("user");
        TennisChallenge challenge = challengeExtras.getParcelable("challenge");

        // Identify page elements.
        TextView titleUser = findViewById(R.id.txt_user_report_score);
        TextView titleOpponent = findViewById(R.id.txt_opponent_report_score);
        Spinner spinnerWinner = findViewById(R.id.spinner_winner);
        TextView lastNameUser = findViewById(R.id.txt_user_last_name);
        TextView lastNameOpponent = findViewById(R.id.txt_opponent_last_name);
        EditText set1Score1 = findViewById(R.id.txt_set1_score1);
        EditText set2Score1 = findViewById(R.id.txt_set2_score1);
        EditText set3Score1 = findViewById(R.id.txt_set3_score1);
        EditText set1Score2 = findViewById(R.id.txt_set1_score2);
        EditText set2Score2 = findViewById(R.id.txt_set2_score2);
        EditText set3Score2 = findViewById(R.id.txt_set3_score2);
        Button submitScore = findViewById(R.id.btn_confirm_score);

        // Get challenge related info.
        int challengeID = challenge.getChallengeID();
        String userName = user.getFname() + " " + user.getLname();
        String opponentName = challenge.getOpponent().getFname() + " " + challenge.getOpponent().getLname();

        // Populate the winner selection spinner with participants.
        ArrayList<String> players = new ArrayList<>();
        players.add(userName);
        players.add(opponentName);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, players);
        spinnerWinner.setAdapter(arrayAdapter);

        // Display challenge info.
        titleUser.setText(userName);
        titleOpponent.setText(opponentName);
        lastNameUser.setText(user.getLname());
        lastNameOpponent.setText(challenge.getOpponent().getLname());

        submitScore.setOnClickListener(v -> {
            // Identify the winner as selected by the spinner.
            int winnerID;
            int loserID;
            int winnerElo;
            int loserElo;
            String winner = spinnerWinner.getSelectedItem().toString();
            if (winner == userName) {
                winnerID = user.getplayerID();
                loserID = challenge.getOpponent().getplayerID();
                winnerElo = user.getElo();
                loserElo = challenge.getOpponent().getElo();
            }
            else {
                winnerID = challenge.getOpponent().getplayerID();
                loserID = user.getplayerID();
                winnerElo = challenge.getOpponent().getElo();
                loserElo = user.getElo();
            }
            // Adjust the player ratings relative to the result.
            winnerElo = getAdjustedRating(winnerElo, loserElo, true);
            loserElo = getAdjustedRating(loserElo, winnerElo, false);

            // Build a string to represent the score.
            StringBuilder scoreString = new StringBuilder();
            scoreString.append(set1Score1.getText().toString());
            scoreString.append("/");
            scoreString.append(set1Score2.getText().toString());
            scoreString.append(" ");
            scoreString.append(set2Score1.getText().toString());
            scoreString.append("/");
            scoreString.append(set2Score2.getText().toString());
            // Only append a 3rd set score if it was played.
            if (set3Score1.getText().toString().length() > 0) {
                scoreString.append(" ");
                scoreString.append(set3Score1.getText().toString());
                scoreString.append("/");
                scoreString.append(set3Score2.getText().toString());
            }
            String score = scoreString.toString();
            postResult(challengeID, winnerID, loserID, score, winnerElo, loserElo);
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtras(challengeExtras);
            startActivity(intent);
        });
    }

    protected int getAdjustedRating(int toAdjust, int opponentElo, boolean winner) {
        double expectedScore = 1/(1+Math.pow(10, (opponentElo-toAdjust)/400));
        double adjustFactor = 32;
        double result;
        result = (winner == true) ? 1 : 0;
        double adjustedScore = adjustFactor*(result-expectedScore);
        return toAdjust+(int)(Math.round(adjustedScore));
    }

    // Post the result to the database including the new ratings as adjusted by the Elo algorithm.
    protected void postResult(int challengeID, int winnerID, int loserID, String score, int winnerElo, int loserElo) {
        HashMap<String, String> params = new HashMap<>();
        params.put("challengeid", String.valueOf(challengeID));
        params.put("winnerid", String.valueOf(winnerID));
        params.put("loserid", String.valueOf(loserID));
        params.put("score", score);
        params.put("winnerelo", String.valueOf(winnerElo));
        params.put("loserelo", String.valueOf(loserElo));
        PostResult postResult = new PostResult(params);
        postResult.execute();
    }

    private class PostResult extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> params;

        public PostResult(HashMap<String, String> params) {
            this.params = params;
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler req = new RequestHandler();
            return req.sendPostRequest(API_URL.URL_POST_RESULT, params);
        }
    }
}