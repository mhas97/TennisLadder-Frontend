package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

/**
 * The user profile fragment as shown in the main activity. This fragment shows user
 * information as well as the ability to view match history. This is a slightly modified
 * variant of the profile activity, where settings are available and there is no challenge
 * button (as a user cannot challenge themself).
 */
public class ProfileFragment extends Fragment {

    private ImageView imgTrophy1, imgTrophy2, imgTrophy3, imgTrophy4, imgTrophy5, imgTrophy6;

    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_profile, container, false);

        /* Identify page elements. */
        TextView txtPlayerName = view.findViewById(R.id.txtUserName);
        TextView txtPlayerClub = view.findViewById(R.id.txtUserClub);
        Button btnMatchHistory = view.findViewById(R.id.btnPlayerMatchHistory);

        imgTrophy1 = view.findViewById(R.id.imgTrophy1);
        imgTrophy2 = view.findViewById(R.id.imgTrophy2);
        imgTrophy3 = view.findViewById(R.id.imgTrophy3);
        imgTrophy4 = view.findViewById(R.id.imgTrophy4);
        imgTrophy5 = view.findViewById(R.id.imgTrophy5);
        imgTrophy6 = view.findViewById(R.id.imgTrophy6);
        TennisUser user = MainActivity.getUser();
        setUpTrophyCabinet(user);

        /* Set page elements. */
        String userName = user.getFname() + " " + user.getLname();
        txtPlayerName.setText(userName);
        txtPlayerClub.setText(user.getClubName());

        /* Navigate to the match history activity upon button press, bundle the user data for future API calls. */
        btnMatchHistory.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MatchHistoryActivity.class);
            Bundle extras = new Bundle();
            extras.putParcelable("user", user);
            intent.putExtras(extras);
            startActivity(intent);
        });
        return view;
    }

    /**
     * Create trophy cabinet helper to format the trophy cabinet.
     */
    protected void setUpTrophyCabinet(TennisUser user) {
        /* Achievement holders. */
        ImageView[] trophies = new ImageView[] {
                imgTrophy1, imgTrophy2, imgTrophy3, imgTrophy4, imgTrophy5, imgTrophy6
        };
        TrophyCabinetHelper helper = new TrophyCabinetHelper(trophies, user, getContext());
        helper.ArrangeTrophyCabinet();
    }
}
