package com.example.myapplication;

public class TennisChallenge {
    private int challengeID;
    private TennisUser opponent;
    private String date;
    private String time;
    private String location;
    private String score;
    private int didWin;
    private int didInitiate;

    public TennisChallenge(int challengeID, TennisUser opponent, String date, String time, String location, String score, int didWin) {
        this.challengeID = challengeID;
        this.opponent = opponent;
        this.date = date;
        this.time = time;
        this.location = location;
        this.score = score;
        this.didWin = didWin;
    }

    public TennisChallenge(int challengeID, TennisUser opponent, String date, String time, String location, int didInitiate) {
        this.challengeID = challengeID;
        this.opponent = opponent;
        this.date = date;
        this.time = time;
        this.location = location;
        this.didInitiate = didInitiate;
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

    public String getScore() {
        return score;
    }

    public int getDidWin() {
        return didWin;
    }

    public int getDidInitiate() { return didInitiate; }
}
