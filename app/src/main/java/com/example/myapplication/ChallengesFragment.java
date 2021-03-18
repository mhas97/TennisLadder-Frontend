package com.example.myapplication;

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

public class ChallengesFragment extends Fragment {

    private RecyclerView recyclerView;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_challenges, container, false);
        recyclerView = view.findViewById(R.id.challenges_recyclerview);
        getChallenges();
        return view;
    }

    protected void getChallenges() {
        ChallengesRequest req = new ChallengesRequest();
        req.execute();
    }

    private class ChallengesRequest extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ArrayList<TennisChallenge> c = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(s);
                JSONArray arr = object.getJSONArray("challenges");
                for (int i = 0; i < arr.length(); ++i) {
                    JSONObject obj = arr.getJSONObject(i);
                    int challengeID = Integer.parseInt(obj.getString("challengeid"));
                    int didInitiate = Integer.parseInt(obj.getString("didinitiate"));
                    int opponentID = Integer.parseInt(obj.getString("opponentid"));
                    String opponentFname = obj.getString("fname");
                    String opponentLname = obj.getString("lname");
                    String date = obj.getString("date");
                    String time = obj.getString("time");
                    String location = obj.getString("location");
                    TennisChallenge challenge = new TennisChallenge(challengeID, new TennisUser(opponentID, opponentFname, opponentLname), date, time, location, didInitiate);
                    c.add(challenge);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            setupRecyclerView(c);
        }

        protected void setupRecyclerView(ArrayList<TennisChallenge> challenges) {
            ChallengesAdapter challengesAdapter = new ChallengesAdapter(getActivity().getApplicationContext(), challenges);
            recyclerView.setAdapter(challengesAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.sendGetRequest(API_URL.URL_GET_CHALLENGES + MainActivity.getUser().getplayerID());
        }
    }
}