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

public class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MatchViewHolder> {

    private ArrayList<TennisChallenge> matches;
    private Context context;

    public MatchesAdapter(ArrayList<TennisChallenge> matches, Context context) {
        this.matches = matches;
        this.context = context;
    }

    @NonNull
    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.match_history_row, parent, false);
        return new MatchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesAdapter.MatchViewHolder holder, int position) {
        if (matches.get(position).getDidWin() == 1) {
            holder.status.setText("Victory");
            holder.cvMatch.setBackgroundColor(Color.parseColor("#FF8EF868"));
        }
        else {
            holder.status.setText("Defeat");
            holder.cvMatch.setBackgroundColor(Color.parseColor("#FFF44336"));
        }
        TennisUser opponent = matches.get(position).getOpponent();
        holder.opponent.setText(opponent.getFname() + " " + opponent.getLname());
        holder.score.setText(matches.get(position).getScore());
        holder.date.setText(matches.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder {

        private CardView cvMatch;
        private TextView status, opponent, score, date;

        public MatchViewHolder(@NonNull View itemView) {
            super(itemView);
            cvMatch = itemView.findViewById(R.id.cvMatch);
            status = itemView.findViewById(R.id.txtMatchStatus);
            opponent = itemView.findViewById(R.id.txtMatchOpponent);
            score = itemView.findViewById(R.id.txtMatchScore);
            date = itemView.findViewById(R.id.txtMatchDate);
        }
    }
}
