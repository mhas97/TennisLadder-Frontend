package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class ChallengeActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private Bundle challengeExtras;
    private Spinner spinnerClub;
    private Button btnTimePicker;
    private long date_seconds;
    private String time;
    private TennisUser user, opponent;

    /**
     * This class handles challenge creation, including formatting user input
     * and handling network requests to communicate with the API.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        // Get user details for from the previous activity, this is
        // required for challenge creation.
        Intent challengeIntent = getIntent();
        challengeExtras = challengeIntent.getExtras();
        user = challengeExtras.getParcelable("user");
        opponent = challengeExtras.getParcelable("opponent");

        // Identify page elements.
        TextView txtUser = findViewById(R.id.txtChallengeUser);
        TextView txtOpponent = findViewById(R.id.txtChallengeOpponent);
        CalendarView calendarViewChallenge = findViewById(R.id.calendarChallengeDate);
        spinnerClub = findViewById(R.id.spinnerChallengeClub);
        btnTimePicker = findViewById(R.id.btnTimePicker);
        Button btnSubmitChallenge = findViewById(R.id.btnSubmitChallenge);

        // Set page elements.
        txtUser.setText(user.getLname());
        txtOpponent.setText(opponent.getLname());
        setUpClubAdapter();

        // Listener for the calender to check for a date selection.
        date_seconds = calendarViewChallenge.getDate() / 1000;
        calendarViewChallenge.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            date_seconds = calendar.getTimeInMillis() / 1000;
        });

        // Listener for the time picker button, this will open
        // a time picker fragment to allow the user to select a time.
        btnTimePicker.setOnClickListener(v -> {
            DialogFragment tpf = new TimePickerFragment();
            tpf.show(getSupportFragmentManager(), "time picker");
        });

        // Listener for a challenge submission.
        btnSubmitChallenge.setOnClickListener(v -> {
            // Obtain parameters.
            int userID = user.getplayerID();
            int opponentID = opponent.getplayerID();
            String location = spinnerClub.getSelectedItem().toString();

            // For easier data storage, send the API UNIX time and convert within the API,
            // this will also make timezone handling easier.
            String date = String.valueOf(date_seconds);

            // Create the challenge.
            createChallenge(location, date, time);
        });
    }

    /**
     * Use the user clubs to create an array list. Once complete, create
     * an array adapter to attach to the spinner.
     */
    protected void setUpClubAdapter() {
        ArrayList<String> clubs = new ArrayList<>();
        String userClub = user.getClubName();
        String opponentClub = opponent.getClubName();
        clubs.add(userClub);
        // Check they are not from the same club.
        if (!userClub.equals(opponentClub)) {
            clubs.add(opponentClub);
        }
        ArrayAdapter<String> clubAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, clubs);
        spinnerClub.setAdapter(clubAdapter);
    }

    /**
     * Set the button to display the time as provided by the time picker.
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        time = hourOfDay + ":" + minute;
        btnTimePicker.setText(time);
    }

    /**
     * Pass challenge parameters to the API via an asynchronous API request.
     * The challenge table holds challenge metadata.
     */
    protected void createChallenge(String location, String date, String time) {
        // Cast to string for hashmap representation.
        HashMap<String, String> params = new HashMap<>();
        params.put("clubname", location);
        params.put("date", date);
        params.put("time", time);
        ChallengeRequest req = new ChallengeRequest(params);
        req.execute();
    }

    /**
     * An asynchronous task to create challenge and player_challenge table entries,
     * holding challenge metadata and player data respectively. The first network
     * request returns an autogenerated challenge ID, which is then used to create
     * the corresponding player_challenge entries.
     */
    private class ChallengeRequest extends AsyncTask<Void, Void, String> {

        private final HashMap<String, String> params;

        public ChallengeRequest(HashMap<String, String> params) {
            this.params = params;
        }

        /**
         * Handle error status. On success navigate to the challenges fragment.
         */
        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject object = new JSONObject(s);
                if (object.getString("error").equals("false")) {
                    // Navigate.
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.putExtras(challengeExtras);
                    startActivity(intent);
                }
                // Display status.
                String message = object.getString("message");
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * Make the initial challenge table entry. This returns a challenge ID
         * which is used to create player_challenge entries.
         */
        @Override
        protected String doInBackground(Void... voids) {
            APIRequest challengeReq = new APIRequest();
            // IDCatch catches the returned challenge ID.
            String IDCatch = challengeReq.executePostRequest(API_URL.URL_CREATE_CHALLENGE, params);
            return createPlayerChallenge(IDCatch);
        }

        /**
         * Use the challenge ID to create player_challenge entries.
         */
        protected String createPlayerChallenge(String IDCatch) {
            APIRequest playerChallengeRequest = new APIRequest();
            try {
                JSONObject object = new JSONObject(IDCatch);
                String challengeID = object.getString("challengeid");
                // Create parameter hashmap.
                HashMap<String, String> playerChallengeParams = new HashMap<>();
                playerChallengeParams.put("challengeid", challengeID);
                playerChallengeParams.put("playerid", String.valueOf(user.getplayerID()));
                playerChallengeParams.put("opponentid", String.valueOf(opponent.getplayerID()));
                // Execute the network request, return the error status.
                return playerChallengeRequest.executePostRequest(API_URL.URL_CREATE_PLAYER_CHALLENGE, playerChallengeParams);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}