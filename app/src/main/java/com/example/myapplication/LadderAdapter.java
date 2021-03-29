package com.example.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * Adapter to handle the ladder recycler view. This includes updating its contents
 * to account for both network requests and user queries via the search bar.
 *
 * The following tutorials on recycler view adapters and on-note actions was used during implementation:
 * https://www.youtube.com/watch?v=18VcnYN5_LM
 * https://www.youtube.com/watch?v=69C1ljfDvl0
 */
public class LadderAdapter extends RecyclerView.Adapter<LadderAdapter.LadderViewHolder> implements Filterable {

    TennisUser user;

    /* 2 lists are required for filtering via queries. */
    private final ArrayList<TennisUser> players;
    private final ArrayList<TennisUser> completePlayers;

    private final Context context;
    private final OnNoteListener onNoteListener;

    public LadderAdapter(Context context, ArrayList<TennisUser> players, OnNoteListener onNoteListener) {
        this.user = MainActivity.getUser();
        this.players = players;
        this.completePlayers = new ArrayList<>(players);    // Make a copy so they aren't pointing at the same list.
        this.context = context;
        this.onNoteListener = onNoteListener;
    }

    /**
     * Inflate the ladder_row view layout for each player on the ladder.
     */
    @NonNull
    @Override
    public LadderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.ladder_row, parent, false);
        return new LadderViewHolder(view, context, onNoteListener);
    }

    /**
     * For each player view holder, modify the view contents relative to the associated data.
     * @param holder The view holder for each player.
     * @param position Its position within the recycler view.
     */
    @Override
    public void onBindViewHolder(@NonNull LadderViewHolder holder, int position) {
        /* Identify player data. */
        int playerID = players.get(position).getPlayerID();
        String fname = players.get(position).getFname();
        String lname = players.get(position).getLname();

        /* Set view fields. */
        holder.txtRank.setText(String.valueOf(position + 1));
        holder.txtFname.setText(fname);
        holder.txtLname.setText(lname);

        /* Highlight the the app users view holder for visibility. */
        if (playerID == user.getPlayerID()) {
            holder.cvPlayer.setBackgroundColor(Color.parseColor("#C2E179"));
        }
        else {
            holder.cvPlayer.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        boolean hasAccolade = false;    // Flag to determine where an accolade icon should sit on the screen.

        /* Identify if a player is a club champion. */
        if (players.get(position).getClubChamp() == 1) {
            hasAccolade = true;
            holder.imgAccolade1.setImageResource(R.drawable.trophy);
            holder.accoladeClubChamp1 = true;   // Flag to show accolade 1 holds a trophy.

        } else {
            holder.imgAccolade1.setImageResource(0);
        }

        /* Identify if a player is on a hotstreak. */
        if (players.get(position).getHotstreak() == 1) {
            if (hasAccolade) {  // If an accolade is obtained, it must lie in position 2.
                holder.imgAccolade2.setImageResource(R.drawable.hot_streak);
                holder.accoladeHotstreak2 = true;   // Flag to show accolade 2 holds a hotstreak.
            }
            else {  // Else display in position 1.
                holder.imgAccolade1.setImageResource(R.drawable.hot_streak);
                holder.accoladeHotstreak1 = true;   // Flag to show accolade 1 holds a hotstreak.
                holder.imgAccolade2.setImageResource(0);
            }
        } else {
            holder.imgAccolade2.setImageResource(0);
        }

        holder.txtElo.setText(String.valueOf(players.get(position).getElo()));  // Display the users Elo rating.
    }

    @Override
    public int getItemCount() {
        return players.size();
    }

    @Override
    public Filter getFilter() {
        return ladderFilter;
    }

    /**
     * The asynchronous filter class performs filtering on a background thread so it
     * won't freeze the app. Results are automatically published to the UI thread.
     */
    private final Filter ladderFilter = new Filter() {
        /**
         * Pass a filter over the complete list to search for players in the ladder. If the
         * constraint is invalid or empty, add all users. Else filter each player in the
         * ladder using the constraint and append any matches to the filtered list.
         * @param constraint The user query.
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<TennisUser> filtered = new ArrayList<>();

            /* Null or empty constraints. */
            if (constraint == null || constraint.length() == 0) {
                filtered.addAll(completePlayers);
            }
            else {
                String filterSequence = constraint.toString().toLowerCase().trim();
                for (TennisUser t : completePlayers) {
                    String fullName = t.getFname() + " " + t.getLname();
                    /* If the users full name contains the constraint, add it to the filtered list. */
                    if (fullName.toLowerCase().contains(filterSequence)) {
                        filtered.add(t);
                    }
                }
            }
            /* Return the filtered list. */
            FilterResults results = new FilterResults();
            results.values = filtered;
            return results;
        }

        /**
         * Publish the results by clearing and updating the data set before notifying the adapter.
         * @param constraint The user query.
         * @param results The resulting list.
         */
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            players.clear();
            players.addAll((ArrayList) results.values);
            notifyDataSetChanged(); // Notify the adapter that the dataset has changed.
        }
    };

    /**
     * Provides a view holder for each player, containing its data and an on-note listener.
     */
    public static class LadderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private boolean accoladeHotstreak1;
        private boolean accoladeHotstreak2;
        private boolean accoladeClubChamp1;
        private final CardView cvPlayer;
        private final TextView txtRank, txtFname, txtLname, txtElo;
        private final ImageView imgAccolade1, imgAccolade2;
        private final OnNoteListener onNoteListener;
        public LadderViewHolder(@NonNull View itemView, Context context, OnNoteListener onNoteListener) {
            super(itemView);

            /* Track accolade positioning. */
            accoladeHotstreak1 = false;
            accoladeHotstreak2 = false;
            accoladeClubChamp1 = false;

            /* Identify row elements. */
            cvPlayer = itemView.findViewById(R.id.cvPlayer);
            txtRank = itemView.findViewById(R.id.txtRank);
            txtFname = itemView.findViewById(R.id.txtFname);
            txtLname = itemView.findViewById(R.id.txtLname);
            imgAccolade1 = itemView.findViewById(R.id.imgAccolade1);
            imgAccolade2 = itemView.findViewById(R.id.imgAccolade2);

            /* On-click listener to display a toast description of accolade 1. */
            imgAccolade1.setOnClickListener(v -> {
                if (accoladeClubChamp1) {   // The user has a club champion trophy in position 1.
                    Toast.makeText(context, "User is the highest rated player at their club!", Toast.LENGTH_SHORT).show();
                }
                else if (accoladeHotstreak1) {  // The user has a hotstreak in position 1.
                    Toast.makeText(context, "User has won 3 or more games in a row and is on a hotstreak!", Toast.LENGTH_SHORT).show();
                }
            });

            /* On-click listener to display a description of accolade 2. */
            imgAccolade2.setOnClickListener(v -> {
                if (accoladeHotstreak2) {   // The user has a hotstreak in position 2.
                    Toast.makeText(context, "User has won 3 or more games in a row and is on a hotstreak!", Toast.LENGTH_SHORT).show();
                }
            });
            txtElo = itemView.findViewById(R.id.txtElo);
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