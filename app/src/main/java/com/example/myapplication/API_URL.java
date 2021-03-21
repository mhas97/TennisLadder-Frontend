package com.example.myapplication;

// [REF: https://www.simplifiedcoding.net/android-mysql-tutorial-to-perform-basic-crud-operation/#Android-MySQL-Tutorial]

public class API_URL {
    private static final String ROOT_URL = "http://192.168.1.11/Android/v3/tennisapi.php?tennisapi=";
    public static final String URL_LOGIN = ROOT_URL + "login";
    public static final String URL_CREATE_PLAYER = ROOT_URL + "create_player";
    public static final String URL_GET_LADDER_DATA = ROOT_URL + "get_ladder_data";
    public static final String URL_CREATE_CHALLENGE = ROOT_URL + "create_challenge";
    public static final String URL_CREATE_PLAYER_CHALLENGE = ROOT_URL + "create_player_challenge";
    public static final String URL_GET_CHALLENGES = ROOT_URL + "get_challenges&playerid=";
    public static final String URL_GET_MATCH_HISTORY = ROOT_URL + "get_match_history&playerid=";
    public static final String URL_GET_CLUBS = ROOT_URL + "get_clubs";
    public static final String URL_ACCEPT_CHALLENGE = ROOT_URL + "accept_challenge&challengeid=";
    public static final String URL_CANCEL_CHALLENGE = ROOT_URL + "cancel_challenge&challengeid=";
}
