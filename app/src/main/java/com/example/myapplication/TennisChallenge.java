package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Similarly to TennisUser, this class implements the Parcelable interface
 * allowing challenge objects to be passed between pages.
 */
public class TennisChallenge implements Parcelable {
    private int challengeID;
    private TennisUser opponent;
    private String date;
    private String time;
    private String location;
    private String score;
    private int didWin;
    private int didInitiate;
    private int accepted;

    /* For displaying challenge data on the active challenges page. */
    public TennisChallenge(int challengeID, TennisUser opponent, String date, String time, String location, int didInitiate, int accepted) {
        this.challengeID = challengeID;
        this.opponent = opponent;
        this.date = date;
        this.time = time;
        this.location = location;
        this.didInitiate = didInitiate;
        this.accepted = accepted;
    }

    /* For displaying challenge data on the match history page. */
    public TennisChallenge(int challengeID, TennisUser opponent, String date, int didWin, String score) {
        this.challengeID = challengeID;
        this.opponent = opponent;
        this.date = date;
        this.didWin = didWin;
        this.score = score;
    }

    public int getChallengeID() { return challengeID; }

    public TennisUser getOpponent() { return opponent; }

    public String getDate() { return date; }

    public String getTime() { return time; }

    public String getLocation() { return location; }

    public String getScore() { return score; }

    public int getDidWin() {return didWin; }

    public int getDidInitiate() { return didInitiate; }

    public int getAccepted() { return accepted; }

    /* Parcel methods. */

    protected TennisChallenge(Parcel in) {
        challengeID = in.readInt();
        opponent = in.readParcelable(TennisUser.class.getClassLoader());
        date = in.readString();
        time = in.readString();
        location = in.readString();
        score = in.readString();
        didWin = in.readInt();
        didInitiate = in.readInt();
        accepted = in.readInt();
    }

    public static final Creator<TennisChallenge> CREATOR = new Creator<TennisChallenge>() {
        @Override
        public TennisChallenge createFromParcel(Parcel in) {
            return new TennisChallenge(in);
        }

        @Override
        public TennisChallenge[] newArray(int size) {
            return new TennisChallenge[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(challengeID);
        dest.writeParcelable(opponent, flags);
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(location);
        dest.writeString(score);
        dest.writeInt(didWin);
        dest.writeInt(didInitiate);
        dest.writeInt(accepted);
    }
}
