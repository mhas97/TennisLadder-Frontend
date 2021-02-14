package com.example.myapplication;

// [REF: https://www.simplifiedcoding.net/android-mysql-tutorial-to-perform-basic-crud-operation/#Android-MySQL-Tutorial]

public class API {
    private static final String ROOT_URL = "http://172.23.166.182/Android/v3/tennisapi.php?tennisapi=";
    public static final String URL_LOGIN = ROOT_URL + "login";
    public static final String URL_CREATE_PLAYER = ROOT_URL + "create_player";
    public static final String URL_GET_PLAYER_DATA = ROOT_URL + "get_player_data&id=";
    public static final String URL_GET_LADDER_DATA = ROOT_URL + "get_ladder_data";
    public static final String URL_UPDATE_PLAYER = ROOT_URL + "update_player";
    public static final String URL_DELETE_PLAYER = ROOT_URL + "delete_player&id=";

    public static final int REQ_TYPE_GET = 26997;
    public static final int REQ_TYPE_POST = 15937;
}
