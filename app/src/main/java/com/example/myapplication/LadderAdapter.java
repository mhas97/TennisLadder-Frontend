package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class LadderAdapter extends RecyclerView.Adapter<LadderAdapter.LadderViewHolder> implements Filterable {

    private final ArrayList<TennisUser> p;
    private final ArrayList<TennisUser> completeP;
    private final Context context;

    public LadderAdapter(Context context, ArrayList<TennisUser> p) {
        this.p = p;
        // Make a copy so they aren't pointing at the same list.
        this.completeP = new ArrayList<TennisUser>(p);
        this.context = context;
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
        holder.rank.setText(String.valueOf(position + 1));
        holder.fname.setText(p.get(position).getFname());
        holder.lname.setText(p.get(position).getLname());
        if (p.get(position).getHotstreak() == 1)
        {
            holder.hotstreak.setImageResource(R.drawable.hot_streak);
        } else {
            holder.hotstreak.setImageResource(0);
        }
    }

    @Override
    public int getItemCount() {
        return p.size();
    }

    @Override
    public Filter getFilter() {
        return ladderFilter;
    }

    /**
     * Filter class performs filtering on the background thread, won't freeze the app.
     * Automatically published to UI thread.
     */
    private final Filter ladderFilter = new Filter() {

        /**
         * Pass a filter over the complete list to search for players in the ladder.
         * If the constraint is invalid or empty, add all users.
         * Else, filter each player in the ladder via the constraint and append
         * any matches to the filtered list.
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<TennisUser> filtered = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filtered.addAll(completeP);
            } else {
                String filterSequence = constraint.toString().toLowerCase().trim(); // Concatenate the full name.
                for (TennisUser t : completeP) {
                    String fullName = t.getFname() + " " + t.getLname();
                    if (fullName.toLowerCase().contains(filterSequence)) {
                        filtered.add(t);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filtered;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            p.clear();
            p.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };

    public static class LadderViewHolder extends RecyclerView.ViewHolder {

        private final TextView rank, fname, lname;
        private final ImageView hotstreak;

        public LadderViewHolder(@NonNull View itemView) {
            super(itemView);
            rank = itemView.findViewById(R.id.rank_text);
            fname = itemView.findViewById(R.id.fname_text);
            lname = itemView.findViewById(R.id.lname_text);
            hotstreak = itemView.findViewById(R.id.img_hotstreak);
        }
    }
}

