package com.example.myapplication;

/**
 * Holds the URL addresses for API communication.
 */
public class API_URL {

    /* Base API URL */
    private static final String BASE_URL = "http://192.168.1.11/Android/v3/tennisapi.php?tennisapi=";

    /* User signup and login URL's. */
    public static final String URL_CREATE_PLAYER = BASE_URL + "create_player";
    public static final String URL_LOGIN = BASE_URL + "login";

    /* Challenge related URL's. */
    public static final String URL_GET_CHALLENGES = BASE_URL + "get_challenges&playerid=";
    public static final String URL_GET_LADDER_DATA = BASE_URL + "get_ladder_profile_data";
    public static final String URL_GET_MATCH_HISTORY = BASE_URL + "get_match_history&playerid=";
    public static final String URL_CREATE_CHALLENGE = BASE_URL + "create_challenge";
    public static final String URL_CREATE_PLAYER_CHALLENGE = BASE_URL + "create_player_challenge";
    public static final String URL_ACCEPT_CHALLENGE = BASE_URL + "accept_challenge&challengeid=";
    public static final String URL_CANCEL_CHALLENGE = BASE_URL + "cancel_challenge&challengeid=";
    public static final String URL_POST_RESULT = BASE_URL + "post_result";

    /* Misc */
    public static final String URL_POST_ACHIEVEMENTS = BASE_URL + "post_achievements";
    public static final String URL_GET_CLUBS = BASE_URL + "get_clubs";
}
