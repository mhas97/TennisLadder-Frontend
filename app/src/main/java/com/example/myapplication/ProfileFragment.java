package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

/**
 * The user profile fragment as shown in the main activity. This fragment
 * shows user information as well as the ability to view match history.
 * This is a slightly modified variant of the profile activity, where settings
 * are available and there is no challenge button (as a user cannot challenge
 * themself).
 */
public class ProfileFragment extends Fragment {

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_profile, container, false);

        // Identify page elements
        TextView txtPlayerName = view.findViewById(R.id.txtUserName);
        TextView txtPlayerClub = view.findViewById(R.id.txtUserClub);
        Button btnMatchHistory = view.findViewById(R.id.btnPlayerMatchHistory);

        // Set page elements
        TennisUser user = MainActivity.getUser();
        String userName = user.getFname() + " " + user.getLname();
        txtPlayerName.setText(userName);
        txtPlayerClub.setText(user.getClubName());

        // Navigate to the match history activity upon button press, bundle the user
        // As this data is required for future API calls.
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
