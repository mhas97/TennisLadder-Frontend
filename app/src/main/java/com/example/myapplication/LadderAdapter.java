package com.example.myapplication;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class LadderAdapter extends RecyclerView.Adapter<LadderAdapter.LadderViewHolder> {

    ArrayList<TennisUser> p;
    Context context;

    public LadderAdapter(Context c, ArrayList<TennisUser> p) {
        this.p = p;
        this.context = c;
    }
    @NonNull
    @Override
    public LadderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull LadderViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class LadderViewHolder extends RecyclerView.ViewHolder {
        public LadderViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}

