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

/**
 * A fragment to display ladder information for a user. An adapter interface
 * is implemented to handle recycler view population and searching. An on-note listener
 * is used handle others players being tapped by the user. A recycler view is a dynamic
 * list which is ideal for displaying large sets of data. As such this is the primary
 * view holder used throughout this project.
 * (https://developer.android.com/guide/topics/ui/layout/recyclerview)
 */
public class LadderFragment extends Fragment implements LadderAdapter.OnNoteListener {

    // Flag to indicates that the adapter needs initialising.
    boolean adapterInitialised;

    // Page elements.
    private TextView txtSearch;
    private RecyclerView recyclerLadder;

    // 2 array lists are required to handle search filtering.
    private ArrayList<TennisUser> globalPlayers;
    private ArrayList<TennisUser> players;

    private LadderAdapter ladderAdapter;
    private final LadderAdapter.OnNoteListener onNoteListener = this;

    /**
     * Identify and set up the recycler view and toolbar. Make a network request
     * to fetch ladder data.
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_ladder, container, false);

        // Identify page elements.
        txtSearch = view.findViewById(R.id.txtLadderSearch);
        recyclerLadder = view.findViewById(R.id.recyclerViewLadder);

        players = new ArrayList<>();
        adapterInitialised = false;
        getLadderData();
        setupToolbar(view);
        return view;
    }

    /**
     * Create the ladder menu display. An on-query listener is created
     * to handle user queries via the ladder adapter.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        // Inflate the ladder menu for display.
        menuInflater.inflate(R.menu.ladder_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.ladder_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        // Set the on query listener.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            //Handle text changes in the search box.
            @Override
            public boolean onQueryTextChange(String newText) {
                // Remove the text hint when the search box is active
                txtSearch.setVisibility(View.INVISIBLE);
                // Pass the text to the ladder adapter
                ladderAdapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    /**
     * Upon resuming the fragment, execute a network request to check
     * for any ladder updates.
     */
    @Override
    public void onResume() {
        super.onResume();
        getLadderData();
    }

    /**
     * Create an object to perform an asynchronous network request to fetch
     * ladder data.
     */
    protected void getLadderData() {
        LadderRequest req = new LadderRequest();
        req.execute();
    }

    //Setup the toolbar for user queries.
    protected void setupToolbar(View view) {
        setHasOptionsMenu(true);
        // The toolbar acts as the action bar (at the top of the screen) in this case.
        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        // Disable the app title to make space for the search text.
        Objects.requireNonNull((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        // Remove search tip if they are searching.
        toolbar.setOnMenuItemClickListener(item -> {
            txtSearch.setVisibility(View.INVISIBLE);
            return false;
        });
    }

    /**
     * An on-note listener is implemented to handle ladder taps.
     * Corresponding user data is bundled and passed to the profile
     * page.
     * @param position the position of the note tapped.
     */
    @Override
    public void onNoteClick(int position) {
        TennisUser tappedPlayer = globalPlayers.get(position);
        // You can't challenge yourself!
        if (tappedPlayer.getplayerID() != MainActivity.getUser().getplayerID()) {
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable("user", MainActivity.getUser());
            extras.putParcelable("tappedPlayer", tappedPlayer);
            intent.putExtras(extras);
            startActivity(intent);
        }
    }

    /**
     * An asynchronous task to handle ladder requests. Also handles the
     * setting up of the recycler view adapter.
     */
    private class LadderRequest extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s)
        {
            parseResponse(s);
            globalPlayers = players;
            if (!adapterInitialised) {
                setUpRecyclerView(players);
                adapterInitialised = true;
            }
            // Otherwise simply notify the adapter that the data set may have changed.
            else {
                ladderAdapter.notifyDataSetChanged();
            }
        }

        /**
         * Parse the JSON string into player objects.
         */
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
                // Order the list by Elo.
                Collections.sort(players, (p1, p2) -> p2.getElo() - p1.getElo());
                TennisUser user = MainActivity.getUser();
                int userID = user.getplayerID();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * Setup the adapter for the recycler view, only happens on the initial run.
         */
        protected void setUpRecyclerView(ArrayList<TennisUser> players) {
            ladderAdapter = new LadderAdapter(getContext(), players, onNoteListener);
            recyclerLadder.setAdapter(ladderAdapter);
            recyclerLadder.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerLadder.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        }

        /**
         * API Request.
         * @return Error status.
         */
        @Override
        protected String doInBackground(Void... voids)
        {
            APIRequest req = new APIRequest();
            return req.executeGetRequest(API_URL.URL_GET_LADDER_DATA);
        }
    }
}
