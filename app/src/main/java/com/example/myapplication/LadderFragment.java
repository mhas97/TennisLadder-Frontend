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

    private TennisUser user;
    private ArrayList<TennisUser> globalPlayers;
    private TextView txtSearch;
    private RecyclerView recyclerView;
    private LadderAdapter ladderAdapter;
    private final LadderAdapter.OnNoteListener onNoteListener = this;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_ladder, container, false);

        // In order to track the logged in user, send some important info from the login procedure.
        // This is passed and retrieved using intents, in this case my player class implements the
        // Parcelable interface, allowing objects to be passed between activities.
        Intent ladderIntent = getActivity().getIntent();
        Bundle ladderExtras = ladderIntent.getExtras();
        user = ladderExtras.getParcelable("user");

        getLadderData();
        setHasOptionsMenu(true);
        Toolbar toolbar = view.findViewById(R.id.my_toolbar);
        txtSearch = view.findViewById(R.id.txtLadderSearch);
        recyclerView = view.findViewById(R.id.ladder_recyclerview);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        Objects.requireNonNull((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOnMenuItemClickListener(item -> {    // Remove search tip if they are searching.
            txtSearch.setVisibility(View.INVISIBLE);
            return false;
        });

        // This is hacky, but if they are flinging the recyclerview, they can't be searching! Make the text visible again.
        recyclerView.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                txtSearch.setVisibility(View.VISIBLE);
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(getContext().INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);
                return false;
            }
        });
        return view;
    }

    protected void getLadderData() {
        LadderRequest req = new LadderRequest();
        req.execute();
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
    public void onNoteClick(int position) {
        TennisUser tappedPlayer = globalPlayers.get(position);

        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelable("user", user);
        extras.putParcelable("tappedPlayer", tappedPlayer);
        intent.putExtras(extras);
        startActivity(intent);
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
                    String clubName = obj.getString("clubname");
                    int elo = Integer.parseInt(obj.getString("elo"));
                    int hotstreak = Integer.parseInt(obj.getString("hotstreak"));
                    TennisUser player = new TennisUser(playerID, fname, lname, clubName, elo, hotstreak);
                    players.add(player);
                }
                Collections.sort(players, (p1, p2) -> p2.getElo() - p1.getElo());
                globalPlayers = players;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setupRecyclerView(players);
        }

        protected void setupRecyclerView(ArrayList<TennisUser> players) {
            ladderAdapter = new LadderAdapter(getActivity().getApplicationContext(), players, onNoteListener);
            recyclerView.setAdapter(ladderAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        }

        @Override
        protected String doInBackground(Void... voids)
        {
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.sendGetRequest(API_URL.URL_GET_LADDER_DATA);
        }
    }
}
