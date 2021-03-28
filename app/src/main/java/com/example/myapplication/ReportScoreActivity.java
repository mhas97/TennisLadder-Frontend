package com.example.myapplication;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Provides functionality to report a score. This includes network requests to
 * communicate with the API, as well as Elo adjustment and achievement checks.
 */
public class ReportScoreActivity extends AppCompatActivity {

    private Bundle challengeExtras;
    private HashMap<String, String> achievementUpdater;
    EditText txtSet1User;
    EditText txtSet2User;
    EditText txtSet3User;
    EditText txtSet1Opponent;
    EditText txtSet2Opponent;
    EditText txtSet3Opponent;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_score);

        /* Obtain bundle extras. */
        Intent challengeIntent = getIntent();
        challengeExtras = challengeIntent.getExtras();
        TennisUser user = challengeExtras.getParcelable("user");
        TennisChallenge challenge = challengeExtras.getParcelable("challenge");

        /* Identify page elements. */
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

        /* Get challenge related info. */
        int challengeID = challenge.getChallengeID();
        TennisUser opponent = challenge.getOpponent();
        String userName = user.getFname() + " " + user.getLname();
        String opponentName = opponent.getFname() + " " + opponent.getLname();

        /* Populate the winner selection spinner with participants. */
        ArrayList<String> players = new ArrayList<>();
        players.add(userName);
        players.add(opponentName);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, players);
        spinnerWinner.setAdapter(arrayAdapter);

        /* Display challenge info. */
        txtTitleUser.setText(userName);
        txtTitleOpponent.setText(opponentName);
        txtUserLname.setText(user.getLname());
        txtOpponentLname.setText(opponent.getLname());

        /* On-click listener for the submit button, submits a result. */
        btnSubmit.setOnClickListener(v -> {

            /* Identify the winner. */
            String winner = spinnerWinner.getSelectedItem().toString(); // Identify the winner as selected by the spinner.

            /* Build a string to represent the score. */
            String score = buildScore();

            /* Variables to calculate database changes. */
            int winnerID;
            int winnerElo;
            int loserID;
            int loserElo;
            int hotstreak;              // Check if the winner is now on a hotstreak.
            int currentHighestElo;      // Check if the winner has achieved an Elo PB.
            boolean userWon;            // Check if the reporting player won.

            /* Determine the winner */
            if (winner.equals(userName)) {
                /* User is the winner. */
                userWon = true;
                winnerID = user.getPlayerID();
                winnerElo = user.getElo();
                loserID = opponent.getPlayerID();
                loserElo = opponent.getElo();
                hotstreak = (user.getWinstreak() + 1 >= 3) ? 1 : 0;     // 3+ wins in a row is a "hotstreak".
                currentHighestElo = user.getHighestElo();
            }
            else {
                /* Opponent is the winner. */
                userWon = false;
                winnerID = opponent.getPlayerID();
                winnerElo = opponent.getElo();
                loserID = user.getPlayerID();
                loserElo = user.getElo();
                hotstreak = (opponent.getWinstreak() + 1 >= 3) ? 1 : 0;
                currentHighestElo = opponent.getHighestElo();
            }

            /* Calculate adjusted player ratings and check if the winners elo is a personal best. */
            int adjustedWinnerElo = getAdjustedRating(winnerElo, loserElo, true);
            int adjustedLoserElo = getAdjustedRating(loserElo, winnerElo, false);
            int newHighestElo = Math.max(adjustedWinnerElo, currentHighestElo);

            /* Check for achievement unlocks. */
            AchievementHandler achievementHandler = new AchievementHandler(user, opponent, score, adjustedWinnerElo, userWon, this);
            achievementHandler.checkForUnlocks();

            /* Post the result. */
            postResult(challengeID, winnerID, loserID, score, adjustedWinnerElo, adjustedLoserElo, newHighestElo, hotstreak);
        });
    }

    /**
     * Adjust the elo for the given result. Note this is a simple Elo implementation, where no
     * consolation" is offered, score is also not taken into account. The probability of a victory
     * is calculated and used to adjust the users Elo with respect to the actual result. Formula
     * used from the following article:
     * https://medium.com/purple-theory/what-is-elo-rating-c4eb7a9061e0#:~:text=The%20Elo%20rating%20system%20is,physics%20professor%20born%20in%201903.
     */
    private int getAdjustedRating(int toAdjust, int opponentElo, boolean winner) {
        /* Calculate the Elo difference and convert to double for math.pow(). */
        double eloDiff = opponentElo - toAdjust;

        /* Calculate the probability of victory for given user. */
        double expectedScore = 1 / (1 + Math.pow(10, eloDiff/400));

        /* The factor to adjust by, standard is 16. I have used 32 as the result volume is likely
        low and inflation not an issue. A larger reward should also help further incentive to play. */
        double adjustFactor = 32;

        double result = (winner) ? 1 : 0;   // The actual result.

        /* Calculate the adjusted Elo. */
        double adjustedScore = Math.round(adjustFactor * (result - expectedScore));
        return (int)(toAdjust + adjustedScore);
    }

    /**
     * Format the user entered score so it can be stored in the database
     * @return Formatted score.
     */
    private String buildScore() {
        StringBuilder scoreString = new StringBuilder();
        scoreString.append(txtSet1User.getText().toString());
        scoreString.append("/");
        scoreString.append(txtSet1Opponent.getText().toString());
        scoreString.append(" ");
        scoreString.append(txtSet2User.getText().toString());
        scoreString.append("/");
        scoreString.append(txtSet2Opponent.getText().toString());
        /* Only append a 3rd set score if it was played. */
        if (txtSet3User.getText().toString().length() > 0) {
            scoreString.append(" ");
            scoreString.append(txtSet3User.getText().toString());
            scoreString.append("/");
            scoreString.append(txtSet3Opponent.getText().toString());
        }
        return scoreString.toString();
    }

    /**
     * Create the parameter hashmap and post the result to the database,
     * including the new ratings as adjusted by the Elo algorithm.
     */
    private void postResult(int challengeID, int winnerID, int loserID, String score, int winnerElo, int loserElo, int newHighestElo, int hotstreak) {
        /* Create parameter hashmap */
        HashMap<String, String> params = new HashMap<>();
        params.put("challengeid", String.valueOf(challengeID));
        params.put("winnerid", String.valueOf(winnerID));
        params.put("loserid", String.valueOf(loserID));
        params.put("score", score);
        params.put("winnerelo", String.valueOf(winnerElo));
        params.put("loserelo", String.valueOf(loserElo));
        params.put("newhighestelo", String.valueOf(newHighestElo));
        params.put("hotstreak", String.valueOf(hotstreak));

        /* Execute API request. */
        PostResult req = new PostResult(params);
        req.execute();
    }

    /**
     * An asynchronous task to communicate with the API to update challenge results.
     */
    private class PostResult extends AsyncTask<Void, Void, String> {

        private HashMap<String, String> params;

        public PostResult(HashMap<String, String> params) {
            this.params = params;
        }

        /**
         * Parse the response and navigate to the challenges page upon success.
         */
        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject object = new JSONObject(s);
                /* Display the error status via a toast. */
                String message = object.getString("message");
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                if (object.getString("error").equals("false")) {
                    /* Navigate to the challenges fragment. */
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtras(challengeExtras);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * Creates a APIRequest object to handle HTTP communication with the API.
         * @return Error status.
         */
        @Override
        protected String doInBackground(Void... voids) {
            APIRequest req = new APIRequest();
            return req.executePostRequest(API_URL.URL_POST_RESULT, params);
        }
    }
}