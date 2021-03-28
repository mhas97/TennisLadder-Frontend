package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.ArrayList;

/**
 * This class implements the Parcelable interface, allowing user objects
 * to be passed between pages. This is better practice than having static
 * global user variables and is useful for displaying user info and creating
 * challenges.
 *
 * https://developer.android.com/reference/android/os/Parcelable
 */
public class TennisUser implements Parcelable {

    private int playerID;
    private String email;
    private String contactNo;
    private String fname;
    private String lname;
    private String clubName;
    private int elo;
    private int winstreak;
    private int hotstreak;
    private int matchesPlayed;
    private int wins;
    private int losses;
    private int highestElo;
    private int clubChamp;
    private ArrayList<Integer> achievements;

    /* App user data to be passed around the app. */
    public TennisUser(int playerID, String email, String contactNo, String fname, String lname, String clubName, int elo, int winstreak, int hotstreak, int matchesPlayed, int wins, int losses, int highestElo, int clubChamp, ArrayList<Integer> achievements) {
        this.playerID = playerID;
        this.email = email;
        this.contactNo = contactNo;
        this.fname = fname;
        this.lname = lname;
        this.clubName = clubName;
        this.elo = elo;
        this.winstreak = winstreak;
        this.hotstreak = hotstreak;
        this.matchesPlayed = matchesPlayed;
        this.wins = wins;
        this.losses = losses;
        this.highestElo = highestElo;
        this.clubChamp = clubChamp;
        this.achievements = achievements;
    }

    /* Challenges data, required for reporting a result. */
    public TennisUser(int playerID, String fname, String lname, int elo, int winstreak, int hotstreak, int matchesPlayed, int wins, int losses, int highestElo, int clubChamp, ArrayList<Integer> achievements) {
        this.playerID = playerID;
        this.fname = fname;
        this.lname = lname;
        this.elo = elo;
        this.winstreak = winstreak;
        this.hotstreak = hotstreak;
        this.matchesPlayed = matchesPlayed;
        this.wins = wins;
        this.losses = losses;
        this.highestElo = highestElo;
        this.clubChamp = clubChamp;
        this.achievements = achievements;
    }

    /* Ladder and profile data, required for creating a challenge. */
    public TennisUser(int playerID, String fname, String lname, String clubName, int elo, int hotstreak, int matchesPlayed, int wins, int losses, int highestElo, int clubChamp, ArrayList<Integer> achievements) {
        this.playerID = playerID;
        this.fname = fname;
        this.lname = lname;
        this.clubName = clubName;
        this.elo = elo;
        this.hotstreak = hotstreak;
        this.matchesPlayed = matchesPlayed;
        this.wins = wins;
        this.losses = losses;
        this.highestElo = highestElo;
        this.clubChamp = clubChamp;
        this.achievements = achievements;
    }

    /* Match history data */
    public TennisUser(int playerID, String fname, String lname) {
        this.playerID = playerID;
        this.fname = fname;
        this.lname = lname;
    }

    public int getPlayerID() { return playerID; }

    public String getFname() { return fname; }

    public String getLname() { return lname; }

    public String getClubName() { return clubName; }

    public int getElo() { return elo; }

    public int getWinstreak() { return winstreak; }

    public int getHotstreak() { return hotstreak; }

    public int getMatchesPlayed() { return matchesPlayed; }

    public int getLosses() { return losses; }

    public int getHighestElo() { return highestElo; }

    public int getClubChamp() { return clubChamp; }

    public ArrayList<Integer> getAchievements() { return achievements; }

    /* Parcel methods. */

    protected TennisUser(Parcel in) {
        playerID = in.readInt();
        email = in.readString();
        contactNo = in.readString();
        fname = in.readString();
        lname = in.readString();
        clubName = in.readString();
        elo = in.readInt();
        winstreak = in.readInt();
        hotstreak = in.readInt();
        matchesPlayed = in.readInt();
        wins = in.readInt();
        losses = in.readInt();
        highestElo = in.readInt();
        clubChamp = in.readInt();
        achievements = in.readArrayList(int.class.getClassLoader());
    }

    public static final Creator<TennisUser> CREATOR = new Creator<TennisUser>() {
        @Override
        public TennisUser createFromParcel(Parcel in) {
            return new TennisUser(in);
        }

        @Override
        public TennisUser[] newArray(int size) {
            return new TennisUser[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(playerID);
        dest.writeString(email);
        dest.writeString(contactNo);
        dest.writeString(fname);
        dest.writeString(lname);
        dest.writeString(clubName);
        dest.writeInt(elo);
        dest.writeInt(winstreak);
        dest.writeInt(hotstreak);
        dest.writeInt(matchesPlayed);
        dest.writeInt(wins);
        dest.writeInt(losses);
        dest.writeInt(highestElo);
        dest.writeInt(clubChamp);
        dest.writeList(achievements);
    }
}
