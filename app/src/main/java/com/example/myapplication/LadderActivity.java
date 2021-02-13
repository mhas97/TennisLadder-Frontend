package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class LadderActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ladder);
        getLadderData();
    }

    public void getLadderData() {
        NetworkRequest req = new NetworkRequest(this, API.URL_GET_LADDER_DATA, null, API.REQ_TYPE_GET);
        req.execute();
    }
}