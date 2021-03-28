package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Retrieves user entered information and attempts to create an account. Communicates
 * with the API via an APIRequest object, success status is returned.
 */
public class SignupFragment extends Fragment {

    private EditText txtEmail, txtPassword, txtContactNo, txtFname, txtLname;
    private Spinner spinnerClub;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (ViewGroup) layoutInflater.inflate(R.layout.fragment_signup, container, false);

        /* Identify page elements. */
        txtEmail = view.findViewById(R.id.txtEmailSignup);
        txtPassword = view.findViewById(R.id.txtPasswordSignup);
        txtContactNo = view.findViewById(R.id.txtContactNoSignup);
        txtFname = view.findViewById(R.id.txtFnameSignup);
        txtLname = view.findViewById(R.id.txtLnameSignup);
        spinnerClub = view.findViewById(R.id.spinnerClubSignup);

        /* Attempt to signup upon button press. */
        Button btnSignup = view.findViewById(R.id.btnSignup);
        getClubList();
        btnSignup.setOnClickListener(v -> {
            createUser();
        });
        return view;
    }

    /**
     * Create a club request to execute asynchronously. This fetches a
     * list of valid clubs from the database ensuring challenge integrity.
     */
    private void getClubList() {
        clubRequest req = new clubRequest();
        req.execute();
    }

    /**
     * Obtain user information to create a signup request.
     */
    private void createUser() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        String contactno = txtContactNo.getText().toString().trim();
        String fname = txtFname.getText().toString().trim();
        String lname = txtLname.getText().toString().trim();
        String clubname = spinnerClub.getSelectedItem().toString().trim();

        /* Create a parameter hashmap to send to the API. */
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        params.put("contactno", contactno);
        params.put("fname", fname);
        params.put("lname", lname);
        params.put("clubname", clubname);

        /* Create a signup request object to execute asynchronously. */
        SignupRequest req = new SignupRequest(params);
        req.execute();
    }

    /**
     * An asynchronous task that fetches valid clubs for selection.
     */
    private class clubRequest extends AsyncTask<Void, Void, String> {

        ArrayList<String> clubList;

        @Override
        protected void onPostExecute(String s) {
            parseClubs(s);
            attachAdapter(clubList);
        }

        private void parseClubs(String s) {
            try {
                /* Parse returned club names. */
                JSONObject object = new JSONObject(s);
                JSONArray arr = object.getJSONArray("clubs");

                /* Use this data to create an array list for an adapter. */
                clubList = new ArrayList<>();
                for (int i = 0; i < arr.length(); ++i) {
                    clubList.add(arr.getString(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * Attach the adapter to the club spinner.
         * @param clubs The list of clubs.
         */
        private void attachAdapter(ArrayList<String> clubs) {
            ArrayAdapter<String> clubAdapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, clubs);
            spinnerClub.setAdapter(clubAdapter);
        }

        /**
         * Create a request handler object to handle HTTP communication with the API.
         */
        @Override
        protected String doInBackground(Void... voids) {
            APIRequest req = new APIRequest();
            return req.executeGetRequest(API_URL.URL_GET_CLUBS);
        }
    }

    /**
     * An asynchronous task that processes a signup request.
     */
    private class SignupRequest extends AsyncTask<Void, Void, String> {

        HashMap<String, String> params;

        SignupRequest(HashMap<String, String> params) {
            this.params = params;
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject object = new JSONObject(s);
                /* Display success status. */
                Toast.makeText(getContext(), object.getString("message"), Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * Create a request handler object to handle HTTP communication with the API.
         */
        @Override
        protected String doInBackground(Void... voids) {
            APIRequest req = new APIRequest();
            return req.executePostRequest(API_URL.URL_CREATE_PLAYER, params);
        }
    }
}
