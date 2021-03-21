package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ChallengeActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private long date_seconds;
    private String time;
    private Button btnTimePicker;

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
        CalendarView calendarViewChallenge = findViewById(R.id.calendarChallengeDate);
        Spinner spinnerChallengeClub = findViewById(R.id.spinnerChallengeClub);
        btnTimePicker = findViewById(R.id.btnTimePicker);
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

        date_seconds = calendarViewChallenge.getDate() / 1000;

        calendarViewChallenge.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            date_seconds = calendar.getTimeInMillis() / 1000;
        });

        btnTimePicker.setOnClickListener(v -> {
            DialogFragment tpf = new TimePickerFragment();
            tpf.show(getSupportFragmentManager(), "time picker");
        });

        // Onclick listener for the button, take all challenge data.
        btnSubmitChallenge.setOnClickListener(v -> {
            int userID = user.getplayerID();
            int opponentID = opponent.getplayerID();
            String location = spinnerChallengeClub.getSelectedItem().toString();
            // In the end it was more elegant to send the API UNIX time and convert there for db storage.
            // SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String date = String.valueOf(date_seconds);
            // Create the challenge.
            createChallenge(location, date, time, userID, opponentID);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtras(challengeExtras);
            startActivity(intent);
        });
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        time = hourOfDay + ":" + minute;
        btnTimePicker.setText(time);
    }

    /**
     * Pass information to the challenge table. This stores challenge metadata.
     */
    protected void createChallenge(String location, String date, String time, int userID, int opponentID) {
        // Must cast to string for parameters hashmap, convert back in API code.
        HashMap<String, String> params = new HashMap<>();
        params.put("clubname", location);
        params.put("date", date);
        params.put("time", time);
        ChallengeRequest createChallenge = new ChallengeRequest(params, userID, opponentID);
        createChallenge.execute();
    }

    private class ChallengeRequest extends AsyncTask<Void, Void, String> {

        private final HashMap<String, String> params;
        private final int userID;
        private final int opponentID;

        public ChallengeRequest(HashMap<String, String> params, int userID, int opponentID) {
            this.params = params;
            this.userID = userID;
            this.opponentID = opponentID;
        }

        /**
         * Now a challenge has been created with metadata, use the returned challenge ID
         * to create a "player_challenge" entry for both players, this is on the post-execute
         * for the challenge request, as it must be done after the challenge is created.
         */
        @Override
        protected void onPostExecute(String s) {
            HashMap<String, String> userParams = new HashMap<>();
            HashMap<String, String> opponentParams = new HashMap<>();
            try {
                JSONObject object = new JSONObject(s);
                String challengeID = object.getString("challengeid");

                userParams.put("challengeid", challengeID);
                userParams.put("playerid", String.valueOf(userID));
                userParams.put("didinitiate", "1");
                CreatePlayerChallenge createUserChallenge = new CreatePlayerChallenge(userParams);
                createUserChallenge.execute();

                opponentParams.put("challengeid", challengeID);
                opponentParams.put("playerid", String.valueOf(opponentID));
                opponentParams.put("didinitiate", "0");
                CreatePlayerChallenge createOpponentChallenge = new CreatePlayerChallenge(opponentParams);
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