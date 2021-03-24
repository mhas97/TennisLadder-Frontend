package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

/**
 * Allows a user to enter credentials and attempt to login. Upon
 * success, the user will be navigated to the challenges page
 * along with a bundled object holding necessary user data.
 */
public class LoginFragment extends Fragment {

    private EditText txtEmail, txtPassword;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_login, container, false);

        // Identify page elements.
        txtEmail = view.findViewById(R.id.txtEmailLogin);
        txtPassword = view.findViewById(R.id.txtPasswordLogin);
        Button btnLogin = view.findViewById(R.id.btnLogin);

        // Attempt to login upon button press.
        btnLogin.setOnClickListener(v -> {
            attemptLogin();
        });
        return view;
    }

    /**
     * Obtain user information and create a login request to
     * execute asynchronously.
     */
    protected void attemptLogin() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        // Create a parameter hashmap to send to the API.
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);

        LoginRequest req = new LoginRequest(params);
        req.execute();
    }

    /**
     * An asynchronous task that processes a login request.
     */
    private class LoginRequest extends AsyncTask<Void, Void, String> {
        HashMap<String, String> params;

        LoginRequest(HashMap<String, String> params) {
            this.params = params;
        }

        /**
         * When the network request is completed, attempt to parse the
         * returned data and create a bundled user object.
         */
        @Override
        protected void onPostExecute(String s) {
            TennisUser user = parseUserData(s);
            if (user != null) {
                login(user);
            }

        }

        /**
         * Attempt to parse the returned JSON string and create a user object.
         * @param s User data JSON string
         * @return The resulting tennis user object.
         */
        protected TennisUser parseUserData(String s) {
            try {
                JSONObject object = new JSONObject(s);
                // Check for error status.
                if (object.getString("error").equals("false")) {
                    // Parse data.
                    JSONObject obj = object.getJSONObject("player");
                    int playerID = obj.getInt("playerid");
                    String email = obj.getString("email");
                    String contactNo = obj.getString("contactno");
                    String fname = obj.getString("fname");
                    String lname = obj.getString("lname");
                    String clubName = obj.getString("clubname");
                    int elo = obj.getInt("elo");
                    int winstreak = obj.getInt("winstreak");
                    int hotstreak = obj.getInt("hotstreak");
                    int matchesPlayed = obj.getInt("matchesplayed");
                    int wins = obj.getInt("wins");
                    int losses = obj.getInt("losses");
                    int highestElo = obj.getInt("highestelo");
                    int clubChamp = obj.getInt("clubchamp");

                    // Create the resulting user object.
                    return new TennisUser(playerID, email, contactNo, fname, lname,
                            clubName, elo, winstreak, hotstreak, matchesPlayed, wins,
                            losses, highestElo, clubChamp);
                }
                else {
                    // Notify the user that invalid credentials have been entered.
                    Toast.makeText(getContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Create an intent to navigate to the challenges page, as login
         * has been successful. A Bundle containing user data is passed
         * along with the intent.
         */
        protected void login(TennisUser user) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable("user", user);
            intent.putExtras(extras);
            startActivity(intent);
        }

        /**
         * Create a request handler object to handle HTTP communication with the API.
         */
        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler req = new RequestHandler();
            return req.sendPostRequest(API_URL.URL_LOGIN, params);
        }
    }
}
