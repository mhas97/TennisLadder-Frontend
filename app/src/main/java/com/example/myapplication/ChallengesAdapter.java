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

public class ChallengesAdapter extends RecyclerView.Adapter<ChallengesAdapter.ChallengeViewHolder> {

    private final ArrayList<TennisChallenge> c;
    private final Context context;
    private final OnNoteListener onNoteListener;

    public ChallengesAdapter(Context context, ArrayList<TennisChallenge> challenges, OnNoteListener onNoteListener) {
        this.context = context;
        this.c = challenges;
        this.onNoteListener = onNoteListener;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.challenges_row, parent, false);
        return new ChallengeViewHolder(view, onNoteListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        if (c.get(position).getAccepted() == 1) {
            holder.status.setText("It's On!");
        }
        else if (c.get(position).getDidInitiate() == 0) {    // -1 flag indicates a match has not yet been played.
            holder.status.setText("Incoming Challenge");
        }
        else {
            holder.status.setText("Outgoing Challenge");
        }
        String opponentName = c.get(position).getOpponent().getFname() + " " + c.get(position).getOpponent().getLname();
        holder.opponent.setText(opponentName);
    }

    @Override
    public int getItemCount() {
        return c.size();
    }

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final CardView cvResult;
        private final TextView status, opponent;
        private final OnNoteListener onNoteListener;

        public ChallengeViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            cvResult = itemView.findViewById(R.id.cvResult);
            status = itemView.findViewById(R.id.txtChallengeStatus);
            opponent = itemView.findViewById(R.id.txtOpponent);
            this.onNoteListener = onNoteListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onNoteListener.onNoteClick(getAdapterPosition());
        }
    }

    public interface OnNoteListener {
        void onNoteClick(int position);
    }
}
