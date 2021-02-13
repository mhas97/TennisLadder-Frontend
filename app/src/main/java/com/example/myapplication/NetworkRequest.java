package com.example.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;

public class NetworkRequest extends AsyncTask<Void, Void, String> {
    String url;
    HashMap<String, String> params;
    int requestCode;
    private Context context;

    String HELLOOOOOO;
    wat2

    NetworkRequest(Context context, String url, HashMap<String, String> params, int requestCode)
    {
        this.context = context;
        this.url = url;
        this.params = params;
        this.requestCode = requestCode;
    }

    @Override
    protected void onPreExecute() { }

    @Override
    protected void onPostExecute(String s)
    {
        try
        {
            JSONObject object = new JSONObject(s);
            if (!object.getBoolean("error"))
            {
                Toast.makeText(context.getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected String doInBackground(Void... voids)
    {
        RequestHandler rqh = new RequestHandler();
        if (requestCode == API.REQ_TYPE_POST)
        {
            return rqh.sendPostRequest(url, params);
        }
        if (requestCode == API.REQ_TYPE_GET)
        {
            return rqh.sendGetRequest(url);
        }
        return null;
    }
}
