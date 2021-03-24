package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_profile, container, false);

        Button btnMatchHistory = view.findViewById(R.id.btnPlayerMatchHistory);

        TextView txtPlayerName = view.findViewById(R.id.txtUserName);
        TextView txtPlayerClub = view.findViewById(R.id.txtUserClub);
        TennisUser user = MainActivity.getUser();
        String userName = user.getFname() + " " + user.getLname();
        txtPlayerName.setText(userName);
        txtPlayerClub.setText(user.getClubName());

        btnMatchHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MatchHistoryActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable("user", user);
            intent.putExtras(extras);
            startActivity(intent);
        });

        return view;
    }
}
