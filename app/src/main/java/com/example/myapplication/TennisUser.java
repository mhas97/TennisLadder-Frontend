package com.example.myapplication;

public class TennisUser {

    private int playerid;
    private String email;
    private String password;
    private String contactno;
    private String fname;
    private String lname;
    private int clubid;
    private int elo;
    private int hotstreak;

    public TennisUser(String fname, String lname, int elo, int hotstreak) {
        this.fname = fname;
        this.lname = lname;
        this.elo = elo;
        this.hotstreak = hotstreak;
    }

    public TennisUser(int playerid, String email, String password, String contactno, String fname, String lname, int clubid, int elo) {
        this.playerid = playerid;
        this.email = email;
        this.password = password;
        this.contactno = contactno;
        this.fname = fname;
        this.lname = lname;
        this.clubid = clubid;
        this.elo = elo;
    }

    public int getPlayerid() {
        return playerid;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getContactno() {
        return contactno;
    }

    public String getFname() {
        return fname;
    }

    public String getLname() {
        return lname;
    }

    public int getClubid() {
        return clubid;
    }

    public int getElo() {
        return elo;
    }

    public int getHotstreak() { return hotstreak; }
}
