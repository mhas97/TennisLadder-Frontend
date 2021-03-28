package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Fragment to display challenge information for a user. An adapter interface is used
 * to handle recycler view population, as well as an on-note listener to handle individual
 * challenge interaction. A recycler view is a dynamic list which is ideal for displaying
 * large sets of data. As such this is the primary view holder used throughout this project.
 * (https://developer.android.com/guide/topics/ui/layout/recyclerview)
 */
public class ChallengesFragment extends Fragment implements ChallengesAdapter.OnNoteListener {

    private ArrayList<TennisChallenge> challenges;
    private ChallengesAdapter challengesAdapter;
    private final ChallengesAdapter.OnNoteListener onNoteListener = this;
    private TextView txtWelcome;
    private TextView txtMessage;

    /**
     * Identify and set up the recycler view. Make a network request to fetch challenge data.
     */
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_challenges, container, false);

        /* Identify welcome message elements, these only show if the user has no challenges. */
        txtWelcome = view.findViewById(R.id.txtWelcome);
        txtMessage = view.findViewById(R.id.txtMessage);

        /* Set up the recycler view and fetch challenge data. */
        challenges = new ArrayList<>();
        setUpRecyclerView(view);
        getChallenges();
        return view;
    }

    /**
     * Upon resuming the fragment, execute a network request to check for new challenges.
     */
    @Override
    public void onResume() {
        super.onResume();
        getChallenges();
    }

    /**
     * Identify the recycler view element. An adapter is then created holding a reference
     * to the challenges list. The list is populated upon completion of the network request.
     */
    private void setUpRecyclerView(View view) {
        RecyclerView recyclerChallenges = view.findViewById(R.id.recyclerViewChallenges);
        challengesAdapter = new ChallengesAdapter(getContext(), challenges, onNoteListener);
        recyclerChallenges.setAdapter(challengesAdapter);
        recyclerChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerChallenges.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    /**
     * Create an object to perform an asynchronous network request to fetch user challenges.
     */
    private void getChallenges() {
        ChallengesRequest req = new ChallengesRequest();
        req.execute();
    }

    /**
     * An on-note listener is implemented to handle challenge taps. A note refers to an
     * object in the recycler view. When a note is tapped, corresponding data is bundled
     * and passed to the challenge viewer activity.
     * @param position the position of the tapped note.
     */
    @Override
    public void onNoteClick(int position) {
        TennisChallenge tappedChallenge = challenges.get(position);
        Intent intent = new Intent(getActivity(), ChallengeViewerActivity.class);
        Bundle extras = new Bundle();
        extras.putParcelable("user", MainActivity.getUser());
        extras.putParcelable("challenge", tappedChallenge);
        intent.putExtras(extras);
        startActivity(intent);
    }

    /**
     * An asynchronous task used to fetch challenge data for the given user.
     */
    private class ChallengesRequest extends AsyncTask<Void, Void, String> {

        /**
         * Notify the adapter that the data set has been altered by the network request.
         */
        @Override
        protected void onPostExecute(String s) {
            parseResponse(s);
            challengesAdapter.notifyDataSetChanged();
        }

        /**
         * Parse challenge data and create an array list of challenges. When this process
         * is finished, the adapter is notified that the data set has been modified.
         * @param s challenge data JSON string.
         */
        private void parseResponse(String s) {
            try {
                /* Clear the dataset to ensure up to date information is displayed between fragment switches */
                challenges.clear();
                JSONObject object = new JSONObject(s);
                JSONArray playersArray = object.getJSONArray("challenges");
                for (int i = 0; i < playersArray.length(); ++i) {

                    /* Parse response. */
                    JSONObject playerObject = playersArray.getJSONObject(i);
                    int challengeID = playerObject.getInt("challengeid");
                    int oppID = playerObject.getInt("opponentid");
                    String oppFname = playerObject.getString("fname");
                    String oppLname = playerObject.getString("lname");
                    int oppElo = playerObject.getInt("elo");
                    int oppWinstreak = playerObject.getInt("winstreak");
                    int oppHotstreak = playerObject.getInt("hotstreak");
                    int oppMatchesPlayed = playerObject.getInt("matchesplayed");
                    int oppWins = playerObject.getInt("wins");
                    int oppLosses = playerObject.getInt("losses");
                    int oppHighestElo = playerObject.getInt("highestelo");
                    int oppClubChamp = playerObject.getInt("clubchamp");
                    String date = playerObject.getString("date");
                    String time = playerObject.getString("time");
                    String location = playerObject.getString("location");
                    int didInitiate = playerObject.getInt("didinitiate");
                    int accepted = playerObject.getInt("accepted");

                    /* Fetch the users achievement data. */
                    ArrayList<Integer> achieved = parseAchieved(playerObject);

                    /* Create the resulting challenge object. */
                    TennisChallenge challenge = new TennisChallenge(challengeID,
                            new TennisUser(oppID, oppFname, oppLname, oppElo, oppWinstreak,
                                    oppHotstreak, oppMatchesPlayed, oppWins, oppLosses, oppHighestElo,
                                    oppClubChamp, achieved), date, time, location, didInitiate, accepted);
                    challenges.add(challenge);
                }
                Collections.reverse(challenges);    // Reverse the order so the latest challenge displays first.
                displayWelcomeMessage();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /* Parse the users achievements. */
        private ArrayList<Integer> parseAchieved(JSONObject object) {
            try {
                ArrayList<Integer> achieved = new ArrayList<>();
                /* Parse the achievements array. */
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
         * Display the welcome message if there are no challenges.
         */
        private void displayWelcomeMessage() {
            if (challenges.size() == 0) {
                txtWelcome.setVisibility(View.VISIBLE);
                txtMessage.setVisibility(View.VISIBLE);
            }
            else {
                txtWelcome.setVisibility(View.INVISIBLE);
                txtMessage.setVisibility(View.INVISIBLE);
            }
        }

        /**
         * Handle HTTP communication with the API via APIRequest.
         */
        @Override
        protected String doInBackground(Void... voids) {
            APIRequest req = new APIRequest();
            int playerID = MainActivity.getUser().getPlayerID();
            return req.executeGetRequest(API_URL.URL_GET_CHALLENGES + playerID);
        }
    }
}
