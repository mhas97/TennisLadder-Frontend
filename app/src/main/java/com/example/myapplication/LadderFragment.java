package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.SearchView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class LadderFragment extends Fragment implements LadderAdapter.OnNoteListener {

    private TextView txtSearch;
    private RecyclerView recyclerView;
    private ArrayList<TennisUser> globalPlayers;
    private ArrayList<TennisUser> players;
    private LadderAdapter ladderAdapter;
    private final LadderAdapter.OnNoteListener onNoteListener = this;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_ladder, container, false);
        txtSearch = view.findViewById(R.id.txtLadderSearch);
        recyclerView = view.findViewById(R.id.ladder_recyclerview);

        players = new ArrayList<>();
        getLadderData();

        setHasOptionsMenu(true);
        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOnMenuItemClickListener(item -> {    // Remove search tip if they are searching.
            txtSearch.setVisibility(View.INVISIBLE);
            return false;
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
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
    }

    @Override
    public void onResume() {
        super.onResume();
        getLadderData();
    }

    @Override
    public void onNoteClick(int position) {
        TennisUser tappedPlayer = globalPlayers.get(position);
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelable("user", MainActivity.getUser());
        extras.putParcelable("tappedPlayer", tappedPlayer);
        intent.putExtras(extras);
        startActivity(intent);
    }

    protected void getLadderData() {
        LadderRequest req = new LadderRequest();
        req.execute();
    }

    private class LadderRequest extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s)
        {
            parseResponse(s);
            globalPlayers = players;
            setupRecyclerView(players);
        }

        protected void parseResponse(String s) {
            try {
                players.clear();
                JSONObject object = new JSONObject(s);
                JSONArray arr = object.getJSONArray("players");
                for (int i = 0; i < arr.length(); ++i) {
                    JSONObject obj = arr.getJSONObject(i);
                    int playerID = obj.getInt("playerid");
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
                    TennisUser player = new TennisUser(playerID, fname, lname, clubName, elo, hotstreak, matchesPlayed, wins, losses, highestElo, clubChamp);
                    players.add(player);
                }
                Collections.sort(players, (p1, p2) -> p2.getElo() - p1.getElo());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        protected void setupRecyclerView(ArrayList<TennisUser> players) {
            ladderAdapter = new LadderAdapter(getContext(), players, onNoteListener);
            recyclerView.setAdapter(ladderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        }

        @Override
        protected String doInBackground(Void... voids)
        {
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.sendGetRequest(API_URL.URL_GET_LADDER_DATA);
        }
    }
}
