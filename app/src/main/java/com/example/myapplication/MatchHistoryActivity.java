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

/**
 * Allows viewing of user match history. Bundled data is passed in either one of two ways:
 * - the users app data for viewing personal match history
 * - accessing match history on another users profile
 */
public class MatchHistoryActivity extends AppCompatActivity {

    private ArrayList<TennisChallenge> matches;
    private MatchesAdapter matchesAdapter;
    private TennisUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_history);

        /* Obtain bundled data. */
        Intent matchHistoryIntent = getIntent();
        Bundle matchExtras = matchHistoryIntent.getExtras();
        user = matchExtras.getParcelable("user");

        /* Set up page elements and fetch match history data via the API. */
        matches = new ArrayList<>();
        setUpRecyclerView();
        getMatches();
    }

    /**
     * Indentify the recycler view and attach its adapter. Once the API request
     * is complete, the adapter is notified and the match history data is displayed.
     */
    protected void setUpRecyclerView() {
        RecyclerView recyclerView = findViewById(R.id.recyclerViewChallenges);
        matchesAdapter = new MatchesAdapter(matches, this);
        recyclerView.setAdapter(matchesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    /**
     * Create an asynchronous object to fetch match history via the API.
     */
    protected void getMatches() {
        MatchHistoryRequest req = new MatchHistoryRequest();
        req.execute();
    }

    /**
     * An asynchronous task to fetch match history data via the API.
     */
    private class MatchHistoryRequest extends AsyncTask<Void, Void, String> {

        /**
         * Parse the JSON encoded string and notify the adapter that the dataset has changed.
         */
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            parseResponse(s);
            matchesAdapter.notifyDataSetChanged();
        }

        /**
         * Parse the JSON encoded string and create corresponding TennisChallenge objects.
         * @param s Match history JSON encoded string.
         */
        protected void parseResponse(String s) {
            try {
                JSONObject object = new JSONObject(s);
                JSONArray arr = object.getJSONArray("challenges");
                for (int i = 0; i < arr.length(); ++i) {
                    /* Parse the response. */
                    JSONObject obj = arr.getJSONObject(i);
                    int challengeID = obj.getInt("challengeid");
                    int didWin = obj.getInt("didwin");
                    String date = obj.getString("date");
                    String score = obj.getString("score");
                    int oppID = obj.getInt("opponentid");
                    String oppFname = obj.getString("fname");
                    String oppLname = obj.getString("lname");
                    TennisChallenge match = new TennisChallenge(challengeID, new TennisUser(oppID, oppFname, oppLname), date, didWin, score);
                    matches.add(match);
                }
                /* Reverse the order so the latest challenge displays first. */
                Collections.reverse(matches);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * APIRequest object handles HTTP communication with the API.
         */
        @Override
        protected String doInBackground(Void... voids) {
            APIRequest req = new APIRequest();
            int playerID = user.getplayerID();
            return req.executeGetRequest(API_URL.URL_GET_MATCH_HISTORY + playerID);
        }
    }
}