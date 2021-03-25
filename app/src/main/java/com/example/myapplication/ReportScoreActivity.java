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

/**
 * Provides functionality to report a score. This includes
 * network requests to communicate with the API, as well as
 * Elo adjustment and achievement checks.
 */
public class ReportScoreActivity extends AppCompatActivity {

    private Bundle challengeExtras;
    EditText txtSet1User;
    EditText txtSet2User;
    EditText txtSet3User;
    EditText txtSet1Opponent;
    EditText txtSet2Opponent;
    EditText txtSet3Opponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_score);

        // Obtain bundle extras.
        Intent challengeIntent = getIntent();
        challengeExtras = challengeIntent.getExtras();
        TennisUser user = challengeExtras.getParcelable("user");
        TennisChallenge challenge = challengeExtras.getParcelable("challenge");

        // Identify page elements.
        TextView txtTitleUser = findViewById(R.id.txtUserReport);
        TextView txtTitleOpponent = findViewById(R.id.txtOpponentReport);
        Spinner spinnerWinner = findViewById(R.id.spinner_winner);
        TextView txtUserLname = findViewById(R.id.txtUserLastName);
        TextView txtOpponentLname = findViewById(R.id.txtOpponentLastName);
        txtSet1User = findViewById(R.id.txtSet1User);
        txtSet2User = findViewById(R.id.txtSet2User);
        txtSet3User = findViewById(R.id.txtSet3User);
        txtSet1Opponent = findViewById(R.id.txtSet1Opponent);
        txtSet2Opponent = findViewById(R.id.txtSet2Opponent);
        txtSet3Opponent = findViewById(R.id.txtSet3Opponent);
        Button btnSubmit = findViewById(R.id.btnConfirmScore);

        // Get challenge related info.
        int challengeID = challenge.getChallengeID();
        TennisUser opponent = challenge.getOpponent();
        String userName = user.getFname() + " " + user.getLname();
        String opponentName = opponent.getFname() + " " + opponent.getLname();

        // Populate the winner selection spinner with participants.
        ArrayList<String> players = new ArrayList<>();
        players.add(userName);
        players.add(opponentName);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, players);
        spinnerWinner.setAdapter(arrayAdapter);

        // Display challenge info.
        txtTitleUser.setText(userName);
        txtTitleOpponent.setText(opponentName);
        txtUserLname.setText(user.getLname());
        txtOpponentLname.setText(opponent.getLname());

        btnSubmit.setOnClickListener(v -> {
            // Identify the winner as selected by the spinner.
            String winner = spinnerWinner.getSelectedItem().toString();

            // Achievement related variables.
            int winnerID;
            int winnerElo;
            int loserID;
            int loserElo;
            int hotstreak;
            int currentHighestElo;

            // Determine the winner.
            if (winner.equals(userName)) {
                winnerID = user.getplayerID();
                winnerElo = user.getElo();
                loserID = challenge.getOpponent().getplayerID();
                loserElo = challenge.getOpponent().getElo();
                // Check if the user is on a hotstreak.
                hotstreak = (user.getWinstreak() + 1 == 3) ? 1 : 0; // 3 wins in a row is a "hotstreak".
                currentHighestElo = user.getHighestElo();
            }
            else {
                winnerID = opponent.getplayerID();
                winnerElo = opponent.getElo();
                loserID = user.getplayerID();
                loserElo = user.getElo();
                hotstreak = (opponent.getWinstreak() + 1 == 3) ? 1 : 0;
                currentHighestElo = opponent.getHighestElo();
            }

            // Adjust the player ratings relative to the result, check if the winners elo is a personal best.
            int adjustedWinnerElo = getAdjustedRating(winnerElo, loserElo, true);
            int adjustedLoserElo = getAdjustedRating(loserElo, winnerElo, false);
            int newHighestElo = Math.max(adjustedWinnerElo, currentHighestElo);

            // Build a string to represent the score.
            String score = buildScore();

            // Post the result.
            postResult(challengeID, winnerID, loserID, score, adjustedWinnerElo, adjustedLoserElo, newHighestElo, hotstreak);
        });
    }

    /**
     * Adjust the elo for the given result. Note this is a simple Elo implementation,
     * where no "consolation" is offered, score is also not taken into account. The
     * probability of a victory is calculated and used to adjust the users Elo with
     * respect to the actual result. Formula used from the following article:
     * https://medium.com/purple-theory/what-is-elo-rating-c4eb7a9061e0#:~:text=The%20Elo%20rating%20system%20is,physics%20professor%20born%20in%201903.
     */
    protected int getAdjustedRating(int toAdjust, int opponentElo, boolean winner) {
        // Calculate the Elo difference and convert to double for math.pow().
        double eloDiff = opponentElo - toAdjust;

        // Calculate the probability of a victory.
        double expectedScore = 1 / (1 + Math.pow(10, eloDiff/400));

        // The factor to adjust by, standard is 16. I have used 32 as the result
        //volume is low and inflation likely not an issue. A larger reward should
        //also provide incentive to play
        double adjustFactor = 32;

        // The actual result.
        double result = (winner) ? 1 : 0;

        // Calculate the adjusted score.
        double adjustedScore = Math.round(adjustFactor * (result - expectedScore));
        return (int)(toAdjust + adjustedScore);
    }

    protected String buildScore() {
        StringBuilder scoreString = new StringBuilder();
        scoreString.append(txtSet1User.getText().toString());
        scoreString.append("/");
        scoreString.append(txtSet1Opponent.getText().toString());
        scoreString.append(" ");
        scoreString.append(txtSet2User.getText().toString());
        scoreString.append("/");
        scoreString.append(txtSet2Opponent.getText().toString());
        // Only append a 3rd set score if it was played.
        if (txtSet3User.getText().toString().length() > 0) {
            scoreString.append(" ");
            scoreString.append(txtSet3User.getText().toString());
            scoreString.append("/");
            scoreString.append(txtSet3Opponent.getText().toString());
        }
        return scoreString.toString();
    }

    /**
     * Post the result to the database including the new ratings as adjusted by the Elo algorithm.
     */
    protected void postResult(int challengeID, int winnerID, int loserID, String score, int winnerElo, int loserElo, int newHighestElo, int hotstreak) {
        HashMap<String, String> params = new HashMap<>();
        params.put("challengeid", String.valueOf(challengeID));
        params.put("winnerid", String.valueOf(winnerID));
        params.put("loserid", String.valueOf(loserID));
        params.put("score", score);
        params.put("winnerelo", String.valueOf(winnerElo));
        params.put("loserelo", String.valueOf(loserElo));
        params.put("newhighestelo", String.valueOf(newHighestElo));
        params.put("hotstreak", String.valueOf(hotstreak));
        PostResult req = new PostResult(params);
        req.execute();
    }

    /**
     * An asynchronous task to communicate with the API to update
     * challenge results.
     */
    private class PostResult extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> params;

        public PostResult(HashMap<String, String> params) {
            this.params = params;
        }

        /**
         * Navigate to the challenges page upon completion.
         */
        @Override
        protected void onPostExecute(String s) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.putExtras(challengeExtras);
            startActivity(intent);
        }

        /**
         * API Request.
         * @return Error status.
         */
        @Override
        protected String doInBackground(Void... voids) {
            APIRequest req = new APIRequest();
            return req.executePostRequest(API_URL.URL_POST_RESULT, params);
        }
    }
}