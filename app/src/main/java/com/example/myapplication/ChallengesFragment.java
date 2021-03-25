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
 * A fragment to display challenge information for a user. An adapter interface
 * is used to handle recycler view population, as well as an on-note listener to
 * handle individual challenges being tapped by the user. A recycler view is a dynamic
 * list which is ideal for displaying large sets of data. As such this is the primary
 * view holder used throughout this project.
 * (https://developer.android.com/guide/topics/ui/layout/recyclerview)
 */
public class ChallengesFragment extends Fragment implements ChallengesAdapter.OnNoteListener {

    private ArrayList<TennisChallenge> challenges;
    private ChallengesAdapter challengesAdapter;
    RecyclerView recyclerChallenges;
    private final ChallengesAdapter.OnNoteListener onNoteListener = this;
    private TextView txtWelcome;
    private TextView txtMessage;

    /**
     * Identify and set up the recycler view, and make a network request
     * to fetch challenge data.
     */
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_challenges, container, false);

        // Identify welcome message elements, these only show if
        // the user has no challenges.
        txtWelcome = view.findViewById(R.id.txtWelcome);
        txtMessage = view.findViewById(R.id.txtMessage);

        challenges = new ArrayList<>();
        setUpRecyclerView(view);
        getChallenges();
        return view;
    }

    /**
     * Upon resuming the fragment, execute a network request to check
     * for any new challenges.
     */
    @Override
    public void onResume() {
        super.onResume();
        getChallenges();
    }

    /**
     * Identify the recycler view element. An adapter is then created
     * holding a reference to the challenges list. This list is populated
     * upon completion of the network request.
     */
    protected void setUpRecyclerView(View view) {
        recyclerChallenges = view.findViewById(R.id.recyclerViewChallenges);
        challengesAdapter = new ChallengesAdapter(getContext(), challenges, onNoteListener);
        recyclerChallenges.setAdapter(challengesAdapter);
        recyclerChallenges.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerChallenges.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
    }

    /**
     * Create an object to perform an asynchronous network request to fetch
     * user challenges.
     */
    protected void getChallenges() {
        ChallengesRequest req = new ChallengesRequest();
        req.execute();
    }

    /**
     * An on-note listener is implemented to handle challenge taps. A note
     * refers to an object in the recycler view. When a note (a challenge)
     * is tapped, corresponding data is bundled an passed to the challenge viewer
     * activity.
     * @param position the position of the note tapped.
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

        @Override
        protected void onPostExecute(String s) {
            parseResponse(s);
            // Notify the adapter that the data set has been altered by the network requeest.
            challengesAdapter.notifyDataSetChanged();
        }

        /**
         * Parses challenge data and creates an array list of challenges.
         * When this process is finished, the adapter is notified that the
         * data set has been modified.
         * @param s challenge data JSON string
         */
        protected void parseResponse(String s) {
            try {
                challenges.clear(); // Clear the dataset to ensure up to date information is displayed between fragment switches.
                JSONObject object = new JSONObject(s);
                JSONArray arr = object.getJSONArray("challenges");
                for (int i = 0; i < arr.length(); ++i) {
                    JSONObject obj = arr.getJSONObject(i);
                    int challengeID = obj.getInt("challengeid");
                    int oppID = obj.getInt("opponentid");
                    String oppFname = obj.getString("fname");
                    String oppLname = obj.getString("lname");
                    int oppElo = obj.getInt("elo");
                    int oppWinstreak = obj.getInt("winstreak");
                    int oppHotstreak = obj.getInt("hotstreak");
                    int oppMatchesPlayed = obj.getInt("matchesplayed");
                    int oppWins = obj.getInt("wins");
                    int oppLosses = obj.getInt("losses");
                    int oppHighestElo = obj.getInt("highestelo");
                    int oppClubChamp = obj.getInt("clubchamp");
                    String date = obj.getString("date");
                    String time = obj.getString("time");
                    String location = obj.getString("location");
                    int didInitiate = obj.getInt("didinitiate");
                    int accepted = obj.getInt("accepted");

                    // The resulting challenge object.
                    TennisChallenge challenge = new TennisChallenge(challengeID,
                            new TennisUser(oppID, oppFname, oppLname, oppElo, oppWinstreak,
                                    oppHotstreak, oppMatchesPlayed, oppWins, oppLosses, oppHighestElo,
                                    oppClubChamp), date, time, location, didInitiate, accepted);
                    challenges.add(challenge);
                }
                // Reverse the order so the latest challenge displays first.
                Collections.reverse(challenges);
                if (challenges.size() == 0) {
                    txtWelcome.setVisibility(View.VISIBLE);
                    txtMessage.setVisibility(View.VISIBLE);
                }
                else {
                    txtWelcome.setVisibility(View.INVISIBLE);
                    txtMessage.setVisibility(View.INVISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        /**
         * Handle HTTP communication with the API via APIRequest.
         */
        @Override
        protected String doInBackground(Void... voids) {
            APIRequest req = new APIRequest();
            int playerID = MainActivity.getUser().getplayerID();
            return req.executeGetRequest(API_URL.URL_GET_CHALLENGES + playerID);
        }
    }
}
