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
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

/**
 * An adapter to handle the ladder recycler view. This includes updating its
 * contents to account for both network requests and user queries via the search bar.
 */
public class LadderAdapter extends RecyclerView.Adapter<LadderAdapter.LadderViewHolder> implements Filterable {

    TennisUser user;
    // 2 lists are required for filtering via queries.
    private final ArrayList<TennisUser> players;
    private final ArrayList<TennisUser> completePlayers;

    private final Context context;
    private final OnNoteListener onNoteListener;

    public LadderAdapter(Context context, ArrayList<TennisUser> players, OnNoteListener onNoteListener) {
        this.user = MainActivity.getUser();
        this.players = players;
        // Make a copy so they aren't pointing at the same list.
        this.completePlayers = new ArrayList<>(players);
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
        return new LadderViewHolder(view, onNoteListener);
    }

    /**
     * For each player view holder, modify the view contents relative to the associated data.
     * @param holder The view holder for each player.
     * @param position Its position within the recycler view.
     */
    @Override
    public void onBindViewHolder(@NonNull LadderViewHolder holder, int position) {
        // Identify relevant data.
        int playerID = players.get(position).getplayerID();
        String fname = players.get(position).getFname();
        String lname = players.get(position).getLname();

        // Set view fields.
        holder.txtRank.setText(String.valueOf(position + 1));
        holder.txtFname.setText(fname);
        holder.txtLname.setText(lname);

        // Highlight the the app users view for visibility.
        if (playerID == user.getplayerID()) {
            holder.cvPlayer.setBackgroundColor(Color.parseColor("#C2E179"));
        }
        else {
            holder.cvPlayer.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        // Identify players on a hotstreak;
        if (players.get(position).getHotstreak() == 1)
        {
            holder.imgHotstreak.setImageResource(R.drawable.hot_streak);
        } else {
            holder.imgHotstreak.setImageResource(0);
        }
        holder.txtElo.setText(String.valueOf(players.get(position).getElo()));
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
     * The asynchronous filter class performs filtering on a background thread so won't
     * freeze the app. Results are automatically published to UI thread.
     */
    private final Filter ladderFilter = new Filter() {

        /**
         * Pass a filter over the complete list to search for players in the ladder.
         * If the constraint is invalid or empty, add all users, else filter each
         * player in the ladder using the constraint and append any matches to the
         * filtered list.
         * @param constraint The user query.
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<TennisUser> filtered = new ArrayList<>();
            // Null or empty constraints.
            if (constraint == null || constraint.length() == 0) {
                filtered.addAll(completePlayers);
            }
            else {
                String filterSequence = constraint.toString().toLowerCase().trim();
                for (TennisUser t : completePlayers) {
                    String fullName = t.getFname() + " " + t.getLname();
                    // If the users full name contains the constraint, add it to the filtered list.
                    if (fullName.toLowerCase().contains(filterSequence)) {
                        filtered.add(t);
                    }
                }
            }
            // Return the filtered list.
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
            notifyDataSetChanged();
        }
    };

    /**
     * Each player is contained within a view holder, containing data
     * as well as an on-note listener.
     */
    public static class LadderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final CardView cvPlayer;
        private final TextView txtRank, txtFname, txtLname, txtElo;
        private final ImageView imgHotstreak;
        private final OnNoteListener onNoteListener;
        public LadderViewHolder(@NonNull View itemView, OnNoteListener onNoteListener) {
            super(itemView);
            // Identify the elements.
            cvPlayer = itemView.findViewById(R.id.cvPlayer);
            txtRank = itemView.findViewById(R.id.txtRank);
            txtFname = itemView.findViewById(R.id.txtFname);
            txtLname = itemView.findViewById(R.id.txtLname);
            imgHotstreak = itemView.findViewById(R.id.imgHotstreak);
            txtElo = itemView.findViewById(R.id.txtElo);
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