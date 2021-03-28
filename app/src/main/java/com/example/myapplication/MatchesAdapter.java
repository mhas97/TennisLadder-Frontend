package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * Handles the recycler view holder by populating it with a card view for each completed match.
 *
 * The following tutorials on recycler view adapters and on-note actions was used during implementation:
 * https://www.youtube.com/watch?v=18VcnYN5_LM
 * https://www.youtube.com/watch?v=69C1ljfDvl0&t=507s
 */
public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MatchViewHolder> {

    private final ArrayList<TennisChallenge> matches;
    private final Context context;

    public MatchesAdapter(ArrayList<TennisChallenge> matches, Context context) {
        this.matches = matches;
        this.context = context;
    }

    /**
     * Inflate the matches_row layout for each challenge.
     */
    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.match_history_row, parent, false);
        return new MatchViewHolder(view);
    }

    /**
     * For each match view holder, modify it's contents relative to its associated data.
     */
    @Override
    public void onBindViewHolder(@NonNull MatchesAdapter.MatchViewHolder holder, int position) {
        /* Victory. */
        if (matches.get(position).getDidWin() == 1) {
            holder.txtStatus.setText(R.string.victory);
            holder.cvMatch.setBackgroundColor(Color.parseColor("#C2E179"));
        }
        /* Defeat. */
        else {
            holder.txtStatus.setText(R.string.defeat);
            holder.cvMatch.setBackgroundColor(Color.parseColor("#E35045"));
        }
        /* Populate with user data. */
        TennisUser opponent = matches.get(position).getOpponent();
        holder.txtOpponent.setText(opponent.getFname() + " " + opponent.getLname());
        holder.txtScore.setText(matches.get(position).getScore());
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    /**
     * Each match is contained within a view holder, containing data as well as an on-note listener.
     */
    public static class MatchViewHolder extends RecyclerView.ViewHolder {

        private final CardView cvMatch;
        private final TextView txtStatus, txtOpponent, txtScore;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            /* Identify page elements. */
            cvMatch = itemView.findViewById(R.id.cvMatch);
            txtStatus = itemView.findViewById(R.id.txtMatchStatus);
            txtOpponent = itemView.findViewById(R.id.txtMatchOpponent);
            txtScore = itemView.findViewById(R.id.txtMatchScore);
        }
    }
}
