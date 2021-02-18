package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class ChallengeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        // Get user details for challenge from previous activity.
        Intent challengeIntent = getIntent();
        Bundle challengeExtras = challengeIntent.getExtras();
        TennisUser user = challengeExtras.getParcelable("user");
        TennisUser opponent = challengeExtras.getParcelable("opponent");

        // Locate elements.
        TextView txtUser = findViewById(R.id.txtChallengeUser);
        TextView txtOpponent = findViewById(R.id.txtChallengeOpponent);
        CalendarView calenderViewChallenge = findViewById(R.id.calenderChallengeDate);
        Spinner spinnerChallengeClub = findViewById(R.id.spinnerChallengeClub);
        Spinner spinnerChallengeTime = findViewById(R.id.spinnerChallengeTime);
        Button btnSubmitChallenge = findViewById(R.id.btnSubmitChallenge);

        // Set elements to correct names.
        txtUser.setText(user.getLname());
        txtOpponent.setText(opponent.getLname());

        // Set clubs to user clubs, if they are from the same club only display once.
        ArrayList<String> clubs = new ArrayList<>();
        String userClub = user.getClubName();
        String opponentClub = opponent.getClubName();
        clubs.add(userClub);
        if (!userClub.equals(opponentClub)) {
            clubs.add(opponentClub);
        }
        ArrayAdapter<String> clubAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, clubs);
        spinnerChallengeClub.setAdapter(clubAdapter);

        // Onclick listener for the button, take all challenge data.
        btnSubmitChallenge.setOnClickListener(v -> {
            int userID = user.getplayerID();
            int opponentID = opponent.getplayerID();
            String location = spinnerChallengeClub.getSelectedItem().toString();
            SimpleDateFormat sdf = new SimpleDateFormat();
            String date = sdf.format(new Date(calenderViewChallenge.getDate()));
            date = date.substring(0, 10);   // truncate so it only holds date and not date+time.
            String time = spinnerChallengeTime.getSelectedItem().toString();

            // Create the challenge.
            createChallenge(location, date, time, userID, opponentID);
        });
    }

    /**
     * Pass information to the challenge table. This stores challenge metadata.
     */
    protected void createChallenge(String location, String date, String time, int userID, int opponentID) {
        // Must cast to string for parameters hashmap, convert back in API code.
        HashMap<String, String> params = new HashMap<>();
        params.put("clubname", String.valueOf(location));
        params.put("date", date);
        params.put("time", time);
        ChallengeRequest createChallenge = new ChallengeRequest(params, userID, opponentID);
        createChallenge.execute();
    }

    private class ChallengeRequest extends AsyncTask<Void, Void, String> {

        private final HashMap<String, String> params;
        private int userID, opponentID;

        public ChallengeRequest(HashMap<String, String> params, int userID, int opponentID) {
            this.params = params;
        }

        /**
         * Now a challenge has been created with metadata, use the returned challenge ID
         * to create a "player_challenge" entry for both players, this is on the post-execute
         * for the challenge request, as it must be done after the challenge is created.
         */
        @Override
        protected void onPostExecute(String s) {
            HashMap<String, String> newParams = new HashMap<>();
            try {
                JSONObject object = new JSONObject(s);
                String challengeID = object.getString("challengeid");
                newParams.put("challengeid", challengeID);
                newParams.put("playerid", String.valueOf(userID));
                newParams.put("didinitiate", "1");
                CreatePlayerChallenge createUserChallenge = new CreatePlayerChallenge(newParams);
                createUserChallenge.execute();

                newParams.clear();
                newParams.put("challengeid", challengeID);
                newParams.put("playerid", String.valueOf(opponentID));
                newParams.put("didinitiate", "0");
                CreatePlayerChallenge createOpponentChallenge = new CreatePlayerChallenge(newParams);
                createOpponentChallenge.execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.sendPostRequest(API_URL.URL_CREATE_CHALLENGE, params);
        }
    }

    private static class CreatePlayerChallenge extends AsyncTask<Void, Void, String> {

        private final HashMap<String, String> params;

        public CreatePlayerChallenge(HashMap<String, String> params) {
            this.params = params;
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.sendPostRequest(API_URL.URL_CREATE_PLAYER_CHALLENGE, params);
        }
    }


}