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

    private EditText txtEmail, txtPassword;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = (ViewGroup) layoutInflater.inflate(R.layout.fragment_login, container, false);
        txtEmail = view.findViewById(R.id.txtEditEmailLogin);
        txtPassword = view.findViewById(R.id.txtEditPasswordLogin);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(v -> {
            login();
        });
        return view;
    }

    protected void login() {
        String email = txtEmail.getText().toString().trim();
        String password = txtPassword.getText().toString().trim();
        HashMap<String, String> params = new HashMap<>();
        params.put("email", email);
        params.put("password", password);
        LoginRequest req = new LoginRequest(params);
        req.execute();
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
            try {
                JSONObject object = new JSONObject(s);
                Toast.makeText(getActivity().getApplicationContext(), object.getString("message"), Toast.LENGTH_SHORT).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            RequestHandler requestHandler = new RequestHandler();
            return requestHandler.sendPostRequest(API_URL.URL_LOGIN, params);
        }
    }
}
