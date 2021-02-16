package com.example.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginFragment extends Fragment {

    EditText emailInput, passwordInput;
    Button buttonLogin;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (ViewGroup) layoutInflater.inflate(R.layout.fragment_login, container, false);
        buttonLogin = view.findViewById(R.id.button_login);
        emailInput = view.findViewById(R.id.email_input_login);
        passwordInput = view.findViewById(R.id.password_input_login);
        buttonLogin.setOnClickListener(v -> {

            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            HashMap<String, String> params = new HashMap<>();
            params.put("email", email);
            params.put("password", password);

            LoginRequest req = new LoginRequest(params);
            req.execute();

        });
        return view;
    }

    private class LoginRequest extends AsyncTask<Void, Void, String> {
        HashMap<String, String> params;

        LoginRequest(HashMap<String, String> params) {
            this.params = params;
        }

        @Override
        protected void onPreExecute() { }

        @Override
        protected void onPostExecute(String s)
        {
            try
            {
                JSONObject object = new JSONObject(s);
                Toast.makeText(getActivity().getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids)
        {
            RequestHandler rqh = new RequestHandler();
            return rqh.sendPostRequest(API.URL_LOGIN, params);
        }
    }
}
