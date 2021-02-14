package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.AsyncTask;
import android.os.Bundle;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class LadderActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ladder);
        getLadderData();
    }

    public void getLadderData() {
        LadderRequest req = new LadderRequest(API.URL_GET_LADDER_DATA, API.REQ_TYPE_GET);
        req.execute();
    }

    private class LadderRequest extends AsyncTask<Void, Void, String> {
        String url;
        int requestCode;

        LadderRequest(String url, int requestCode)
        {
            this.url = url;
            this.requestCode = requestCode;
        }

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
                for (int i = 0; i < arr.length(); ++i)
                {
                    JSONObject obj = arr.getJSONObject(i);
                    String fname = obj.getString("fname");
                    String lname = obj.getString("lname");
                    int elo = Integer.parseInt(obj.getString("elo"));
                    TennisUser t = new TennisUser(fname, lname, elo);
                    players.add(t);
                }
                populateLadder(players);
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        private void populateLadder(ArrayList<TennisUser> p) {
            recyclerView = findViewById(R.id.ladder_recyclerview);
            LadderAdapter l = new LadderAdapter(getApplicationContext(), p);
        }

        @Override
        protected String doInBackground(Void... voids)
        {
            RequestHandler rqh = new RequestHandler();
            if (requestCode == API.REQ_TYPE_POST)
            {
                return rqh.sendPostRequest(url, null);
            }
            if (requestCode == API.REQ_TYPE_GET)
            {
                return rqh.sendGetRequest(url);
            }
            return null;
        }
    }
}