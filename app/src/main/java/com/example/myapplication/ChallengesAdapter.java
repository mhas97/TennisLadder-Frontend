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

public class ChallengesAdapter extends RecyclerView.Adapter<ChallengesAdapter.ChallengeViewHolder> {

    private final ArrayList<TennisChallenge> c;
    private final Context context;

    public ChallengesAdapter(Context context, ArrayList<TennisChallenge> challenges) {
        this.context = context;
        this.c = challenges;
    }

    @NonNull
    @Override
    public ChallengeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.challenges_row, parent, false);
        return new ChallengeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChallengeViewHolder holder, int position) {
        if (c.get(position).getDidInitiate() == 0) {    // -1 flag indicates a match has not yet been played.
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

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {

        private final CardView cvResult;
        private final TextView status, opponent;

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            cvResult = itemView.findViewById(R.id.cvResult);
            status = itemView.findViewById(R.id.txtChallengeStatus);
            opponent = itemView.findViewById(R.id.txtOpponent);
        }
    }
}
