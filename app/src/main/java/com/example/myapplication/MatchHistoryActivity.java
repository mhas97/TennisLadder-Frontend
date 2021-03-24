package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;

public class MatchHistoryActivity extends AppCompatActivity {

    private ArrayList<TennisChallenge> matches;
    private MatchesAdapter matchesAdapter;
    private TennisUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_history);

        Intent matchHistoryIntent = getIntent();
        Bundle matchExtras = matchHistoryIntent.getExtras();
        user = matchExtras.getParcelable("user");

        matches = new ArrayList<>();
        setUpRecyclerView();
        getMatches();
    }

    protected void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewChallenges);
        matchesAdapter = new MatchesAdapter(matches, this);
        recyclerView.setAdapter(matchesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    protected void getMatches() {
        MatchHistoryRequest req = new MatchHistoryRequest();
        req.execute();
    }

    private class MatchHistoryRequest extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            parseResponse(s);
            matchesAdapter.notifyDataSetChanged();
        }

        protected void parseResponse(String s) {
            try {
                JSONObject object = new JSONObject(s);
                JSONArray arr = object.getJSONArray("challenges");
                for (int i = 0; i < arr.length(); ++i) {
                    JSONObject obj = arr.getJSONObject(i);
                    int challengeID = obj.getInt("challengeid");
                    int oppID = obj.getInt("opponentid");
                    String oppFname = obj.getString("fname");
                    String oppLname = obj.getString("lname");
                    String date = obj.getString("date");
                    int didWin = obj.getInt("didwin");
                    String score = obj.getString("score");
                    TennisChallenge match = new TennisChallenge(challengeID, new TennisUser(oppID, oppFname, oppLname), date, didWin, score);
                    matches.add(match);
                }
                // Reverse the order so the latest challenge displays first
                Collections.reverse(matches);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler req = new RequestHandler();
            return req.sendGetRequest(API_URL.URL_GET_MATCH_HISTORY + user.getplayerID());
        }
    }
}