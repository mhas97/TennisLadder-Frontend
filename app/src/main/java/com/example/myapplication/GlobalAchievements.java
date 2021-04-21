package com.example.myapplication;

import java.util.HashMap;

/**
 * Implements a Singleton pattern to hold a the global list of achievements. The achievement list
 * data is feteched on login via a network request. This data is used to generate a hashmap mapping
 * achievementID:[name, description], which is used for displaying achievements on user profiles.
 */
public class GlobalAchievements {

    private static GlobalAchievements mInstance = null;
    private HashMap<String, String[]> achievementList;

    protected GlobalAchievements() { }

    public static synchronized GlobalAchievements getInstance() {
        if (null == mInstance) {
            mInstance = new GlobalAchievements();
        }
        return mInstance;
    }

    public HashMap<String, String[]> getAchievementList() { return achievementList; }

    public void setAchievementList(HashMap<String, String[]> achievementList) { this.achievementList = achievementList; }
}
