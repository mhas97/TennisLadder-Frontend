package com.example.myapplication;

public class TennisChallenge {
    private int challengeID;
    private TennisUser opponent;
    private String date;
    private String location;

    public TennisChallenge(int challengeID, TennisUser opponent, String date, String location) {
        this.challengeID = challengeID;
        this.opponent = opponent;
        this.date = date;
        this.location = location;
    }
}
