package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A sign up activity.
 * This page obtains user input via various fields and makes a network request
 * to the sign up functionality of the API. Club data is dynamically fetched
 * on creation to allow users to only select valid clubs. Both sign-up and getClubs
 * are asynchronous activities (via request handler).
 */
public class SignupActivity extends AppCompatActivity {
    EditText editTextEmail, editTextPassword, editTextContactno, editTextFname, editTextLname;
    Spinner spinnerClub;
    Button buttonSignup;

    /**
     * Identify elements on the sign up page and assign to variables.
     * Call getClubList to obtain a list of valid clubs to populate the spinner.
     * If the sign up button is pressed, call createUser.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);
        editTextEmail = findViewById(R.id.email_input_signup);
        editTextPassword = findViewById(R.id.password_input_signup);
        editTextContactno = findViewById(R.id.contactno_input_signup);
        editTextFname = findViewById(R.id.fname_input_signup);
        editTextLname = findViewById(R.id.lname_input_signup);
        spinnerClub = findViewById(R.id.spinner_club_signup);
        buttonSignup = findViewById(R.id.signup_button);

        getClubList();

        buttonSignup.setOnClickListener(view -> {
            createUser();
        });
    }

    /**
     * Create a club request object to obtain a list of clubs.
     */
    private void getClubList() {
        clubRequest clubRequest = new clubRequest();
        clubRequest.execute();
    }

    /**
     * Inner class for handling club requests.
     * First create a RequestHandler object to contact the database via API.
     * Handle the returned data in the post execute function.
     */
    private class clubRequest extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler rqh = new RequestHandler();
            return rqh.sendGetRequest(API.URL_GET_CLUBS);
        }

        /**
         *
         * Create a JSON object from the returned JSON string,
         * Use this object to create a clubs ArrayList.
         * Once this has been fully populated, attach an ArrayAdapter to the
         * spinner to populate it with the data.
         */
        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject object = new JSONObject(s);
                JSONArray arr = object.getJSONArray("clubs");
                ArrayList<String> clubs = new ArrayList<>();
                for (int i = 0; i < arr.length(); ++i)
                {
                    clubs.add(arr.getString(i));
                }
                ArrayAdapter<String> clubAdapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.support_simple_spinner_dropdown_item, clubs);
                spinnerClub.setAdapter(clubAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Use identified elements to obtain sign up variables.
     * Insert these into a hashmap to provide parameters to the API.
     * Create a signupRequest object to handle network requests.
     */
    private void createUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String contactno = editTextContactno.getText().toString().trim();
        String fname = editTextFname.getText().toString().trim();
        String lname = editTextLname.getText().toString().trim();
        String clubname = spinnerClub.getSelectedItem().toString().trim();

        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("contactno", contactno);
        params.put("fname", fname);
        params.put("lname", lname);
        params.put("clubname", clubname);

        SignupRequest signupRequest = new SignupRequest(params);
        signupRequest.execute();
    }

    /**
     * Inner class to handle network requests for a signup request.
     * Make a network request via the api, providing signup info as parameters.
     * Create a toast on post execute to output success status.
     */
    private class SignupRequest extends AsyncTask<Void, Void, String> {
        HashMap<String, String> params;

        SignupRequest(HashMap<String, String> params)
        {
            this.params = params;
        }

        @Override
        protected void onPreExecute() { }

        /**
         * Display success status
         */
        @Override
        protected void onPostExecute(String s)
        {
            try
            {
                JSONObject object = new JSONObject(s);
                Toast.makeText(getApplicationContext(), object.getString("message"), Toast.LENGTH_LONG).show();
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }


        /**
         * Contact API via request handler
         */
        @Override
        protected String doInBackground(Void... voids)
        {
            RequestHandler rqh = new RequestHandler();
            return rqh.sendPostRequest(API.URL_CREATE_PLAYER, params);
        }
    }
}