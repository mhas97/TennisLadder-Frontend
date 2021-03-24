package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * Handles the recycler view holder by populating it with a card view for each
 * challenge.
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

    //Inflate the challenges_row layout for each challenge.
    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.challenges_row, parent, false);
        return new ChallengeViewHolder(view, onNoteListener);
    }

    // For each challenge view holder, modify it's contents relative to its associated data.
    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        // Accepted challenge.
        if (challenges.get(position).getAccepted() == 1) {
            holder.txtStatus.setText("It's On!");
        }
        // Initiated challenge.
        else if (challenges.get(position).getDidInitiate() == 0) {    // -1 flag indicates a match has not yet been played.
            holder.txtStatus.setText("Incoming Challenge");
        }
        // Recieved challenge.
        else {
            holder.txtStatus.setText("Outgoing Challenge");
        }
        // Display opponent data.
        String opponentName = challenges.get(position).getOpponent().getFname() + " " + challenges.get(position).getOpponent().getLname();
        holder.txtOpponent.setText(opponentName);
    }

    @Override
    public int getItemCount() {
        return challenges.size();
    }

    /**
     * Each challenge is contained within a view holder, containing data
     * as well as an on-note listener.
     */
    public static class ChallengeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView txtStatus, txtOpponent;
        private final OnNoteListener onNoteListener;

        public ChallengeViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            // Identify elements
            txtStatus = itemView.findViewById(R.id.txtMatchStatus);
            txtOpponent = itemView.findViewById(R.id.txtMatchOpponent);

            // Assign an on-note listener
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        /**
         * Get the the position of the tap.
         */
        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}
