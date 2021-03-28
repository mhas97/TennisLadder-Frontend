package com.example.myapplication;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import androidx.annotation.RequiresApi;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Helper class for achievement checking. Takes participant and match related data to determine
 * if an achievement requirement has been met. If so, a notification is made to notify the user
 * and a network request is made to update the database.
 */
public class AchievementHandler {

    private final TennisUser user;
    private final TennisUser winner;
    private final TennisUser loser;
    private final int adjustedElo;
    private final int initialLength;
    private final String score;
    private final Context context;

    public AchievementHandler(TennisUser user, TennisUser opponent, String score, int adjustedElo, boolean userWon, Context context) {
        this.user = user;
        if (userWon) {
            this.winner = user;
            this.loser = opponent;
        }
        else {
            this.loser = user;
            this.winner = opponent;
        }
        this.adjustedElo = adjustedElo;
        this.score = score;
        this.context = context;
        this.initialLength = user.getAchievements().size();
    }

    /**
     * If an achievement requirement has been met, first check that the winning player does
     * not already own the achievement. If not, if the user is the acquirer send a system
     * notification. Finally create a network request to update the database.
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void checkForUnlocks() {

        /* Check for each achievement */
        checkGrinding();
        checkNiceTry();
        checkStreaker();
        checkUnstoppable();
        checkBagel();
        checkGiantSlayer();

        /* If the app user has unlocked something, send a system notification. This is easily
        calculated by checking the difference between the achievement array lengths. */
        if (user.getAchievements().size() > initialLength) {
            createNotification();
        }
    }

    /* Check for 10 matches played (accounting for this match). */
    private void checkGrinding() {
        if (winner.getMatchesPlayed() == 9) {
            winner.getAchievements().add(1);
            unlockRequest(winner.getPlayerID(), 1);
        }
        if (loser.getMatchesPlayed() == 9) {
            loser.getAchievements().add(1);
            unlockRequest(loser.getPlayerID(), 1);
        }
    }

    /* Check for a first loss. */
    private void checkNiceTry() {
        if (loser.getLosses() == 0) {
            loser.getAchievements().add(2);
            unlockRequest(loser.getPlayerID(), 2);
        }
    }

    /* Check for a 3 winstreak (accounting for this match). */
    private void checkStreaker() {
        if (winner.getWinstreak() == 2 && !winner.getAchievements().contains(3)) {
            winner.getAchievements().add(3);
            unlockRequest(winner.getPlayerID(), 3);
        }
    }

    /* Check if the winner has surpassed 1750 Elo. */
    private void checkUnstoppable() {
        if (adjustedElo > 1750 && !winner.getAchievements().contains(4)) {
            winner.getAchievements().add(4);
            unlockRequest(winner.getPlayerID(), 4);
        }
    }

    /* Check if the winner has won a set 6-0 (bagel). */
    private void checkBagel() {
        if (score.contains("6/0") || score.contains("0/6")) {
            if (!winner.getAchievements().contains(5)) {
                winner.getAchievements().add(5);
                unlockRequest(winner.getPlayerID(), 5);
            }
        }
    }

    /* Check if the winner has beaten a significantly higher ranked opponent. */
    private void checkGiantSlayer() {
        if (loser.getElo() - winner.getElo() > 250 && !winner.getAchievements().contains(6)) {
            winner.getAchievements().add(6);
            unlockRequest(winner.getPlayerID(), 6);
        }
    }

    /* Create a request using the corresponding player and achievement ID */
    private void unlockRequest(int playerID, int achievementID) {
        PostAchievements req = new PostAchievements(playerID, achievementID);
        req.execute();
    }

    /* Create a notification if the app user has unlocked something. */
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createNotification() {
        /* Create notification manager and assign a channel. */
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channelID = "channel_achievement";
        String channelName = "Achievement Channel";
        int notifyID = 1;
        NotificationChannel notificationChannel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_HIGH);
        notificationManager.createNotificationChannel(notificationChannel);
        Notification notification = new Notification.Builder(context)
                .setContentTitle("Achievement Unlocked!")
                .setContentText("Check out your new trophy on your profile")
                .setSmallIcon(R.drawable.trophy)
                .setChannelId(channelID)
                .build();
        notificationManager.notify(notifyID, notification); // Display the notification.
    }

    /**
     * An asynchronous task to update the player_achievement table via the API.
     */
    private class PostAchievements extends AsyncTask<Void, Void, String> {

        HashMap<String, String> params;

        public PostAchievements(int playerID, int achievementID) {
            this.params = new HashMap<>();
            /* Parameters must be in string format. */
            params.put("achievementid", String.valueOf(achievementID));
            params.put("playerid", String.valueOf(playerID));
        }

        /**
         * APIRequest object handles HTTP communication.
         */
        @Override
        protected String doInBackground(Void... voids) {
            APIRequest req = new APIRequest();
            return req.executePostRequest(API_URL.URL_POST_ACHIEVEMENTS, params);
        }
    }
}
