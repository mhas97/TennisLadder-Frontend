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
 * Fragment to display ladder information. An adapter interface is used to handle recycler
 * view population, as well as an on-note listener to handle individual player interaction.
 *
 * A recycler view is a dynamic list which is ideal for displaying large sets of data. As
 * such this is the primary view holder used throughout this project.
 * (https://developer.android.com/guide/topics/ui/layout/recyclerview)
 *
 * The following tutorial on recycler views was also used during implementation:
 * https://www.youtube.com/watch?v=18VcnYN5_LM
 */
public class LadderFragment extends Fragment implements LadderAdapter.OnNoteListener {

    boolean adapterInitialised;     // Flag to indicates that the adapter needs initialising.

    private TextView txtSearch;
    private RecyclerView recyclerLadder;

    /* 2 array lists are required to handle search filtering. */
    private ArrayList<TennisUser> globalPlayers;
    private ArrayList<TennisUser> players;

    /* Adapter and on-note listener */
    private LadderAdapter ladderAdapter;
    private final LadderAdapter.OnNoteListener onNoteListener = this;

    /**
     * Identify and set up the recycler view and toolbar. Make a network request to fetch ladder data.
     */
    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_ladder, container, false);

        /* Identify page elements. */
        txtSearch = view.findViewById(R.id.txtLadderSearch);
        recyclerLadder = view.findViewById(R.id.recyclerViewLadder);

        /* Fetch ladder data and set up the search bar. */
        players = new ArrayList<>();
        adapterInitialised = false;
        getLadderData();
        setupToolbar(view);
        return view;
    }

    /**
     * Create the ladder menu display. An on-query listener is
     * created to handle user queries via the ladder adapter.
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        /* Inflate the ladder menu for display. */
        menuInflater.inflate(R.menu.ladder_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.ladder_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        /* Set the on query listener. */
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            /* Handle text changes in the search box. */
            @Override
            public boolean onQueryTextChange(String newText) {
                txtSearch.setVisibility(View.INVISIBLE);    // Remove the text hint when the search box is active
                ladderAdapter.getFilter().filter(newText);  // Pass the text to the ladder adapter
                return false;
            }
        });
    }

    /**
     * Upon resuming the fragment, execute a network request to check for ladder updates.
     */
    @Override
    public void onResume() {
        super.onResume();
        getLadderData();
    }

    /**
     * Create an object to perform an asynchronous network request to fetch ladder data.
     */
    private void getLadderData() {
        LadderRequest req = new LadderRequest();
        req.execute();
    }

    /* Setup the toolbar for user queries. */
    private void setupToolbar(View view) {
        setHasOptionsMenu(true);
        /* In this case the toolbar acts as the support action bar (sits at the top of the page). */
        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);

        /* Disable the app title to make space for the search bar and text entry. */
        Objects.requireNonNull((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);

        /* Remove search tip if they are searching. */
        toolbar.setOnMenuItemClickListener(item -> {
            txtSearch.setVisibility(View.INVISIBLE);
            return false;
        });
    }

    /**
     * An on-note listener is implemented to handle ladder taps. Corresponding
     * user data is bundled and passed to the profile page.
     * @param position the position of the tapped note.
     */
    @Override
    public void onNoteClick(int position) {
        TennisUser tappedPlayer = globalPlayers.get(position);
        if (tappedPlayer.getPlayerID() != MainActivity.getUser().getPlayerID()) {   // Can't challenge yourself!
            Intent intent = new Intent(getActivity(), ProfileActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable("user", MainActivity.getUser());
            extras.putParcelable("tappedPlayer", tappedPlayer);
            intent.putExtras(extras);
            startActivity(intent);
        }
    }

    /**
     * An asynchronous task to handle ladder requests. Also
     * handles initial setup up of the recycler view adapter.
     */
    private class LadderRequest extends AsyncTask<Void, Void, String> {

        /* Parse the response, only setup the ladder adapter if this is the initial call. */
        @Override
        protected void onPostExecute(String s) {
            parseResponse(s);
            globalPlayers = players;
            if (!adapterInitialised) {  // Only initialise the adapter once.
                setUpRecyclerView(players);
                adapterInitialised = true;
            }
            else {
                ladderAdapter.notifyDataSetChanged();   // Notify the adapter that the dataset has changed.
            }
        }

        /**
         * Parse the JSON string into player objects.
         */
        private void parseResponse(String s) {
            try {
                players.clear();    // Clear the dataset and repopulate with data from the most recent API call.
                JSONObject object = new JSONObject(s);
                JSONArray playerArray = object.getJSONArray("players");
                for (int i = 0; i < playerArray.length(); i++) {
                    /* Parse the response */
                    JSONObject playerObject = playerArray.getJSONObject(i);
                    int playerID = playerObject.getInt("playerid");
                    String fname = playerObject.getString("fname");
                    String lname = playerObject.getString("lname");
                    String clubName = playerObject.getString("clubname");
                    int elo = playerObject.getInt("elo");
                    int hotstreak = playerObject.getInt("hotstreak");
                    int matchesPlayed = playerObject.getInt("matchesplayed");
                    int wins = playerObject.getInt("wins");
                    int losses = playerObject.getInt("losses");
                    int highestElo = playerObject.getInt("highestelo");
                    int clubChamp = playerObject.getInt("clubchamp");

                    /* Fetch the users achievement data. */
                    ArrayList<Integer> achieved = parseAchieved(playerObject);

                    /* Create the resulting challenge object. */
                    TennisUser player = new TennisUser(playerID, fname, lname, clubName, elo, hotstreak, matchesPlayed, wins, losses, highestElo, clubChamp, achieved);

                    players.add(player);
                }
                Collections.sort(players, (p1, p2) -> p2.getElo() - p1.getElo());   // Sort by Elo rating.
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * Setup the adapter for the recycler view, only happens on the initial run.
         */
        private void setUpRecyclerView(ArrayList<TennisUser> players) {
            ladderAdapter = new LadderAdapter(getContext(), players, onNoteListener);
            recyclerLadder.setAdapter(ladderAdapter);
            recyclerLadder.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerLadder.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        }

        /* Parse the users achievements. */
        private ArrayList<Integer> parseAchieved(JSONObject object) {
            try {
                ArrayList<Integer> achieved = new ArrayList<>();
                /* Parse the response. */
                JSONArray achievedArray = object.getJSONArray("achieved");
                for (int i = 0; i < achievedArray.length(); i++) {
                    achieved.add(achievedArray.getInt(i));
                }
                return achieved;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
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
