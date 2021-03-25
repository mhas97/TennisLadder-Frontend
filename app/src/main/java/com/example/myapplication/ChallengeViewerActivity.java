package com.example.myapplication;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provides functionality to view a challenge. First identifies the
 * nature of the challenge and displays the corresponding view. The
 * user can respond to challenge via the APIRequest class executed
 * on button presses.
 */
public class ChallengeViewerActivity extends AppCompatActivity {

    Bundle challengeExtras;
    TennisChallenge challenge;

    /**
     * Identify elements and display the correct view for a given challenge.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain bundled data.
        Intent challengeIntent = getIntent();
        challengeExtras = challengeIntent.getExtras();
        challenge = challengeExtras.getParcelable("challenge");

        // Set up the page for an accepted challenge.
        if (challenge.getAccepted() == 1) {
            setUpAcceptedChallenge();
        }

        // Set up the page for an initiated challenge.
        else if (challenge.getDidInitiate() == 0) {
            setUpInitiatedChallenge();
        }

        // Set up the page for an incoming challenge.
        else {
            setUpIncomingChallenge();
        }

        // Identify page elements
        TextView txtOpponent = findViewById(R.id.txtOpponent);
        TextView txtOpponentElo = findViewById(R.id.txtOpponentElo);
        TextView txtDate = findViewById(R.id.txtDate);
        TextView txtTime = findViewById(R.id.txtTime);
        TextView txtLocation = findViewById(R.id.txtLocation);

        // Set page elements.
        String opponentName = challenge.getOpponent().getFname() + " " + challenge.getOpponent().getLname();
        String opponentElo = String.valueOf(challenge.getOpponent().getElo());
        txtOpponent.setText(opponentName);
        txtOpponentElo.setText(opponentElo + " Elo");
        txtDate.setText(challenge.getDate());
        txtTime.setText(challenge.getTime());
        txtLocation.setText(challenge.getLocation());
    }

    /**
     * Display the layout for an accepted challenge with its options.
     */
    protected void setUpAcceptedChallenge() {
        // Display the accepted challenge layout.
        setContentView(R.layout.activity_challenge_accepted);

        // Identify unique page elements.
        Button btnReportScore = findViewById(R.id.btnReportScore);
        Button btnCancel = findViewById(R.id.btnCancelAccepted);

        // Listener for the score report button.
        btnReportScore.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReportScoreActivity.class);
            intent.putExtras(challengeExtras);
            // Navigate to the report score activity, send the bundled challenge data.
            startActivity(intent);
        });

        btnCancel.setOnClickListener(v -> {
            setUpCancelButton();
        });
    }

    /**
     * Display the layout for an initiated challenge with its options.
     */
    protected void setUpInitiatedChallenge() {
        // Display the initiated challenge layout.
        setContentView(R.layout.activity_challenge_incoming);

        // Identify unique page elements.
        Button btnAccept = findViewById(R.id.btnAccept);
        Button btnDecline = findViewById(R.id.btnDecline);

        btnAccept.setOnClickListener(v -> {
            setUpAcceptButton();
        });

        btnDecline.setOnClickListener(v -> {
            setUpDeclineButton();
        });
    }

    /**
     * Display the layout for an incoming challenge with its options.
     */
    protected void setUpIncomingChallenge() {
        // Display the incoming challenge layout.
        setContentView(R.layout.activity_challenge_outgoing);

        // Identify unique page elements.
        Button btnCancel = findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(v -> {
            setUpCancelButton();
        });
    }

    /**
     * Setup the accept button for an activity including confirmation dialogue.
     * If the user confirms, accept the challenge via an API request.
     */
    protected void setUpAcceptButton() {
        // Create confirmation alert.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Accept challenge");
        builder.setMessage("Are you sure you want to accept this challenge?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Accept the challenge.
            acceptChallenge(challenge.getChallengeID());
        });
        builder.setNegativeButton("No", (dialog, which) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Setup the decline button for the activity. If the user declines a challenge,
     * it can be cancelled as these operations have the same results. Upon confirmation
     * make an API request.
     */
    protected void setUpDeclineButton() {
        // Create confirmation alert
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Decline challenge");
        builder.setMessage("Are you sure you want to decline this challenge?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            // Cancel the challenge.
            cancelChallenge(challenge.getChallengeID(), false);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Setup the cancel button for the activity (shared for accepted and
     * outgoing challenges). If the user confirms, cancel the challenge
     * via an API request.
     */
    protected void setUpCancelButton() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Cancel challenge");
        builder.setMessage("Are you sure you want to cancel this challenge?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            cancelChallenge(challenge.getChallengeID(), true);
        });
        builder.setNegativeButton("No", (dialog, which) -> {
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Create an AcceptRequest object to execute asynchronously.
    protected void acceptChallenge(int challengeID) {
        AcceptRequest req = new AcceptRequest(challengeID);
        req.execute();
    }

    // Create a CancelRequest object to execute asynchronously.
    protected void cancelChallenge(int challengeID, boolean cancelReq) {
        CancelRequest req = new CancelRequest(challengeID, cancelReq);
        req.execute();
    }

    /**
     * An asynchronous task to accept a challenge.
     */
    private class AcceptRequest extends AsyncTask<Void, Void, String> {

        private final int challengeID;

        public AcceptRequest(int challengeID) {
            this.challengeID = challengeID;
        }

        /**
         * Handle status and navigate to the challenges fragment upon success.
         */
        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject object = new JSONObject(s);
                String message = object.getString("message");
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                if (object.getString("error").equals("false")) {
                    // Navigate back to the challenges fragment.
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtras(challengeExtras);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * API request
         * @return Error status.
         */
        @Override
        protected String doInBackground(Void... voids) {
            APIRequest req = new APIRequest();
            return req.executeGetRequest(API_URL.URL_ACCEPT_CHALLENGE + challengeID);
        }
    }

    /**
     * An asynchronous task to cancel a challenge.
     */
    private class CancelRequest extends AsyncTask<Void, Void, String> {

        private final int challengeID;
        // Cancel and decline share functionality, as such use cancelReq to handle
        // status messages.
        private final boolean cancelReq;

        public CancelRequest(int challengeID, boolean cancelReq) {
            this.challengeID = challengeID;
            this.cancelReq = cancelReq;
        }

        /**
         * Handle status and navigate to the challenges fragment upon success.
         */
        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject object = new JSONObject(s);
                String message = "Challenge ";
                // Determine if it was a decline or cancellation.
                message += cancelReq ? "cancelled" : "declined";
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                if (object.getString("error").equals("false")) {
                    // Navigate back to the challenges fragment.
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtras(challengeExtras);
                    startActivity(intent);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * API request
         * @return Error status.
         */
        @Override
        protected String doInBackground(Void... voids) {
            APIRequest req = new APIRequest();
            return req.executeGetRequest(API_URL.URL_CANCEL_CHALLENGE + challengeID);
        }
    }
}