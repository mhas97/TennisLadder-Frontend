package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.SearchView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class LadderActivity extends AppCompatActivity {

    private LadderAdapter ladderAdapter;
    private TextView txtSearch;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ladder);
        getLadderData();
        Toolbar toolbar = findViewById(R.id.my_toolbar);
        txtSearch = findViewById(R.id.txtLadderSearch);
        recyclerView = findViewById(R.id.ladder_recyclerview);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        toolbar.setOnMenuItemClickListener(item -> {    // Remove search tip if they are searching.
            txtSearch.setVisibility(View.INVISIBLE);
            return false;
        });

        // This is hacky, but if they are flinging the recyclerview, they can't be searching! Make the text visible again.
        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                txtSearch.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    protected void getLadderData() {
        LadderRequest req = new LadderRequest();
        req.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.ladder_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.ladder_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                txtSearch.setVisibility(View.INVISIBLE);
                ladderAdapter.getFilter().filter(newText);
                return false;
            }
        });
        return true;
    }

    private class LadderRequest extends AsyncTask<Void, Void, String> {

        public LadderRequest() { }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(String s)
        {
            ArrayList<TennisUser> players = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(s);
                JSONArray arr = object.getJSONArray("players");
                for (int i = 0; i < arr.length(); ++i) {
                    JSONObject obj = arr.getJSONObject(i);
                    int playerID = Integer.parseInt(obj.getString("playerid"));
                    String fname = obj.getString("fname");
                    String lname = obj.getString("lname");
                    int elo = Integer.parseInt(obj.getString("elo"));
                    int hotstreak = Integer.parseInt(obj.getString("hotstreak"));
                    TennisUser player = new TennisUser(playerID, fname, lname, elo, hotstreak);
                    players.add(player);
                }
                Collections.sort(players, (p1, p2) -> p2.getElo() - p1.getElo());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setupRecyclerView(players);
        }

        protected void setupRecyclerView(ArrayList<TennisUser> players) {
            recyclerView = findViewById(R.id.ladder_recyclerview);
            ladderAdapter = new LadderAdapter(getApplicationContext(), players);
            recyclerView.setAdapter(ladderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        }

        @Override
        protected String doInBackground(Void... voids)
        {
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.sendGetRequest(API_URL.URL_GET_LADDER_DATA);
        }
    }
}