package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * Handles the recycler view holder by populating it with a card view for each challenge.
 */
public class ChallengesAdapter extends RecyclerView.Adapter<ChallengesAdapter.ChallengeViewHolder> {

    private final ArrayList<TennisChallenge> challenges;
    private final Context context;
    private final OnNoteListener onNoteListener;

    public ChallengesAdapter(Context context, ArrayList<TennisChallenge> challenges, OnNoteListener onNoteListener) {
        this.context = context;
        this.challenges = challenges;
        this.onNoteListener = onNoteListener;
    }

    /**
     * Inflate the challenges_row layout for each challenge.
     */
    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.challenges_row, parent, false);
        return new ChallengeViewHolder(view, onNoteListener);
    }

    /**
     * For each challenge view holder, modify it's contents relative to its associated data.
     */
    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        /* Accepted challenge. */
        if (challenges.get(position).getAccepted() == 1) {
            holder.txtStatus.setText(R.string.accepted_challenge);
        }
        /* Outgoing challenge. */
        else if (challenges.get(position).getDidInitiate() == 0) {
            holder.txtStatus.setText(R.string.incoming_challenge);
        }
        /* Incoming challenge. */
        else {
            holder.txtStatus.setText(R.string.outgoing_challenge);
        }
        /* Display opponent data. */
        TennisUser opponent = challenges.get(position).getOpponent();
        String opponentName = opponent.getFname() + " " + opponent.getLname();
        holder.txtOpponent.setText(opponentName);
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    /**
     * Provides each TennisChallenge object with a view holder, containing it's data.
     * Also provides an on-note listener for each challenge allowing for user interaction.
     */
    public static class ChallengeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView txtStatus, txtOpponent;
        private final OnNoteListener onNoteListener;

        /* Identify holder elements and set an on-note listener for each challenge. */
        public ChallengeViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            txtStatus = itemView.findViewById(R.id.txtMatchStatus);
            txtOpponent = itemView.findViewById(R.id.txtMatchOpponent);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());   // Get the position of the tap.
        }
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}
