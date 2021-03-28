package com.example.myapplication;

import java.util.HashMap;

/**
 * A singleton class used to hold a the global list of achievements. This list is generated
 * on login by first making a network request to fetch a full list of achievement data. This
 * is used to generate a hashmap which maps achievementID:[name, description], which is used
 * for displaying achievements on user profiles.
 */
public class GlobalAchievements {

    private static GlobalAchievements mInstance = null;
    private HashMap<String, String[]> achievementList;

    protected GlobalAchievements() { }

    public static synchronized GlobalAchievements getInstance() {
        if(null == mInstance){
            mInstance = new GlobalAchievements();
        }
        return mInstance;
    }

    public HashMap<String, String[]> getAchievementList() { return achievementList; }

    public void setAchievementList(HashMap<String, String[]> achievementList) { this.achievementList = achievementList; }
}
