package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
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
        holder.date.setText(c.get(position).getDate());
        holder.time.setText(c.get(position).getTime());
        holder.opponent.setText(c.get(position).getOpponent().getFname());
    }

    @Override
    public int getItemCount() {
        return c.size();
    }

    public static class ChallengeViewHolder extends RecyclerView.ViewHolder {

        private final TextView date, time, opponent;

        public ChallengeViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.txtDate);
            time = itemView.findViewById(R.id.txtTime);
            opponent = itemView.findViewById(R.id.txtOpponent);
        }
    }
}
