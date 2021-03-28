package com.example.myapplication;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import androidx.appcompat.app.AlertDialog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * A helper class for formatting the trophy cabinet section of a player profile.
 */
public class TrophyCabinetHelper {

    private final ImageView[] trophies;
    private final HashMap<String, String[]> globalAchievements;
    private final TennisUser user;
    private final Context context;

    public TrophyCabinetHelper(ImageView[] trophies, TennisUser user, Context context) {
        this.trophies = trophies;
        this.user = user;
        this.context = context;

        /* Fetch global achievements hashmap. */
        this.globalAchievements = GlobalAchievements.getInstance().getAchievementList();
    }

    /**
     * Loop through the users achievement array and populate the trophy holders with any
     * unlocks. To ensure no duplicates, double check against an array of seen achievements.
     * User achievements are an array of ID's, these are fed to the global achievements hashmap
     * generated during login to fetch the corresponding achievement data.
     */
    public void ArrangeTrophyCabinet() {
        /* Obtain the list of user achievements and create the seen array. */
        ArrayList<Integer> userAchievements = user.getAchievements();
        ArrayList<Integer> seen = new ArrayList<>();

        /* For each user achievement, populate an image view holder and create an on-click
        popup to display its information. */
        for (int i = 0; i < userAchievements.size(); i++) {
            int achievementID = userAchievements.get(i);
            if (seen.contains(achievementID)) {
                break;
            }
            seen.add(achievementID);
            switch (achievementID) {
                case 1:
                    /* Grinding achievement. */
                    trophies[i].setImageResource(R.drawable.grinding);
                    setDialogue(i, achievementID);
                    break;
                case 2:
                    /* Nice try achievement. */
                    trophies[i].setImageResource(R.drawable.nice_try);
                    setDialogue(i, achievementID);
                    break;
                case 3:
                    /* Streaker achievement. */
                    trophies[i].setImageResource(R.drawable.hot_streak);
                    setDialogue(i, achievementID);
                    break;
                case 4:
                    /* Unstoppable achievement. */
                    trophies[i].setImageResource(R.drawable.unstoppable);
                    setDialogue(i, achievementID);
                    break;
                case 5:
                    /* Bagel achievement. */
                    trophies[i].setImageResource(R.drawable.bagel);
                    setDialogue(i, achievementID);
                    break;
                case 6:
                    trophies[i].setImageResource(R.drawable.giant_slayer);
                    setDialogue(i, achievementID);
                    /* Giant slayer achievement. */
                    break;
            }
            // Make the trophy holder visible.
            trophies[i].setVisibility(View.VISIBLE);
        }
    }

    /**
     * Set the dialogue for a trophy spot. To obtain the corresponding achievement data,
     * feed the achievementID to the hashmap and retrieve its name and description values.
     */
    private void setDialogue(int index, int achievementID) {
        trophies[index].setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            String id = String.valueOf(achievementID);
            builder.setTitle(Objects.requireNonNull(globalAchievements.get(id))[0]);
            builder.setMessage(Objects.requireNonNull(globalAchievements.get(id))[1]);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }
}
