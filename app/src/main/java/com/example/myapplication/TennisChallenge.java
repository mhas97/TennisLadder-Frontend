package com.example.myapplication;

public class TennisChallenge {
    private int challengeID;
    private TennisUser opponent;
    private String date;
    private String time;
    private String location;

    public TennisChallenge(int challengeID, TennisUser opponent, String date, String time) {
        this.challengeID = challengeID;
        this.opponent = opponent;
        this.date = date;
        this.time = time;
    }

    public int getChallengeID() {
        return challengeID;
    }

    public TennisUser getOpponent() {
        return opponent;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getLocation() {
        return location;
    }
}
