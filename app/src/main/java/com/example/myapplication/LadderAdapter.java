package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class LadderAdapter extends RecyclerView.Adapter<LadderAdapter.LadderViewHolder> {

    int rankCount = 1;

    ArrayList<TennisUser> p;
    Context context;

    public LadderAdapter(Context c, ArrayList<TennisUser> p) {
        this.p = p;
        this.context = c;
    }

    @NonNull
    @Override
    public LadderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ladder_row, parent, false);
        return new LadderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LadderViewHolder holder, int position) {
        holder.rank.setText(String.valueOf(rankCount));
        holder.fname.setText(p.get(position).getFname());
        holder.lname.setText(p.get(position).getLname());
        holder.elo.setText(String.valueOf((p.get(position).getElo())));
        ++rankCount;
    }

    @Override
    public int getItemCount() {
        return p.size();
    }

    public class LadderViewHolder extends RecyclerView.ViewHolder {

        TextView rank, fname, lname, elo;

        public LadderViewHolder(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.rank_text);
            fname = itemView.findViewById(R.id.fname_text);
            lname = itemView.findViewById(R.id.lname_text);
            elo = itemView.findViewById(R.id.elo_text);
        }
    }
}

