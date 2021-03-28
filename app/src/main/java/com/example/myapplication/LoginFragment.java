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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Allows a user to enter credentials and attempt to login. Upon success, necessary user app
 * data will be returned from the API and bundled for activity switching.
 */
public class LoginFragment extends Fragment {

    private EditText txtEmail, txtPassword;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_login, container, false);

        /* Identify page elements. */
        txtEmail = view.findViewById(R.id.txtEmailLogin);
        txtPassword = view.findViewById(R.id.txtPasswordLogin);
        Button btnLogin = view.findViewById(R.id.btnLogin);

        /* Attempt to login upon button press. */
        btnLogin.setOnClickListener(v -> {
            attemptLogin();
        });
        return view;
    }

    /**
     * Obtain user information and create a login request to execute asynchronously.
     */
    private void attemptLogin() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();

        /* Create a parameter hashmap to send to the API. */
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

        public LoginRequest(HashMap<String, String> params) {
            this.params = params;
        }

        /**
         * When the network request is completed, attempt to parse
         * the returned data and create a bundled user object.
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
         * @param s User data JSON string.
         * @return The resulting tennis user object.
         */
        private TennisUser parseUserData(String s) {
            try {
                JSONObject object = new JSONObject(s);
                /* Check for error status. */
                if (object.getString("error").equals("false")) {
                    /* Parse the response. */
                    JSONObject playerObject = object.getJSONObject("player");
                    int playerID = playerObject.getInt("playerid");
                    String email = playerObject.getString("email");
                    String contactNo = playerObject.getString("contactno");
                    String fname = playerObject.getString("fname");
                    String lname = playerObject.getString("lname");
                    String clubName = playerObject.getString("clubname");
                    int elo = playerObject.getInt("elo");
                    int winstreak = playerObject.getInt("winstreak");
                    int hotstreak = playerObject.getInt("hotstreak");
                    int matchesPlayed = playerObject.getInt("matchesplayed");
                    int wins = playerObject.getInt("wins");
                    int losses = playerObject.getInt("losses");
                    int highestElo = playerObject.getInt("highestelo");
                    int clubChamp = playerObject.getInt("clubchamp");

                    /* Obtain the user achievements. */
                    ArrayList<Integer> achieved = parseAchieved(playerObject);

                    /* Create a list of global achievements. */
                    fetchGlobalAchievements(object);

                    /* Create resulting user object. */
                    return new TennisUser(playerID, email, contactNo, fname, lname,
                            clubName, elo, winstreak, hotstreak, matchesPlayed, wins,
                            losses, highestElo, clubChamp, achieved);
                }
                /* Display message via toast */
                String message = object.getString("message");
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Instantiate the Singleton instance and use its single method to hold the list of global achievements.
         */
        private void fetchGlobalAchievements(JSONObject object) {
            GlobalAchievements globalAchievements = GlobalAchievements.getInstance();
            globalAchievements.setAchievementList(parseGlobalAchievements(object));
        }

        /**
         * Parse the achievements JSON object.
         */
        private HashMap<String, String[]> parseGlobalAchievements(JSONObject object) {
            try {
                /* Check for error status. */
                HashMap<String, String[]> achievementMap = new HashMap<>();
                /* Parse the response. */
                JSONArray achievementArray = object.getJSONArray("achievements");
                for (int i = 0; i < achievementArray.length(); i++) {
                    JSONObject achievementObject = achievementArray.getJSONObject(i);
                    String achievementID = achievementObject.getString("achievementid");
                    String achievementName = achievementObject.getString("achievementname");
                    String achievementdescription = achievementObject.getString("achievementdescription");
                    String[] achievementData = { achievementName, achievementdescription };
                    achievementMap.put(achievementID, achievementData);
                }
                return achievementMap;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Parse the users achievements.
         */
        private ArrayList<Integer> parseAchieved(JSONObject object) {
            try {
                /* Check for error status. */
                ArrayList<Integer> achieved = new ArrayList<>();
                /* Parse the response. */
                JSONArray playerAchieved = object.getJSONArray("achieved");
                for (int i = 0; i < playerAchieved.length(); i++) {
                    achieved.add(playerAchieved.getInt(i));
                }
                return achieved;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * Create an intent to navigate to the challenges page, as login has been successful.
         * A Bundle containing user data is passed along with the intent.
         */
        private void login(TennisUser user) {
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
            APIRequest req = new APIRequest();
            return req.executePostRequest(API_URL.URL_LOGIN, params);
        }
    }
}
