package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.AsyncTask;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;

public class LadderActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ladder);

        getLadderData();
    }

    public void getLadderData() {
        LadderRequest req = new LadderRequest();
        req.execute();
    }

    private class LadderRequest extends AsyncTask<Void, Void, String> {

        LadderRequest() { }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(String s)
        {
            try
            {
                JSONObject object = new JSONObject(s);
                JSONArray arr = object.getJSONArray("players");
                ArrayList<TennisUser> players = new ArrayList<TennisUser>();
                // Parse player data
                for (int i = 0; i < arr.length(); ++i)
                {
                    JSONObject obj = arr.getJSONObject(i);
                    String fname = obj.getString("fname");
                    String lname = obj.getString("lname");
                    int elo = Integer.parseInt(obj.getString("elo"));
                    int hotstreak = Integer.parseInt(obj.getString("hotstreak"));
                    TennisUser player = new TennisUser(fname, lname, elo, hotstreak);
                    players.add(player);
                }
                Collections.sort(players, (p1, p2) -> p2.getElo() - p1.getElo());
                recyclerView = findViewById(R.id.ladder_recyclerview);
                LadderAdapter ladderAdapter = new LadderAdapter(getApplicationContext(), players);
                recyclerView.setAdapter(ladderAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids)
        {
            RequestHandler rqh = new RequestHandler();
            return rqh.sendGetRequest(API.URL_GET_LADDER_DATA);
        }
    }
}