package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SignupActivity extends AppCompatActivity {
    EditText editTextEmail, editTextPassword, editTextContactno, editTextFname, editTextLname;
    Spinner spinnerClub;
    Button buttonSignup;
    boolean isUpdating = false;
    ArrayList<String> clubs;

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
            if (isUpdating) {
            } else {
                createUser();
            }
        });
    }

    private void getClubList() {
        clubRequest clubRequest = new clubRequest();
        clubRequest.execute();
    }

    private class clubRequest extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler rqh = new RequestHandler();
            return rqh.sendGetRequest(API.URL_GET_CLUBS);
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }

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

    private class SignupRequest extends AsyncTask<Void, Void, String> {
        HashMap<String, String> params;

        SignupRequest(HashMap<String, String> params)
        {
            this.params = params;
        }

        @Override
        protected void onPreExecute() { }

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

        @Override
        protected String doInBackground(Void... voids)
        {
            RequestHandler rqh = new RequestHandler();
            return rqh.sendPostRequest(API.URL_CREATE_PLAYER, params);
        }
    }
}