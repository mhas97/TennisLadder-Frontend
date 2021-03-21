package com.example.myapplication;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ChallengesFragment extends Fragment implements ChallengesAdapter.OnNoteListener {

    private ArrayList<TennisChallenge> challenges;
    private ChallengesAdapter challengesAdapter;
    private final ChallengesAdapter.OnNoteListener onNoteListener = this;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_challenges, container, false);
        challenges = new ArrayList<>();
        setUpRecyclerView(view);
        getChallenges();
        return view;
    }

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

    @Override
    public void onResume() {
        super.onResume();
        getChallenges();
    }

    protected void setUpRecyclerView(View view) {
        RecyclerView recyclerView = view.findViewById(R.id.challenges_recyclerview);
        challengesAdapter = new ChallengesAdapter(getActivity(), challenges, onNoteListener);
        recyclerView.setAdapter(challengesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
    }

    protected void getChallenges() {
        ChallengesRequest req = new ChallengesRequest();
        req.execute();
    }

    private class ChallengesRequest extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            parseResponse(s);
            challengesAdapter.notifyDataSetChanged();
        }

        protected void parseResponse(String s) {
            try {
                challenges.clear();
                JSONObject object = new JSONObject(s);
                JSONArray arr = object.getJSONArray("challenges");
                for (int i = 0; i < arr.length(); ++i) {
                    JSONObject obj = arr.getJSONObject(i);
                    int challengeID = Integer.parseInt(obj.getString("challengeid"));
                    int opponentID = Integer.parseInt(obj.getString("opponentid"));
                    String opponentFname = obj.getString("fname");
                    String opponentLname = obj.getString("lname");
                    String date = obj.getString("date");
                    String time = obj.getString("time");
                    String location = obj.getString("location");
                    int didInitiate = Integer.parseInt(obj.getString("didinitiate"));
                    int accepted = Integer.parseInt(obj.getString("accepted"));
                    TennisChallenge challenge = new TennisChallenge(challengeID, new TennisUser(opponentID, opponentFname, opponentLname), date, time, location, didInitiate, accepted);
                    challenges.add(challenge);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.sendGetRequest(API_URL.URL_GET_CHALLENGES + MainActivity.getUser().getplayerID());
        }
    }
}
