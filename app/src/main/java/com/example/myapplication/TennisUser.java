package com.example.myapplication;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class implements the Parcelable interface, allowing user objects
 * to be passed between pages. This is better practice than having static
 * global variables and is useful for displaying user info and creating
 * challenges.
 */
public class TennisUser implements Parcelable {

    private int playerID;
    private String email;
    private String contactno;
    private String fname;
    private String lname;
    private int clubID;
    private int elo;
    private int hotstreak;

    // For fetching and displaying ladder data
    public TennisUser(int playerID, String fname, String lname, int clubID, int elo, int hotstreak) {
        this.playerID = playerID;
        this.fname = fname;
        this.lname = lname;
        this.clubID = clubID;
        this.elo = elo;
        this.hotstreak = hotstreak;
    }

    // For storing a users info upon signing in
    public TennisUser(int playerID, String email, String contactno, String fname, String lname, int clubID, int elo) {
        this.playerID = playerID;
        this.email = email;
        this.contactno = contactno;
        this.fname = fname;
        this.lname = lname;
        this.clubID = clubID;
        this.elo = elo;
    }

    protected TennisUser(Parcel in) {
        playerID = in.readInt();
        email = in.readString();
        contactno = in.readString();
        fname = in.readString();
        lname = in.readString();
        clubID = in.readInt();
        elo = in.readInt();
    }

    public int getplayerID() { return playerID; }

    public String getEmail() {
        return email;
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

    public int getClubID() {
        return clubID;
    }

    public int getElo() {
        return elo;
    }

    public int getHotstreak() { return hotstreak; }

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
        dest.writeString(contactno);
        dest.writeString(fname);
        dest.writeString(lname);
        dest.writeInt(clubID);
        dest.writeInt(elo);
    }
}
