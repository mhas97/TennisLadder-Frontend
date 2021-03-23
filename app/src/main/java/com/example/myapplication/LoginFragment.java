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

public class LoginFragment extends Fragment {

    static TennisUser user;
    private EditText txtEmail, txtPassword;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_login, container, false);
        txtEmail = view.findViewById(R.id.txtEditEmailLogin);
        txtPassword = view.findViewById(R.id.txtEditPasswordLogin);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            login();
        });
        return view;
    }

    protected void login() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        LoginRequest req = new LoginRequest(params);
        req.execute();
    }

    private class LoginRequest extends AsyncTask<Void, Void, String> {
        HashMap<String, String> params;

        LoginRequest(HashMap<String, String> params) {
            this.params = params;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(String s)
        {
            try {
                JSONObject object = new JSONObject(s);
                if (object.getString("error").equals("false")) {
                    JSONObject obj = object.getJSONObject("player");
                    int playerID = obj.getInt("playerid");
                    String email = obj.getString("email");
                    String contactNo = obj.getString("contactno");
                    String fname = obj.getString("fname");
                    String lname = obj.getString("lname");
                    String clubName = obj.getString("clubname");
                    int elo = obj.getInt("elo");
                    int hotstreak = obj.getInt("hotstreak");
                    int matchesPlayed = obj.getInt("matchesplayed");
                    int wins = obj.getInt("wins");
                    int losses = obj.getInt("losses");
                    int highestElo = obj.getInt("highestelo");
                    int clubChamp = obj.getInt("clubchamp");
                    user = new TennisUser(playerID, email, contactNo, fname, lname, clubName, elo, hotstreak, matchesPlayed, wins, losses, highestElo, clubChamp);

                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    Bundle extras = new Bundle();
                    extras.putParcelable("user", user);
                    intent.putExtras(extras);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(), "Incorrect username or password", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.sendPostRequest(API_URL.URL_LOGIN, params);
        }
    }
}
